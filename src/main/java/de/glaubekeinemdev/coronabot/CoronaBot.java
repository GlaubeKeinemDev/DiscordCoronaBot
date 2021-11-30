package de.glaubekeinemdev.coronabot;

import com.google.gson.Gson;
import de.glaubekeinemdev.coronabot.commands.CoronaCommand;
import de.glaubekeinemdev.coronabot.commands.IncidenceCommand;
import de.glaubekeinemdev.coronabot.configuration.Configuration;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateHelper;
import de.glaubekeinemdev.coronabot.utils.CityAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.ImageEditor;
import de.glaubekeinemdev.coronabot.utils.objects.Population;
import de.glaubekeinemdev.discordutilities.DiscordBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

public class CoronaBot {

    private static CoronaBot instance;

    private final File configFile;
    private Configuration configuration;

    private DiscordBot discordBot;
    private JDA jda;

    private Population germanPopulation;
    private CoronaAPI coronaAPI;
    private DailyUpdateHelper dailyUpdateHelper;

    public CoronaBot() {
        instance = this;

        final File folder = new File("data/");

        if(!folder.exists())
            folder.mkdir();

        configFile = new File(folder, "configuration.json");

        if(configFile.exists()) {
            try {
                configuration = new Gson().fromJson(new FileReader(configFile), Configuration.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            configuration = new Configuration();

            try {
                configuration.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            discordBot = new DiscordBot(configuration.getBotToken(), OnlineStatus.ONLINE);
            discordBot.setupLogger(System.getProperty("user.name") + "@CoronaBot $ ", false);

            // starts the bot
            jda = discordBot.start();

            discordBot.setupCommandCore(configuration.getCommandInvoke());
            discordBot.setEmbedBuilder(new CoronaEmbedBuilder());

            System.out.println("Bot successfully started");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("Error while starting the Bot");
        }

        final CityAPI cityAPI = new CityAPI(folder);

        coronaAPI = new CoronaAPI(cityAPI);

        if (coronaAPI.init(configuration.getRestHost())) {
            final ImageEditor imageEditor = new ImageEditor(folder, coronaAPI);

            coronaAPI.setImageEditor(imageEditor);

            discordBot.registerCommand(new CoronaCommand(null, null, coronaAPI));
            discordBot.registerCommand(new IncidenceCommand(null, null, coronaAPI));
        }

        dailyUpdateHelper = new DailyUpdateHelper(jda, Executors.newScheduledThreadPool(1),
                configuration.getDailyUpdateInformation(), coronaAPI);

    }

    public static CoronaBot getInstance() {
        return instance;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public JDA getJda() {
        return jda;
    }

    public int getGermanPopulation() {
        if (germanPopulation == null || System.currentTimeMillis() >= germanPopulation.getTimeout()) {
            germanPopulation = new Population(coronaAPI.getPopulation(), System.currentTimeMillis() + 86400000);
            return germanPopulation.getPopulation();
        }
        return germanPopulation.getPopulation();
    }

    public CoronaAPI getCoronaAPI() {
        return coronaAPI;
    }

    public DailyUpdateHelper getDailyUpdateHelper() {
        return dailyUpdateHelper;
    }
}
