package com.github.daggerok.akka.fsm;

import akka.actor.AbstractFSMWithStash;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import static com.github.daggerok.akka.fsm.Data.Empty;
import static com.github.daggerok.akka.fsm.Network.Connect;
import static com.github.daggerok.akka.fsm.Network.Disconnect;
import static com.github.daggerok.akka.fsm.State.Connected;
import static com.github.daggerok.akka.fsm.State.Disconnected;

// fsm:

enum State {
    Connected,
    Disconnected
}

enum Data {
    Empty
}

// old...

@Value(staticConstructor = "of")
final class User {
    private final String name;
    private final int age;
}

enum Network {
    Connect,
    Disconnect
}

interface DbOperations { }

@Value(staticConstructor = "of")
final class Save implements DbOperations {
    private final User user;
}

@Value(staticConstructor = "of")
final class Delete implements DbOperations {
    private final User user;
}

// etc...

@Slf4j
class FiniteStateMachineUserRepositoryActor extends AbstractFSMWithStash<State, Data> {

    static Props props() {
        return Props.create(FiniteStateMachineUserRepositoryActor.class);
    }

    @Override
    public void postStop() {
        // hz, but it's requires for success compilation...
    }

    {
        startWith(Disconnected, Empty);

        when(Disconnected,
             matchEventEquals(Connect, (state, data) -> {
                 log.info("connected!");
                 unstashAll();
                 return goTo(Connected)/*.using(Empty)*/;
             })
                     .anyEvent((o, data) -> {
                         log.info("received a message when disconnected!");
                         stash();
                         return stay();
                     }));

        /* // optionals:
        onTransition(
                matchState(Connected, Disconnected, (c, d) -> {
                    log.info("on transition: {} -> {}", c, d);
                })
                .state(Disconnected, Connected, (d, c) -> {
                    log.info("on transition: {} -> {}", d, c);
                })
                .build());
        */

        when(Connected,
             matchEventEquals(Disconnect, (state, data) -> {
                 log.info("disconnect!");
                 getContext().unbecome();
                 return goTo(Disconnected)/*.using(Empty)*/;
             })
                     .event(DbOperations.class, (dbOperations, data) -> {
                         log.info("received: {}", dbOperations);
                         return stay();
                     })
                     .build());

        whenUnhandled(
                matchAnyEvent((o, data) -> {
                    log.error("received unknown: {} {} on {}", o, data, stateName());
                    unhandled(o);
                    return stay()/*.using(Empty)*/;
                })
                        .build());

        initialize();
    }

    // @Override
    // public Receive createReceive() {
    //     return disconnected();
    // }
    //
    // private Receive disconnected() {
    //     return receiveBuilder()
    //             .matchEquals(Connect, network -> {
    //                 log.info("connected!");
    //                 unstashAll();
    //                 getContext().become(connected());
    //             })
    //             .matchAny(o -> {
    //                 log.info("received a message when disconnected!");
    //                 stash();
    //             })
    //             .build();
    // }
    //
    // private Receive connected() {
    //     return receiveBuilder()
    //             .matchEquals(Disconnect, network -> {
    //                 log.info("disconnect!");
    //                 getContext().unbecome();
    //             })
    //             .match(DbOperations.class, op -> {
    //                 log.info("received: {}", op);
    //             })
    //             .matchAny(o -> {
    //                 log.error("received unknown: {}", o);
    //                 unhandled(o);
    //             })
    //             .build();
    // }
}

public class AkkaFSM {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("stashAkka");
        ActorRef userRepositoryActor = system.actorOf(FiniteStateMachineUserRepositoryActor.props(),
                                                      "finiteStateMachineUserRepositoryActor");
        ActorRef sender = ActorRef.noSender();

        userRepositoryActor.tell(Save.of(User.of("max", 123)), sender);
        userRepositoryActor.tell(Connect, sender);
        userRepositoryActor.tell(Save.of(User.of("bob", 321)), sender);
        userRepositoryActor.tell(Delete.of(User.of("bob", 321)), sender);
        userRepositoryActor.tell(Disconnect, sender);

        system.terminate();
    }
}
