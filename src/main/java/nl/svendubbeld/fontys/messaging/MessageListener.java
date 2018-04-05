package nl.svendubbeld.fontys.messaging;

import java.io.IOException;

@FunctionalInterface
public interface MessageListener<T> {

    void onMessageReceived(T message) throws IOException;
}
