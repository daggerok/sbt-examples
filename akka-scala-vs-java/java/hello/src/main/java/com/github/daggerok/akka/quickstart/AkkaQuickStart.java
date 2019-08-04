package com.github.daggerok.akka.quickstart;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Value(staticConstructor = "of")
final class WhoToGreet {
    private final String who;
}

final class Greet { }

@Slf4j
@RequiredArgsConstructor
class Greeter extends AbstractActor {

    private final ActorRef printer;

    static Props props(ActorRef printer) {
        return Props.create(Greeter.class, printer);
    }

    @Override
    public Receive createReceive() {
        return withWhomToGreet("");
    }

    private Receive withWhomToGreet(String name) {
        return receiveBuilder()
                .match(WhoToGreet.class, whoToGreet -> {
                    getContext().become(withWhomToGreet(whoToGreet.getWho()));
                })
                .match(Greet.class,
                       greet -> {
                           String msg = format("Hola, %s", name);
                           log.info("sending {} to {}", msg, printer);
                           printer.tell(Greeting.of(msg), self());
                       })
                .matchAny(o -> {
                    log.error("unexpected: {}", o);
                    unhandled(o);
                })
                .build();
    }
}

@Value(staticConstructor = "of")
final class Greeting {
    private final String greeting;
}

@Slf4j
class Printer extends AbstractActor {

    static Props props() {
        return Props.create(Printer.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Greeting.class,
                       greeting -> log.info("{}: {}", self(), greeting.getGreeting()))
                .matchAny(o -> {
                    log.error("unexpected: {}", o);
                    unhandled(o);
                })
                .build();
    }
}

public class AkkaQuickStart {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("helloAkka");
        ActorRef printer = system.actorOf(Printer.props(), "printer");

        ActorRef howdyGreeter = system.actorOf(Greeter.props(printer), "howdyGreeter");
        ActorRef helloGreeter = system.actorOf(Greeter.props(printer), "helloGreeter");
        ActorRef goodDayGreeter = system.actorOf(Greeter.props(printer), "goodDayGreeter");

        howdyGreeter.tell(WhoToGreet.of("Akka"), ActorRef.noSender());
        howdyGreeter.tell(new Greet(), ActorRef.noSender());

        howdyGreeter.tell(WhoToGreet.of("Lightbend"), ActorRef.noSender());
        howdyGreeter.tell(new Greet(), ActorRef.noSender());

        helloGreeter.tell(WhoToGreet.of("Java"), ActorRef.noSender());
        helloGreeter.tell(new Greet(), ActorRef.noSender());

        goodDayGreeter.tell(WhoToGreet.of("Play"), ActorRef.noSender());
        goodDayGreeter.tell(new Greet(), ActorRef.noSender());

        system.terminate();
    }
}
