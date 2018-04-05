package nl.svendubbeld.fontys.loanbroker;

import nl.svendubbeld.fontys.model.bank.BankInterestReply;
import nl.svendubbeld.fontys.model.bank.BankInterestRequest;
import nl.svendubbeld.fontys.model.loan.LoanRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents one line in the JList in Loan Broker.
 * This class stores all objects that belong to one LoanRequest:
 * - LoanRequest,
 * - BankInterestRequest, and
 * - BankInterestReply.
 * Use objects of this class to add them to the JList.
 *
 * @author 884294
 */
class JListLine {

    private LoanRequest loanRequest;
    private BankInterestRequest bankRequest;
    private Set<BankInterestReply> bankReplies = new HashSet<>();
    private long requestsSent = 0;

    public JListLine(LoanRequest loanRequest) {
        this.setLoanRequest(loanRequest);
    }

    public LoanRequest getLoanRequest() {
        return loanRequest;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }

    public BankInterestRequest getBankRequest() {
        return bankRequest;
    }

    public void setBankRequest(BankInterestRequest bankRequest) {
        this.bankRequest = bankRequest;
    }

    public Set<BankInterestReply> getBankReplies() {
        return bankReplies;
    }

    public void addBankReply(BankInterestReply bankReply) {
        this.bankReplies.add(bankReply);
    }

    public long getRequestsSent() {
        return requestsSent;
    }

    public void setRequestsSent(long requestsSent) {
        this.requestsSent = requestsSent;
    }

    @Override
    public String toString() {
        return loanRequest.toString() + " || " + String.format("(%d/%d)", bankReplies.size(), getRequestsSent()) + " || " + bankReplies.stream().map(BankInterestReply::toString).collect(Collectors.joining(","));
    }

}
