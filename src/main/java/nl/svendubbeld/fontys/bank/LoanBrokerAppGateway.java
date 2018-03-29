package nl.svendubbeld.fontys.bank;

import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.messaging.MessageListener;
import nl.svendubbeld.fontys.messaging.MessageReceiverGateway;
import nl.svendubbeld.fontys.messaging.MessageSenderGateway;
import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;

import java.io.Closeable;
import java.io.IOException;

public class LoanBrokerAppGateway implements Closeable {

    private final MessageSenderGateway<BankInterestReply> senderGateway;

    private final MessageReceiverGateway<BankInterestRequest> receiverGateway;

    public LoanBrokerAppGateway() {
        senderGateway = new MessageSenderGateway<>(Queues.BANK_INTEREST_REPLY);
        receiverGateway = new MessageReceiverGateway<>(Queues.BANK_INTEREST_REQUEST);
    }

    public void sendReply(BankInterestReply reply) throws IOException {
        senderGateway.send(reply);
    }

    public void onBankInterestRequestReceived(MessageListener<BankInterestRequest> messageListener) throws IOException {
        receiverGateway.setListener(messageListener);
    }

    @Override
    public void close() throws IOException {
        senderGateway.close();
        receiverGateway.close();
    }
}
