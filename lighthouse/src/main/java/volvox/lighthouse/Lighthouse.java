package volvox.lighthouse;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import volvox.lighthouse.actor.LighthouseActor;
import volvox.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lighthouse actor responsible for joining to the cluster.
 */
public class Lighthouse {
    private static Logger logger = LoggerFactory.getLogger(Lighthouse.class);

    public static void main(String[] args) {
        var utils = new ActorNameUtils();
        final ActorSystem system = ActorSystem.create(utils.toLowerCase("volvox"));
        try {
            logger.info("Akka stared successfully ...");

            final ActorRef lighthouseActor = system.actorOf(LighthouseActor.props(), "lighthouse");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
