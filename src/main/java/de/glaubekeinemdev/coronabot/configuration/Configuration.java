package de.glaubekeinemdev.coronabot.configuration;

import com.google.gson.GsonBuilder;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;
import de.glaubekeinemdev.coronabot.database.DataBaseCredential;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {

    private final String botToken;
    private final String restHost;

    private final DataBaseCredential dataBaseCredential;

    public Configuration() {
        this.botToken = "INSERT BOTTOKEN HERE";
        this.restHost = "https://api.corona-zahlen.org/";
        this.dataBaseCredential = DataBaseCredential.getDefault();
    }

    public void save(final File file) throws IOException {
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
        fileWriter.flush();
        fileWriter.close();
    }

    public DataBaseCredential getDataBaseCredential() {
        return dataBaseCredential;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getRestHost() {
        return restHost;
    }
}
