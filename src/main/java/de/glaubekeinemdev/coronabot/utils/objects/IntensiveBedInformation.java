package de.glaubekeinemdev.coronabot.utils.objects;

import org.json.simple.JSONObject;

public class IntensiveBedInformation {

    private final int bedsFree;
    private final int bedsUsed;

    private final double bedsFreePerLocation;

    private final int covidPatients;
    private final int covidPatientsVentilated;

    public IntensiveBedInformation(int bedsFree, int bedsUsed, double bedsFreePerLocation, int covidPatients, int covidPatientsVentilated) {
        this.bedsFree = bedsFree;
        this.bedsUsed = bedsUsed;
        this.bedsFreePerLocation = bedsFreePerLocation;
        this.covidPatients = covidPatients;
        this.covidPatientsVentilated = covidPatientsVentilated;
    }

    public static IntensiveBedInformation parse(final JSONObject jsonObject) {
        final JSONObject mainObject = (JSONObject) jsonObject.get("overallSum");

        int bedsFree = (int) (long) mainObject.get("intensivBettenFrei");
        int bedsUsed = (int) (long) mainObject.get("intensivBettenBelegt");

        double bedsFreePerLocation = (double) mainObject.get("intensivBettenFreiProStandort");

        int covidPatients = (int) (long) mainObject.get("faelleCovidAktuell");
        int covidPatientsVentilated = (int) (long) mainObject.get("faelleCovidAktuellBeatmet");

        return new IntensiveBedInformation(bedsFree, bedsUsed, bedsFreePerLocation, covidPatients, covidPatientsVentilated);
    }

    public int getBedsFree() {
        return bedsFree;
    }

    public int getBedsUsed() {
        return bedsUsed;
    }

    public double getBedsFreePerLocation() {
        return bedsFreePerLocation;
    }

    public int getCovidPatients() {
        return covidPatients;
    }

    public int getCovidPatientsVentilated() {
        return covidPatientsVentilated;
    }
}
