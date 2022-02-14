package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;

import java.util.ArrayList;

public class GuildData {

    private final String id;
    private String commandInvoke;
    private final ArrayList<String> allowedChannels;
    private DailyUpdateInformation dailyUpdateInformation;

    public GuildData(String id, String commandInvoke, ArrayList<String> allowedChannels, DailyUpdateInformation dailyUpdateInformation) {
        this.id = id;
        this.commandInvoke = commandInvoke;
        this.allowedChannels = allowedChannels;
        this.dailyUpdateInformation = dailyUpdateInformation;
    }

    public String getId() {
        return id;
    }

    public String getCommandInvoke() {
        return commandInvoke;
    }

    public ArrayList<String> getAllowedChannels() {
        return allowedChannels;
    }

    public DailyUpdateInformation getDailyUpdateInformation() {
        return dailyUpdateInformation;
    }

    public void setCommandInvoke(String commandInvoke) {
        this.commandInvoke = commandInvoke;
    }

    public void setDailyUpdateInformation(DailyUpdateInformation dailyUpdateInformation) {
        this.dailyUpdateInformation = dailyUpdateInformation;
    }
}
