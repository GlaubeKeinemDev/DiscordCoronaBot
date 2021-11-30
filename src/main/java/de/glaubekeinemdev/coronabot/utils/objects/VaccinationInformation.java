package de.glaubekeinemdev.coronabot.utils.objects;

import org.json.simple.JSONObject;

public class VaccinationInformation {

    private final String lastUpdated;

    private final String name;

    private final int totalDosesGiven;
    private final int firstDose;
    private final int vaccinated;

    private final int vaccinatedLast24Hours;

    private final FirstVaccinationInformation firstVaccinationInformation;
    private final SecondVaccinationInformation secondVaccinationInformation;
    private final BoosterVaccinationInformation boosterVaccinationInformation;

    public VaccinationInformation(String lastUpdated, String name, int totalDosesGiven, int firstDose, int vaccinated, int vaccinatedLast24Hours, FirstVaccinationInformation firstVaccinationInformation, SecondVaccinationInformation secondVaccinationInformation, BoosterVaccinationInformation boosterVaccinationInformation) {
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.totalDosesGiven = totalDosesGiven;
        this.firstDose = firstDose;
        this.vaccinated = vaccinated;
        this.vaccinatedLast24Hours = vaccinatedLast24Hours;
        this.firstVaccinationInformation = firstVaccinationInformation;
        this.secondVaccinationInformation = secondVaccinationInformation;
        this.boosterVaccinationInformation = boosterVaccinationInformation;
    }

    public static VaccinationInformation parse(final JSONObject jsonObject, final String lastUpdated) {
        String name = (jsonObject.containsKey("name") ? jsonObject.get("name").toString() : "Deutschland");

        int totalDosesGiven = (int) (long) jsonObject.get("administeredVaccinations");
        int firstDose = (int) (long) jsonObject.get("vaccinated");
        int vaccinated = (int) (long) ((JSONObject) jsonObject.get("secondVaccination")).get("vaccinated");

        final JSONObject firstVaccinationObject = (JSONObject) jsonObject.get("vaccination");

        final FirstVaccinationInformation firstVaccinationInformation = new FirstVaccinationInformation(
                (int) (long) firstVaccinationObject.get("biontech"), (int) (long) firstVaccinationObject.get("moderna"),
                (int) (long) firstVaccinationObject.get("astraZeneca"), (int) (long) firstVaccinationObject.get("janssen"),
                (int) (long) jsonObject.get("delta"),
                (((double) jsonObject.get("quote")) * 100.0));

        final JSONObject secondVaccinationObject = (JSONObject) ((JSONObject) jsonObject.get("secondVaccination"))
                .get("vaccination");

        final SecondVaccinationInformation secondVaccinationInformation = new SecondVaccinationInformation(
                (int) (long) secondVaccinationObject.get("biontech"), (int) (long) secondVaccinationObject.get("moderna"),
                (int) (long) secondVaccinationObject.get("astraZeneca"),
                (int) (long) ((JSONObject) jsonObject.get("secondVaccination")).get("delta"),
                ((double) ((JSONObject) jsonObject.get("secondVaccination")).get("quote") * 100.0));

        final JSONObject boosterVaccinationObject = (JSONObject) ((JSONObject) jsonObject.get("boosterVaccination"))
                .get("vaccination");

        final BoosterVaccinationInformation boosterVaccinationInformation = new BoosterVaccinationInformation(
                (int) (long) boosterVaccinationObject.get("biontech"), (int) (long) boosterVaccinationObject.get("moderna"),
                (int) (long) boosterVaccinationObject.get("janssen"),
                (int) (long) ((JSONObject) jsonObject.get("boosterVaccination")).get("delta"),
                ((double) ((JSONObject) jsonObject.get("boosterVaccination")).get("quote") * 100.0));

        final double quote = ((double) ((JSONObject) jsonObject.get("secondVaccination")).get("quote") * 100.0);
        final int vaccinatedLast24Hours = firstVaccinationInformation.getVaccinatedLast24Hours()
                + secondVaccinationInformation.getVaccinatedLast24Hours()
                + boosterVaccinationInformation.getVaccinatedLast24Hours();

        return new VaccinationInformation(lastUpdated, name, totalDosesGiven, firstDose, vaccinated,
                vaccinatedLast24Hours, firstVaccinationInformation,
                secondVaccinationInformation, boosterVaccinationInformation);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getName() {
        return name;
    }

    public int getTotalDosesGiven() {
        return totalDosesGiven;
    }

    public int getFirstDose() {
        return firstDose;
    }

    public int getVaccinated() {
        return vaccinated;
    }

    public int getVaccinatedLast24Hours() {
        return vaccinatedLast24Hours;
    }

    public FirstVaccinationInformation getFirstVaccinationInformation() {
        return firstVaccinationInformation;
    }

    public SecondVaccinationInformation getSecondVaccinationInformation() {
        return secondVaccinationInformation;
    }

    public BoosterVaccinationInformation getBoosterVaccinationInformation() {
        return boosterVaccinationInformation;
    }
}
