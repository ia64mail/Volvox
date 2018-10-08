package volvox.lighthouse;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.management.AkkaManagement;
import akka.management.cluster.bootstrap.ClusterBootstrap;
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

        //See https://github.com/akka/akka-management/issues/282
        if (system.settings().config().getStringList("akka.cluster.seed-nodes").size() == 0) {
            // Akka Management hosts the HTTP routes used by bootstrap
            AkkaManagement.get(system).start();
            // Starting the bootstrap process needs to be done explicitly
            ClusterBootstrap.get(system).start();
        }

        logger = Logging.getLogger(system, "main");
        try {
            logger.warning("Akka stared successfully ...");

            final ActorRef lighthouseActor = system.actorOf(LighthouseActor.props(), "lighthouse");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
