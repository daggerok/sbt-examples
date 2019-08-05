package com.github.daggerok.akka.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import io.vavr.API;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigInteger;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

interface Operation {
    BigInteger getBy();
}

@Value(staticConstructor = "of")
class Increment implements Operation, Serializable {
    private static final long serialVersionUID = 1L;
    private final BigInteger by;
}

@Value(staticConstructor = "of")
class Decrement implements Operation, Serializable {
    private static final long serialVersionUID = 1L;
    private final BigInteger by;
}

@Value(staticConstructor = "of")
class MyState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final BigInteger counter;

    public MyState copy() {
        return MyState.of(counter);
    }
}

@Value(staticConstructor = "of")
class Cmd implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Operation operation;
}

interface Event {
    Operation getOperation();
}

@Value(staticConstructor = "of")
class Evt implements Event, Serializable {
    private static final long serialVersionUID = 1L;
    private final Operation operation;
}

// etc...

@Slf4j
class CounterPersistentActor extends AbstractPersistentActor/* AbstractPersistentActorWithAtLeastOnceDelivery*/ {

    static Props props() {
        return Props.create(CounterPersistentActor.class);
    }

    private final int snapShotInterval = 11;
    private MyState myState = MyState.of(BigInteger.ZERO);

    public CounterPersistentActor() {
        log.info("starting... {}", myState);
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(Event.class, this::updateState)
                .match(SnapshotOffer.class, ss -> {
                    MyState prev = this.myState;
                    this.myState = (MyState) ss.snapshot();
                    log.info("recovered: {} -> {} from snapshot: {}", prev, myState, ss);
                })
                // .matchAny(this::unknown)
                .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Cmd.class, cmd -> {
                    Operation operation = cmd.getOperation();
                    Evt evt = Evt.of(operation);
                    persist(evt, (Event event) -> {
                        // update state after new event committed:
                        updateState(event);
                        getContext().system().eventStream().publish(event);
                        // save snapshot if needed:
                        long lastNr = lastSequenceNr();
                        if (lastNr % snapShotInterval == 0 && lastNr != 0)
                            saveSnapshot(myState.copy());
                    });
                })
                .match(SaveSnapshotSuccess.class, saveSnapshotSuccess -> {
                    log.info("snapshot saved: {} for state: {}", saveSnapshotSuccess, myState);
                })
                .matchAny(o -> {
                    log.info("current state is: {} on: {}", myState, o);
                    // sender().tell(myState, self());
                })
                .build();
    }

    @Override
    public String persistenceId() {
        return "global-counter";
    }

    private void updateState(Event event) {
        MyState prev = this.myState.copy();
        this.myState = API.Match(event.getOperation()).of(
                Case($(instanceOf(Increment.class)),
                     op -> MyState.of(this.myState.getCounter().add(op.getBy()))),
                Case($(instanceOf(Decrement.class)),
                     op -> MyState.of(this.myState.getCounter().subtract(op.getBy())))/*,
                Case($(), () -> MyState.of(this.myState.getCounter()))*/ // just fail...
        );
        log.info("update state: {} -> {} on: {}", prev, myState, event.getOperation());
    }
}

public class AkkaPersistence {

    @SneakyThrows
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("AkkaPersistence");
        ActorRef ref = system.actorOf(CounterPersistentActor.props(), "counterPersistentActor");
        ActorRef noSender = ActorRef.noSender();

        for (int i = 0; i < 5; i++) {
            ref.tell(Cmd.of(Increment.of(BigInteger.valueOf(1))), noSender);
            ref.tell(Cmd.of(Decrement.of(BigInteger.valueOf(0))), noSender);
            ref.tell(Cmd.of(Increment.of(BigInteger.valueOf(2))), noSender);
            ref.tell(Cmd.of(Decrement.of(BigInteger.valueOf(1))), noSender);
            ref.tell("print", noSender);
            Thread.sleep(567);
        }
        system.terminate();
    }
}
