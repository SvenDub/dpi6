package nl.svendubbeld.fontys.loanbroker;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.Rules;
import nl.svendubbeld.fontys.messaging.MessageListener;
import nl.svendubbeld.fontys.messaging.MessageReceiverGateway;
import nl.svendubbeld.fontys.messaging.MessageSenderGateway;
import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BankAppGateway implements Closeable {

    private final Map<MessageSenderGateway<BankInterestRequest>, String> senderGateways;

    private final MessageReceiverGateway<BankInterestReply> receiverGateway;

    public BankAppGateway() {
        senderGateways = new HashMap<>();
        senderGateways.put(new MessageSenderGateway<>(Queues.ING_REQUEST), Rules.ING);
        senderGateways.put(new MessageSenderGateway<>(Queues.ABN_REQUEST), Rules.ABN_AMRO);
        senderGateways.put(new MessageSenderGateway<>(Queues.RABO_REQUEST), Rules.RABO_BANK);

        receiverGateway = new MessageReceiverGateway<>(Queues.BANK_INTEREST_REPLY);
    }

    public long sendBankRequest(BankInterestRequest request) {
        return senderGateways.entrySet().stream()
                .filter(entry -> {
                    try {
                        var evaluator = new Evaluator();

                        evaluator.putVariable("amount", String.valueOf(request.getAmount()));
                        evaluator.putVariable("time", String.valueOf(request.getTime()));

                        return evaluator.evaluate(entry.getValue()).equals("1.0");
                    } catch (EvaluationException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .peek(entry -> {
                    try {
                        entry.getKey().send(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .count();
    }

    public void onBankReplyReceived(MessageListener<BankInterestReply> messageListener) throws IOException {
        receiverGateway.setListener(messageListener);
    }

    @Override
    public void close() throws IOException {
        for (var senderGateway : senderGateways.keySet()) {
            senderGateway.close();
        }
        receiverGateway.close();
    }
}
