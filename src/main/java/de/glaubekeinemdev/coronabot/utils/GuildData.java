package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;

import java.awt.*;
import java.util.ArrayList;

public class GuildData {

    private final String id;
    private String commandInvoke;
    private final ArrayList<String> allowedChannels;
    private DailyUpdateInformation dailyUpdateInformation;
    private String color;

    public GuildData(String id, String commandInvoke, ArrayList<String> allowedChannels, DailyUpdateInformation dailyUpdateInformation, String color) {
        this.id = id;
        this.commandInvoke = commandInvoke;
        this.allowedChannels = allowedChannels;
        this.dailyUpdateInformation = dailyUpdateInformation;
        this.color = color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Color getColor() {
        return Color.decode(this.color);
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
