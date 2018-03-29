package nl.svendubbeld.fontys.model.bank;

import java.io.Serializable;

/**
 * This class stores information about the bank reply
 * to a loan request of the specific client
 */
public class BankInterestReply implements Serializable {

    private int requestId;
    private double interest; // the loan interest
    private String bankId; // the nunique quote Id

    public BankInterestReply(int requestId, double interest, String quoteId) {
        this.requestId = requestId;
        this.interest = interest;
        this.bankId = quoteId;
    }

    public int getRequestId() {
        return requestId;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public String getQuoteId() {
        return bankId;
    }

    public void setQuoteId(String quoteId) {
        this.bankId = quoteId;
    }

    public String toString() {
        return "quote=" + this.bankId + " interest=" + this.interest;
    }
}
