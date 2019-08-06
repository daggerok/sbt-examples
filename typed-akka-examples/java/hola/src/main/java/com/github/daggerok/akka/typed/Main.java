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
        val childRef = context.spawn(Behaviors.empty(), "2nd-actor");
        log.info("childRef: {}", childRef);
        return this;
    }
}

@Slf4j
public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Behavior<String> startingBehavior = Behaviors.setup(context -> {
            val parentRef = context.spawn(ChildActor.behavior.get(), "1st-actor");
            return Behaviors.receiveMessage(message -> {
                log.info("{} parentRef: {}", message, parentRef);
                parentRef.tell("print");
                return Behaviors.same();
            });
        });
        ActorSystem<String> system = ActorSystem.create(startingBehavior, "typedJavaSystem");
        // Thread.sleep(123);
        system.tell("start");
        system.terminate();
    }
}
