package nl.svendubbeld.fontys.model.loan;

import java.io.Serializable;

/**
 * This class stores all information about a bank offer
 * as a response to a client loan request.
 */
public class LoanReply implements Serializable {

    private int requestId;
    private double interest; // the interest that the bank offers
    private String bankID; // the unique quote identification

    public LoanReply() {
        super();
        this.interest = 0;
        this.bankID = "";
    }

    public LoanReply(int requestId, double interest, String quoteID) {
        super();
        this.requestId = requestId;
        this.interest = interest;
        this.bankID = quoteID;
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

    public String getQuoteID() {
        return bankID;
    }

    public void setQuoteID(String quoteID) {
        this.bankID = quoteID;
    }

    @Override
    public String toString() {
        return " interest=" + String.valueOf(interest) + " quoteID=" + String.valueOf(bankID);
    }
}
