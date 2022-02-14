package de.glaubekeinemdev.coronabot.dailyupdates;

public class DailyUpdateInformation {

    private boolean enabled;
    private String sendTime;
    private String textChannelId;

    public DailyUpdateInformation() {
        this.enabled = false;
        this.sendTime = "06";
        this.textChannelId = "INSERT TEXTCHANNEL-ID HERE";
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setTextChannelId(String textChannelId) {
        this.textChannelId = textChannelId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getTextChannelId() {
        return textChannelId;
    }

}
