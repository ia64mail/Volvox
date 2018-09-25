package volvox.stub;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import volvox.common.utils.ActorNameUtils;
import volvox.stub.actor.StubActor;

public class Stub {
    private static Logger logger = LoggerFactory.getLogger(Stub.class);

    public static void main(String[] args) {
        var utils = new ActorNameUtils();
        final ActorSystem system = ActorSystem.create(utils.toLowerCase("volvox"));
        try {
            logger.info("Akka stared successfully ...");

            final ActorRef stubActor = system.actorOf(StubActor.props(), "stub");

        } catch (Exception ioe) {
            logger.error(ioe.toString());
            system.terminate();
        }
    }
}
