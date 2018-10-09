package volvox.model;

public class BounceSignal implements IMessage {
    private final int count;

    public BounceSignal() {
        this.count = 0;
    }

    public BounceSignal(BounceSignal signal) {
        this.count = signal.count + 1;
    }

    @Override
    public String toString() {
        return String.format("%1$s#%2$x{count:%3$s}", this.getClass().getName(), this.hashCode(), this.count);
    }
}
