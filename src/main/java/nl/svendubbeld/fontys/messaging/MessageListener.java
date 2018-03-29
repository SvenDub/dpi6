package nl.svendubbeld.fontys.messaging;

import java.io.IOException;

public interface MessageListener<T> {

    void onMessageReceived(T message) throws IOException;
}
