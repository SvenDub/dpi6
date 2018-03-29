package nl.svendubbeld.fontys.model.loan;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class stores all information about a
 * request that a client submits to get a loan.
 */
public class LoanRequest implements Serializable {

    private int id;
    private int ssn; // unique client number.
    private int amount; // the ammount to borrow
    private int time; // the time-span of the loan

    public LoanRequest(int id, int ssn, int amount, int time) {
        super();
        this.id = id;
        this.ssn = ssn;
        this.amount = amount;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
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

    @Override
    public String toString() {
        return "ssn=" + String.valueOf(ssn) + " amount=" + String.valueOf(amount) + " time=" + String.valueOf(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanRequest that = (LoanRequest) o;
        return id == that.id &&
                ssn == that.ssn &&
                amount == that.amount &&
                time == that.time;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ssn, amount, time);
    }
}
