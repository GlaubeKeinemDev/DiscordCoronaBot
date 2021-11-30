package de.glaubekeinemdev.coronabot.utils.objects;

public class SecondVaccinationInformation {

    private final int biontech;
    private final int moderna;
    private final int astraZeneca;

    private final int vaccinatedLast24Hours;

    private final double quote;

    public SecondVaccinationInformation(int biontech, int moderna, int astraZeneca, int vaccinatedLast24Hours, double quote) {
        this.biontech = biontech;
        this.moderna = moderna;
        this.astraZeneca = astraZeneca;
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

    public int getAstraZeneca() {
        return astraZeneca;
    }

    public int getVaccinatedLast24Hours() {
        return vaccinatedLast24Hours;
    }
}
