package nl.svendubbeld.fontys.model.bank;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class stores all information about an request from a bank to offer
 * a loan to a specific client.
 */
public class BankInterestRequest implements Serializable {

    private int id;
    private transient int loanRequestId;
    private int amount; // the requested loan amount
    private int time; // the requested loan period

    public BankInterestRequest(int id, int loanRequestId, int amount, int time) {
        super();
        this.id = id;
        this.loanRequestId = loanRequestId;
        this.amount = amount;
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getLoanRequestId() {
        return loanRequestId;
    }

    @Override
    public String toString() {
        return " amount=" + amount + " time=" + time;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankInterestRequest that = (BankInterestRequest) o;
        return id == that.id &&
                amount == that.amount &&
                time == that.time;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, amount, time);
    }
}
