package nl.svendubbeld.fontys.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

public class MessageSenderGateway<T extends Serializable> implements Closeable {

    private Connection connection;
    private Channel channel;

    private final String queue;

    public MessageSenderGateway(String queue) {
        this.queue = queue;

        var rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost("127.0.0.1");

        try {
            connection = rabbitFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queue, true, false, false, null);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void send(T message) throws IOException {
        send(SerializationUtils.serialize(message));
    }

    public void send(byte[] bytes) throws IOException {
        channel.basicPublish("", queue, null, bytes);
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
            connection.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
