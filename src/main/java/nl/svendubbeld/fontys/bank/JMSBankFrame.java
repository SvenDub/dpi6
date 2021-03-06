package nl.svendubbeld.fontys.bank;

import nl.svendubbeld.fontys.Queues;
import nl.svendubbeld.fontys.messaging.requestreply.RequestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class JMSBankFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfReply;
    private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<>();

    private LoanBrokerAppGateway loanBrokerAppGateway;

    private final String name;
    private final String queue;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                var ingFrame = new JMSBankFrame("ING", Queues.ING_REQUEST);
                ingFrame.setVisible(true);

                var abnFrame = new JMSBankFrame("ABNAMRO", Queues.ABN_REQUEST);
                abnFrame.setVisible(true);

                var raboFrame = new JMSBankFrame("RABOBANK", Queues.RABO_REQUEST);
                raboFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     * @param name
     * @param queue
     */
    public JMSBankFrame(String name, String queue) throws IOException, TimeoutException {
        this.name = name;
        this.queue = queue;
        setTitle(String.format("JMS Bank - %s", name));
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
        gbcScrollPane.gridwidth = 5;
        gbcScrollPane.insets = new Insets(0, 0, 5, 5);
        gbcScrollPane.fill = GridBagConstraints.BOTH;
        gbcScrollPane.gridx = 0;
        gbcScrollPane.gridy = 0;
        contentPane.add(scrollPane, gbcScrollPane);

        JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<>(listModel);
        scrollPane.setViewportView(list);

        JLabel lblNewLabel = new JLabel("type reply");
        GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
        gbcLblNewLabel.anchor = GridBagConstraints.EAST;
        gbcLblNewLabel.insets = new Insets(0, 0, 0, 5);
        gbcLblNewLabel.gridx = 0;
        gbcLblNewLabel.gridy = 1;
        contentPane.add(lblNewLabel, gbcLblNewLabel);

        tfReply = new JTextField();
        GridBagConstraints gbcTfReply = new GridBagConstraints();
        gbcTfReply.gridwidth = 2;
        gbcTfReply.insets = new Insets(0, 0, 0, 5);
        gbcTfReply.fill = GridBagConstraints.HORIZONTAL;
        gbcTfReply.gridx = 1;
        gbcTfReply.gridy = 1;
        contentPane.add(tfReply, gbcTfReply);
        tfReply.setColumns(10);

        JButton btnSendReply = new JButton("send reply");
        btnSendReply.addActionListener(e -> {
            var rr = list.getSelectedValue();
            var interest = Double.parseDouble((tfReply.getText()));
            if (rr != null) {
                var reply = new BankInterestReply(rr.getRequest().getId(), interest, name);
                rr.setReply(reply);
                list.repaint();

                try {
                    loanBrokerAppGateway.sendReply(reply);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        GridBagConstraints gbcBtnSendReply = new GridBagConstraints();
        gbcBtnSendReply.anchor = GridBagConstraints.NORTHWEST;
        gbcBtnSendReply.gridx = 4;
        gbcBtnSendReply.gridy = 1;
        contentPane.add(btnSendReply, gbcBtnSendReply);

        loanBrokerAppGateway = new LoanBrokerAppGateway(queue);

        loanBrokerAppGateway.onBankInterestRequestReceived(request -> listModel.addElement(new RequestReply<>(request, null)));
    }

}
