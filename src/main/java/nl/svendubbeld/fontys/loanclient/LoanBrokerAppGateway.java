package nl.svendubbeld.fontys.loanclient;

import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.messaging.MessageListener;
import nl.svendubbeld.fontys.messaging.MessageReceiverGateway;
import nl.svendubbeld.fontys.messaging.MessageSenderGateway;
import nl.svendubbeld.fontys.model.loan.LoanReply;
import nl.svendubbeld.fontys.model.loan.LoanRequest;

import java.io.Closeable;
import java.io.IOException;

public class LoanBrokerAppGateway implements Closeable {

    private final MessageSenderGateway<LoanRequest> senderGateway;

    private final MessageReceiverGateway<LoanReply> receiverGateway;

    public LoanBrokerAppGateway() {
        senderGateway = new MessageSenderGateway<>(Queues.LOAN_REQUEST);
        receiverGateway = new MessageReceiverGateway<>(Queues.LOAN_REPLY);
    }

    public void applyForLoan(LoanRequest request) throws IOException {
        senderGateway.send(request);
    }

    public void onLoanReplyReceived(MessageListener<LoanReply> messageListener) throws IOException {
        receiverGateway.setListener(messageListener);
    }

    @Override
    public void close() throws IOException {
        senderGateway.close();
        receiverGateway.close();
    }

}
