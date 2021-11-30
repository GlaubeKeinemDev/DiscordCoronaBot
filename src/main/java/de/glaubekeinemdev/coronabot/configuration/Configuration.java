package de.glaubekeinemdev.coronabot.configuration;

import com.google.gson.GsonBuilder;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {

    private final String botToken;
    private final String restHost;
    private final String commandInvoke;
    private final DailyUpdateInformation dailyUpdateInformation;

    public Configuration() {
        this.botToken = "INSERT BOTTOKEN HERE";
        this.restHost = "https://api.corona-zahlen.org/";
        this.commandInvoke = "!";
        this.dailyUpdateInformation = new DailyUpdateInformation();
    }

    public void save(final File file) throws IOException {
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
        fileWriter.flush();
        fileWriter.close();
    }

    public String getBotToken() {
        return botToken;
    }

    public String getRestHost() {
        return restHost;
    }

    public String getCommandInvoke() {
        return commandInvoke;
    }

    public DailyUpdateInformation getDailyUpdateInformation() {
        return dailyUpdateInformation;
    }
}
