package volvox.stub;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.management.AkkaManagement;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import volvox.stub.actor.StubActor;

public class Stub {
    private static Logger logger = LoggerFactory.getLogger(Stub.class);

    private static final String akkaNameConfig = "akka.name";

    public static void main(String[] args) {
        final var config = ConfigFactory.load();
        final ActorSystem system = ActorSystem.create(config.getString(akkaNameConfig));

        //See https://github.com/akka/akka-management/issues/282
        if (system.settings().config().getStringList("akka.cluster.seed-nodes").size() == 0) {
            // Akka Management hosts the HTTP routes used by bootstrap
            AkkaManagement.get(system).start();
            // Starting the bootstrap process needs to be done explicitly
            ClusterBootstrap.get(system).start();
        }

        try {
            logger.info("Akka stared successfully ...");

            final ActorRef stubActor = system.actorOf(StubActor.props(), "stub");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
