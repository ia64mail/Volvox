package volvox.lighthouse;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import volvox.lighthouse.actor.LighthouseActor;
import volvox.common.utils.*;

/**
 * Lighthouse actor responsible for joining to the cluster.
 */
public class Lighthouse {
    private static LoggingAdapter logger;

    public static void main(String[] args) {
        var utils = new ActorNameUtils();
        final ActorSystem system = ActorSystem.create(utils.toLowerCase("volvox"));
        logger= Logging.getLogger(system, "main");
        try {
            logger.warning("Akka stared successfully ...");

            final ActorRef lighthouseActor = system.actorOf(LighthouseActor.props(), "lighthouse");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
