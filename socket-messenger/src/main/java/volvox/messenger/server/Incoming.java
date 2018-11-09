package volvox.messenger.server;

public class Incoming {
    private String messageText;

    public Incoming(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    @Override
    public String toString() {
        return "Incoming::" + getMessageText();
    }
}
