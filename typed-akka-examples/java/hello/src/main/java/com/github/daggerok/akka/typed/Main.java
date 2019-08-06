package com.github.daggerok.akka.typed;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
class ChildActor extends AbstractBehavior<String> {

    public static Supplier<Behavior<String>> behavior = () -> Behaviors.setup(ChildActor::new);

    private final ActorContext<String> context;

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("print", this::print)
                .build();
    }

    private Behavior<String> print() {
        val secondRef = context.spawn(Behaviors.empty(), "2nd-actor");
        log.info("Second: {}", secondRef);
        return this;
    }
}

@Slf4j
@RequiredArgsConstructor
class ParentActor extends AbstractBehavior<String> {

    public static Supplier<Behavior<String>> behavior = () -> Behaviors.setup(ParentActor::new);

    private final ActorContext<String> context;

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("start", this::start)
                .build();
    }

    private Behavior<String> start() {
        val firstRef = context.spawn(ChildActor.behavior.get(), "1st-actor");
        log.info("First: {}", firstRef);
        firstRef.tell("print");
        return Behaviors.same();
    }
}

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        ActorSystem<String> system = ActorSystem.create(ParentActor.behavior.get(), "typedJavaSystem");
        system.tell("start");
        Thread.sleep(123);
        system.terminate();
    }
}
