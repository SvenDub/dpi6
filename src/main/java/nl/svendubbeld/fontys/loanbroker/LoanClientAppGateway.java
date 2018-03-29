package nl.svendubbeld.fontys.loanbroker;

import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.messaging.MessageListener;
import nl.svendubbeld.fontys.messaging.MessageReceiverGateway;
import nl.svendubbeld.fontys.messaging.MessageSenderGateway;
import nl.svendubbeld.fontys.model.loan.LoanReply;
import nl.svendubbeld.fontys.model.loan.LoanRequest;

import java.io.Closeable;
import java.io.IOException;

public class LoanClientAppGateway implements Closeable {

    private final MessageSenderGateway<LoanReply> senderGateway;

    private final MessageReceiverGateway<LoanRequest> receiverGateway;

    public LoanClientAppGateway() {
        senderGateway = new MessageSenderGateway<>(Queues.LOAN_REPLY);
        receiverGateway = new MessageReceiverGateway<>(Queues.LOAN_REQUEST);
    }

    public void sendLoanReply(LoanReply reply) throws IOException {
        senderGateway.send(reply);
    }

    public void onLoanRequestReceived(MessageListener<LoanRequest> messageListener) throws IOException {
        receiverGateway.setListener(messageListener);
    }

    @Override
    public void close() throws IOException {
        senderGateway.close();
        receiverGateway.close();
    }
}
