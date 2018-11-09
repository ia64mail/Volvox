package volvox.messenger.server;

import akka.actor.ActorRef;

public class OutgoingDestination {
    private ActorRef destination;

    public OutgoingDestination(ActorRef destination) {
        this.destination = destination;
    }

    public ActorRef getDestinationRef() {
        return destination;
    }

    @Override
    public String toString() {
        return "OutgoingDestination::" + destination.toString();
    }
}
