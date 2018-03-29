package nl.svendubbeld.fontys.loanbroker;

import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.messaging.MessageListener;
import nl.svendubbeld.fontys.messaging.MessageReceiverGateway;
import nl.svendubbeld.fontys.messaging.MessageSenderGateway;
import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;

import java.io.Closeable;
import java.io.IOException;

public class BankAppGateway implements Closeable {

    private final MessageSenderGateway<BankInterestRequest> senderGateway;

    private final MessageReceiverGateway<BankInterestReply> receiverGateway;

    public BankAppGateway() {
        senderGateway = new MessageSenderGateway<>(Queues.BANK_INTEREST_REQUEST);
        receiverGateway = new MessageReceiverGateway<>(Queues.BANK_INTEREST_REPLY);
    }

    public void sendBankRequest(BankInterestRequest request) throws IOException {
        senderGateway.send(request);
    }

    public void onBankReplyReceived(MessageListener<BankInterestReply> messageListener) throws IOException {
        receiverGateway.setListener(messageListener);
    }

    @Override
    public void close() throws IOException {
        senderGateway.close();
        receiverGateway.close();
    }
}
