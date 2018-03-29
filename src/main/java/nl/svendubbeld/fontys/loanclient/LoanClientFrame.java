package nl.svendubbeld.fontys.loanclient;

import nl.svendubbeld.fontys.messaging.requestreply.RequestReply;
import nl.svendubbeld.fontys.model.loan.LoanReply;
import nl.svendubbeld.fontys.model.loan.LoanRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

public class LoanClientFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tfSSN;
    private DefaultListModel<RequestReply<LoanRequest, LoanReply>> listModel = new DefaultListModel<>();
    private JList<RequestReply<LoanRequest, LoanReply>> requestReplyList;

    private JTextField tfAmount;
    private JLabel lblNewLabel;
    private JLabel lblNewLabel_1;
    private JTextField tfTime;

    private LoanBrokerAppGateway loanBrokerAppGateway;

    private int nextId = 1;

    /**
     * Create the frame.
     */
    public LoanClientFrame() throws IOException {
        setTitle("Loan Client");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 684, 619);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gblContentPane = new GridBagLayout();
        gblContentPane.columnWidths = new int[]{0, 0, 30, 30, 30, 30, 0};
        gblContentPane.rowHeights = new int[]{30, 30, 30, 30, 30};
        gblContentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gblContentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gblContentPane);

        JLabel lblBody = new JLabel("ssn");
        GridBagConstraints gbcLblBody = new GridBagConstraints();
        gbcLblBody.insets = new Insets(0, 0, 5, 5);
        gbcLblBody.gridx = 0;
        gbcLblBody.gridy = 0;
        contentPane.add(lblBody, gbcLblBody);

        tfSSN = new JTextField();
        GridBagConstraints gbcTfSSN = new GridBagConstraints();
        gbcTfSSN.fill = GridBagConstraints.HORIZONTAL;
        gbcTfSSN.insets = new Insets(0, 0, 5, 5);
        gbcTfSSN.gridx = 1;
        gbcTfSSN.gridy = 0;
        contentPane.add(tfSSN, gbcTfSSN);
        tfSSN.setColumns(10);

        lblNewLabel = new JLabel("amount");
        GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
        gbcLblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbcLblNewLabel.anchor = GridBagConstraints.WEST;
        gbcLblNewLabel.gridx = 0;
        gbcLblNewLabel.gridy = 1;
        contentPane.add(lblNewLabel, gbcLblNewLabel);

        tfAmount = new JTextField();
        GridBagConstraints gbcTfAmount = new GridBagConstraints();
        gbcTfAmount.anchor = GridBagConstraints.NORTH;
        gbcTfAmount.insets = new Insets(0, 0, 5, 5);
        gbcTfAmount.fill = GridBagConstraints.HORIZONTAL;
        gbcTfAmount.gridx = 1;
        gbcTfAmount.gridy = 1;
        contentPane.add(tfAmount, gbcTfAmount);
        tfAmount.setColumns(10);

        lblNewLabel_1 = new JLabel("time");
        GridBagConstraints gbcLblNewLabel1 = new GridBagConstraints();
        gbcLblNewLabel1.anchor = GridBagConstraints.EAST;
        gbcLblNewLabel1.insets = new Insets(0, 0, 5, 5);
        gbcLblNewLabel1.gridx = 0;
        gbcLblNewLabel1.gridy = 2;
        contentPane.add(lblNewLabel_1, gbcLblNewLabel1);

        tfTime = new JTextField();
        GridBagConstraints gbcTfTime = new GridBagConstraints();
        gbcTfTime.insets = new Insets(0, 0, 5, 5);
        gbcTfTime.fill = GridBagConstraints.HORIZONTAL;
        gbcTfTime.gridx = 1;
        gbcTfTime.gridy = 2;
        contentPane.add(tfTime, gbcTfTime);
        tfTime.setColumns(10);

        JButton btnQueue = new JButton("send loan request");
        btnQueue.addActionListener(arg0 -> {
            int ssn = Integer.parseInt(tfSSN.getText());
            int amount = Integer.parseInt(tfAmount.getText());
            int time = Integer.parseInt(tfTime.getText());

            var request = new LoanRequest(nextId++, ssn, amount, time);
            listModel.addElement(new RequestReply<>(request, null));

            try {
                loanBrokerAppGateway.applyForLoan(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        GridBagConstraints gbcBtnQueue = new GridBagConstraints();
        gbcBtnQueue.insets = new Insets(0, 0, 5, 5);
        gbcBtnQueue.gridx = 2;
        gbcBtnQueue.gridy = 2;
        contentPane.add(btnQueue, gbcBtnQueue);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbcScrollPane = new GridBagConstraints();
        gbcScrollPane.gridheight = 7;
        gbcScrollPane.gridwidth = 6;
        gbcScrollPane.fill = GridBagConstraints.BOTH;
        gbcScrollPane.gridx = 0;
        gbcScrollPane.gridy = 4;
        contentPane.add(scrollPane, gbcScrollPane);

        requestReplyList = new JList<>(listModel);
        scrollPane.setViewportView(requestReplyList);

        loanBrokerAppGateway = new LoanBrokerAppGateway();
        loanBrokerAppGateway.onLoanReplyReceived(reply -> {
            var requestReply = getRequestReply(getLoanRequest(reply.getRequestId()));
            if (requestReply != null) {
                requestReply.setReply(reply);
                requestReplyList.repaint();
            }
        });

    }

    /**
     * This method returns the RequestReply line that belongs to the request from requestReplyList (JList).
     * You can call this method when an reply arrives in order to add this reply to the right request in requestReplyList.
     *
     * @param request
     * @return
     */
    private RequestReply<LoanRequest, LoanReply> getRequestReply(LoanRequest request) {

        for (int i = 0; i < listModel.getSize(); i++) {
            RequestReply<LoanRequest, LoanReply> rr = listModel.get(i);
            if (rr.getRequest().equals(request)) {
                return rr;
            }
        }

        return null;
    }

    private LoanRequest getLoanRequest(int id) {
        for (int i = 0; i < listModel.getSize(); i++) {
            RequestReply<LoanRequest, LoanReply> rr = listModel.get(i);
            if (rr.getRequest().getId() == id) {
                return rr.getRequest();
            }
        }

        return null;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoanClientFrame frame = new LoanClientFrame();

                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
