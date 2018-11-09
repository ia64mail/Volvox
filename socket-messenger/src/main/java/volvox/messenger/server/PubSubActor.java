package volvox.messenger.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class PubSubActor extends AbstractActor {
    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    private OutgoingDestination destination;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Incoming.class, this::onReceiveIncoming)
                .match(Outgoing.class, this::onReceiveOutgoing)
                .match(OutgoingDestination.class, this::onReceiveOutgoingDestination)
                .build();
    }

    private void onReceiveIncoming(Incoming incoming) {
        logger.info("[Incoming] {}", incoming);

        //reply
        if (destination != null) {
            destination.getDestinationRef().tell(new Outgoing(incoming), ActorRef.noSender());
        }
    }

    private void onReceiveOutgoing(Outgoing outgoing) {
        logger.info("[Outgoing] {}", outgoing);
    }

    private void onReceiveOutgoingDestination(OutgoingDestination outgoingDestination) {
        destination = outgoingDestination;
        logger.info("[OutgoingDestination] {}", outgoingDestination);
    }

}
