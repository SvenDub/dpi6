package nl.svendubbeld.fontys.loanbroker;

import com.rabbitmq.client.*;
import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;
import nl.svendubbeld.fontys.model.loan.LoanReply;
import nl.svendubbeld.fontys.model.loan.LoanRequest;
import org.apache.commons.lang3.SerializationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LoanBrokerFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
    private JList<JListLine> list;

    private ConnectionFactory rabbitFactory;

    private int nextId = 1;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoanBrokerFrame frame = new LoanBrokerFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Create the frame.
     */
    public LoanBrokerFrame() throws IOException, TimeoutException {
        rabbitFactory = new ConnectionFactory();
        rabbitFactory.setHost("127.0.0.1");

        Connection connection = rabbitFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(Queues.LOAN_REQUEST, true, false, false, null);
        channel.queueDeclare(Queues.BANK_INTEREST_REPLY, true, false, false, null);

        setTitle("Loan Broker");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gblContentPane = new GridBagLayout();
        gblContentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
        gblContentPane.rowHeights = new int[]{233, 23, 0};
        gblContentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gblContentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gblContentPane);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbcScrollPane = new GridBagConstraints();
        gbcScrollPane.gridwidth = 7;
        gbcScrollPane.insets = new Insets(0, 0, 5, 5);
        gbcScrollPane.fill = GridBagConstraints.BOTH;
        gbcScrollPane.gridx = 0;
        gbcScrollPane.gridy = 0;
        contentPane.add(scrollPane, gbcScrollPane);

        list = new JList<>(listModel);
        scrollPane.setViewportView(list);

        Consumer loanRequestConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                LoanRequest request = SerializationUtils.deserialize(body);

                add(request);
                BankInterestRequest bankInterestRequest = new BankInterestRequest(nextId++, request.getId(), request.getAmount(), request.getTime());
                add(request, bankInterestRequest);

                channel.basicPublish("", Queues.BANK_INTEREST_REQUEST, null, SerializationUtils.serialize(bankInterestRequest));
            }
        };
        channel.basicConsume(Queues.LOAN_REQUEST, true, loanRequestConsumer);

        Consumer bankInterestReplyConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                BankInterestReply reply = SerializationUtils.deserialize(body);
                LoanRequest loanRequest = getLoanRequest(reply.getRequestId());

                add(loanRequest, reply);

                channel.basicPublish("", Queues.LOAN_REPLY, null, SerializationUtils.serialize(new LoanReply(loanRequest.getId(), reply.getInterest(), reply.getQuoteId())));
            }
        };
        channel.basicConsume(Queues.BANK_INTEREST_REPLY, true, bankInterestReplyConsumer);
    }

    private JListLine getRequestReply(LoanRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getLoanRequest().equals(request)) {
                return rr;
            }
        }

        return null;
    }

    public void add(LoanRequest loanRequest) {
        listModel.addElement(new JListLine(loanRequest));
    }


    public void add(LoanRequest loanRequest, BankInterestRequest bankRequest) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankRequest != null) {
            rr.setBankRequest(bankRequest);
            list.repaint();
        }
    }

    public void add(LoanRequest loanRequest, BankInterestReply bankReply) {
        JListLine rr = getRequestReply(loanRequest);
        if (rr != null && bankReply != null) {
            rr.setBankReply(bankReply);
            list.repaint();
        }
    }

    public LoanRequest getLoanRequest(int bankRequestId) {
        for (int i = 0; i < listModel.getSize(); i++) {
            JListLine rr = listModel.get(i);
            if (rr.getBankRequest().getId() == bankRequestId) {
                return rr.getLoanRequest();
            }
        }

        return null;
    }


}
