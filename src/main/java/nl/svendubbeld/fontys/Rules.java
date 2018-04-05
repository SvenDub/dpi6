package nl.svendubbeld.fontys;

public final class Rules {

    public static final String ING = "#{amount} <= 100000 && #{time} <= 10";
    public static final String ABN_AMRO = "#{amount} >= 200000 && #{amount} <= 300000  && #{time} <= 20";
    public static final String RABO_BANK = "#{amount} <= 250000 && #{time} <= 15";

    private Rules() {
    }
}
