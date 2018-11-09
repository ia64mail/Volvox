package volvox.messenger.server;

public class Outgoing {
    private String messageText;

    public Outgoing(Incoming incoming) {
        this.messageText = incoming.getMessageText();
    }

    public String getMessageText() {
        return messageText;
    }

    @Override
    public String toString() {
        return "Outgoing::" + getMessageText();
    }

}
