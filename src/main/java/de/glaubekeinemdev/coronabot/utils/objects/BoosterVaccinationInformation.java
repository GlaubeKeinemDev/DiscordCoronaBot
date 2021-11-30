package de.glaubekeinemdev.coronabot.utils.objects;

public class BoosterVaccinationInformation {

    private final int biontech;
    private final int moderna;
    private final int jannsen;

    private final int vaccinatedLast24Hours;

    private final double quote;

    public BoosterVaccinationInformation(int biontech, int moderna, int jannsen, int vaccinatedLast24Hours, double quote) {
        this.biontech = biontech;
        this.moderna = moderna;
        this.jannsen = jannsen;
        this.vaccinatedLast24Hours = vaccinatedLast24Hours;
        this.quote = quote;
    }

    public double getQuote() {
        return quote;
    }

    public int getBiontech() {
        return biontech;
    }

    public int getModerna() {
        return moderna;
    }

    public int getJannsen() {
        return jannsen;
    }

    public int getVaccinatedLast24Hours() {
        return vaccinatedLast24Hours;
    }
}
