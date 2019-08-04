package com.github.daggerok.akka.stash;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Value(staticConstructor = "of")
final class User implements Serializable {
    private final String name;
    private final int age;
}

interface Messages extends Serializable { }
final class Connect implements Messages { }
final class Disconnect implements Messages { }

interface DbOperations extends Messages { }

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
class UserRepositoryActor extends AbstractActorWithStash {

    static Props props() {
        return Props.create(UserRepositoryActor.class);
    }

    @Override
    public Receive createReceive() {
        return disconnected();
    }

    private Receive disconnected() {
        return receiveBuilder()
                .match(Connect.class, connect -> {
                    log.info("connected!");
                    unstashAll();
                    getContext().become(connected());
                })
                .matchAny(o -> {
                    log.info("received a message when disconnected!");
                    stash();
                })
                .build();
    }

    private Receive connected() {
        return receiveBuilder()
                .match(Disconnect.class, disconnect -> {
                    log.info("disconnect!");
                    getContext().unbecome();
                })
                .match(DbOperations.class, op -> {
                    log.info("received: {}", op);
                })
                .matchAny(o -> {
                    log.error("received unknown: {}", o);
                    unhandled(o);
                })
                .build();
    }
}

public class AkkaStash {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("stashAkka");
        ActorRef userRepositoryActor = system.actorOf(UserRepositoryActor.props(), "userRepositoryActor");
        ActorRef sender = ActorRef.noSender();

        userRepositoryActor.tell(Save.of(User.of("max", 123)), sender);
        userRepositoryActor.tell(new Connect(), sender);
        userRepositoryActor.tell(Save.of(User.of("bob", 321)), sender);
        userRepositoryActor.tell(Delete.of(User.of("bob", 321)), sender);
        userRepositoryActor.tell(new Disconnect(), sender);

        system.terminate();
    }
}
