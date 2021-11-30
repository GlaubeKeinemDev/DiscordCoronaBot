package de.glaubekeinemdev.coronabot.utils.objects;

public class Population {

    private final int population;
    private final long timeout;

    public Population(int population, long timeout) {
        this.population = population;
        this.timeout = timeout;
    }

    public int getPopulation() {
        return population;
    }

    public long getTimeout() {
        return timeout;
    }
}
