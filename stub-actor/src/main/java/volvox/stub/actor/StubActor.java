package volvox.stub.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import volvox.model.BounceSignal;

import java.time.Duration;

public class StubActor extends AbstractActorWithTimers {
    private final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    private static String TIMER_KEY = "TIMER_KEY";

    private ActorRef router;

    public static Props props() {
        return Props.create(StubActor.class);
    }

    private StubActor() {
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        logger.warning("Started!");

        router = getContext().actorOf(
                FromConfig.getInstance().props(Props.empty()), "router");

        router.tell(new BounceSignal(), self());
    }

    @Override
    public void postStop() throws Exception {
        logger.warning("Stopped!");

        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return readyForBounceState();
    }

    private Receive readyForBounceState() {
        return receiveBuilder()
                .match(BounceSignal.class, this::onReceiveBounceSignal)
                .build();
    }

    private Receive busyState() {
        return receiveBuilder()
                .match(BounceSignal.class, this::onReSendBounceSignal)
                .build();
    }

    private void onReceiveBounceSignal(BounceSignal bounceSignal) {
        logger.info("[IN ] {}", bounceSignal);

        var newBounceSignal = new BounceSignal(bounceSignal);
        getTimers().startSingleTimer(TIMER_KEY, newBounceSignal, Duration.ofSeconds(2));

        getContext().become(busyState());
    }

    private void onReSendBounceSignal(BounceSignal bounceSignal) {
        logger.info("[OUT] {}", bounceSignal);

        router.tell(bounceSignal, self());

        getContext().become(readyForBounceState());
    }
}