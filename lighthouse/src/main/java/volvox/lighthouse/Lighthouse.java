package volvox.lighthouse;

import akka.actor.ActorSystem;
import volvox.common.utils.*;

import java.io.IOException;

/**
 * Lighthouse actor responsible for joining to the cluster.
 */
public class Lighthouse {
    public static void main(String[] args) {
        var utils = new ActorNameUtils();
        final ActorSystem system = ActorSystem.create(utils.toLowerCase("helloakka"));
        try {
//            //#create-actors
//            final ActorRef printerActor =
//                    system.actorOf(Printer.props(), "printerActor");
//            final ActorRef howdyGreeter =
//                    system.actorOf(Greeter.props("Howdy", printerActor), "howdyGreeter");
//            final ActorRef helloGreeter =
//                    system.actorOf(Greeter.props("Hello", printerActor), "helloGreeter");
//            final ActorRef goodDayGreeter =
//                    system.actorOf(Greeter.props("Good day", printerActor), "goodDayGreeter");
//            //#create-actors
//
//            //#main-send-messages
//            howdyGreeter.tell(new WhoToGreet("Akka"), ActorRef.noSender());
//            howdyGreeter.tell(new Greet(), ActorRef.noSender());
//
//            howdyGreeter.tell(new WhoToGreet("Lightbend"), ActorRef.noSender());
//            howdyGreeter.tell(new Greet(), ActorRef.noSender());
//
//            helloGreeter.tell(new WhoToGreet("Java"), ActorRef.noSender());
//            helloGreeter.tell(new Greet(), ActorRef.noSender());
//
//            goodDayGreeter.tell(new WhoToGreet("Play"), ActorRef.noSender());
//            goodDayGreeter.tell(new Greet(), ActorRef.noSender());
//            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }

    }
}