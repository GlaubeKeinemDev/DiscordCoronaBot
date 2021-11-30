package de.glaubekeinemdev.coronabot.utils.objects;

import org.json.simple.JSONObject;

public class TestInformation {

    private final String lastUpdated;
    private final int performedTests;
    private final int positiveTests;
    private final double positivityRate;
    private final int laboratoryCount;

    public TestInformation(String lastUpdated, int performedTests, int positiveTests, double positivityRate,
                           int laboratoryCount) {
        this.lastUpdated = lastUpdated;
        this.performedTests = performedTests;
        this.positiveTests = positiveTests;
        this.positivityRate = positivityRate;
        this.laboratoryCount = laboratoryCount;
    }

    public static TestInformation parse(final JSONObject jsonObject, final String lastUpdated) {
        int performedTests = (int) (long) jsonObject.get("performedTests");
        int positiveTests = (int) (long) jsonObject.get("positiveTests");
        double positivityRate = (double) jsonObject.get("positivityRate");
        int laboratoryCount = (int) (long) jsonObject.get("laboratoryCount");

        return new TestInformation(lastUpdated, performedTests, positiveTests, positivityRate, laboratoryCount);
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public int getPerformedTests() {
        return performedTests;
    }

    public int getPositiveTests() {
        return positiveTests;
    }

    public double getPositivityRate() {
        return positivityRate;
    }

    public int getLaboratoryCount() {
        return laboratoryCount;
    }
}
