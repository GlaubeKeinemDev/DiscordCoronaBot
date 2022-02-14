package de.glaubekeinemdev.coronabot.utils;

public class SetupData {

    private int step;
    private GuildData guildData;

    public SetupData(int step, GuildData guildData) {
        this.step = step;
        this.guildData = guildData;
    }

    public void increaseStep() {
        this.step = step + 1;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public GuildData getGuildData() {
        return guildData;
    }
}
