package nl.svendubbeld.fontys.messaging;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

public class MessageReceiverGateway<T extends Serializable> implements Closeable {

    private Connection connection;
    private Channel channel;

    private final String queue;

    public MessageReceiverGateway(String queue) {
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

    public void setListener(MessageListener<T> messageListener) throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                messageListener.onMessageReceived(SerializationUtils.deserialize(body));
            }
        };

        channel.basicConsume(queue, true, consumer);
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
