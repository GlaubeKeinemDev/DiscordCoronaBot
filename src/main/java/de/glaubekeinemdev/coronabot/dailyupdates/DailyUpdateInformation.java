package de.glaubekeinemdev.coronabot.dailyupdates;

public class DailyUpdateInformation {

    private final boolean enabled;
    private final String sendTime;
    private final String guildId;
    private final String textChannelId;

    public DailyUpdateInformation() {
        this.enabled = false;
        this.sendTime = "06";
        this.guildId = "INSERT GUILDID (SERVER-ID) HERE";
        this.textChannelId = "INSERT TEXTCHANNEL-ID HERE";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getTextChannelId() {
        return textChannelId;
    }
}
