package de.glaubekeinemdev.coronabot.utils.objects;

public class Incidence {

    private final double weekIncidence;
    private final String date;

    public Incidence(double weekIncidence, String date) {
        this.weekIncidence = weekIncidence;
        this.date = date;
    }

    public double getWeekIncidence() {
        return weekIncidence;
    }

    public String getDate() {
        return date;
    }
}
