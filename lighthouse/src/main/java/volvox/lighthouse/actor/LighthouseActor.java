package volvox.lighthouse.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class LighthouseActor extends AbstractActor {
    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {

        return Props.create(LighthouseActor.class);
    }

    private LighthouseActor() {
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        logger.warning("Started!");
    }

    @Override
    public void postStop() throws Exception {
        logger.warning("Stopped!");

        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
