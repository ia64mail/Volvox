package volvox.lighthouse;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import volvox.lighthouse.actor.LighthouseActor;

/**
 * Lighthouse actor responsible for joining to the cluster.
 */
public class Lighthouse {
    private static Logger logger = LoggerFactory.getLogger(Lighthouse.class);

    private static final String akkaNameConfig = "akka.name";

    public static void main(String[] args) {
        final var config = ConfigFactory.load();
        final ActorSystem system = ActorSystem.create(config.getString(akkaNameConfig));

        try {
            logger.info("Akka stared successfully ...");

            final ActorRef lighthouseActor = system.actorOf(LighthouseActor.props(), "lighthouse");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
