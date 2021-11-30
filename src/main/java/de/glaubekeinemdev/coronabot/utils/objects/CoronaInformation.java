package de.glaubekeinemdev.coronabot.utils.objects;

import de.glaubekeinemdev.coronabot.CoronaBot;
import org.json.simple.JSONObject;

public class CoronaInformation {

    private final String lastUpdated;

    private final String name;

    private final int population;
    private final int cases;
    private final int deaths;
    private final int recovered;
    private final int weekIncidence;

    private final int casesLast24Hours;
    private final int deathsLast24Hours;
    private final int recoveredLast24Hours;
    private final int lastIncidence;


    private final Double rValue;

    public CoronaInformation(String lastUpdated, String name, int population, int cases, int deaths, int recovered, int weekIncidence, int casesLast24Hours, int deathsLast24Hours, int recoveredLast24Hours, int lastIncidence, Double rValue) {
        this.lastUpdated = lastUpdated;
        this.name = name;
        this.population = population;
        this.cases = cases;
        this.deaths = deaths;
        this.recovered = recovered;
        this.weekIncidence = weekIncidence;
        this.casesLast24Hours = casesLast24Hours;
        this.deathsLast24Hours = deathsLast24Hours;
        this.recoveredLast24Hours = recoveredLast24Hours;
        this.lastIncidence = lastIncidence;
        this.rValue = rValue;
    }

    public static CoronaInformation parse(final JSONObject jsonObject, final int lastIncidence, final String lastUpdated) {
        String name = (jsonObject.containsKey("name") ? jsonObject.get("name").toString() : "Deutschland");
        int population = (jsonObject.containsKey("population") ? (int) (long) jsonObject.get("population") : CoronaBot.getInstance().getGermanPopulation());
        int cases = (int) (long) jsonObject.get("cases");
        int deaths = (int) (long) jsonObject.get("deaths");
        int recovered = (int) (long) jsonObject.get("recovered");
        int weekIncidence = (int) (double) jsonObject.get("weekIncidence");

        Double rValue = null;

        if (jsonObject.containsKey("r"))
            rValue = (double) ((JSONObject) jsonObject.get("r")).get("value");

        final JSONObject delta = (JSONObject) jsonObject.get("delta");

        int casesLast24Hours = (int) (long) delta.get("cases");
        int deathsLast24Hours = (int) (long) delta.get("deaths");
        int recoveredLast24Hours = (int) (long) delta.get("recovered");

        return new CoronaInformation(lastUpdated, name, population, cases, deaths, recovered, weekIncidence, casesLast24Hours,
                deathsLast24Hours, recoveredLast24Hours, lastIncidence, rValue);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Double getrValue() {
        return rValue;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getCases() {
        return cases;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public int getCasesLast24Hours() {
        return casesLast24Hours;
    }

    public int getDeathsLast24Hours() {
        return deathsLast24Hours;
    }

    public int getRecoveredLast24Hours() {
        return recoveredLast24Hours;
    }

    public int getLastIncidence() {
        return lastIncidence;
    }

    public int getWeekIncidence() {
        return weekIncidence;
    }
}
