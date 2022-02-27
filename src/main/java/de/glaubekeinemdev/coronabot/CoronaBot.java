package de.glaubekeinemdev.coronabot;

import com.google.gson.Gson;
import de.glaubekeinemdev.coronabot.configuration.Configuration;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateHelper;
import de.glaubekeinemdev.coronabot.database.BackendHandler;
import de.glaubekeinemdev.coronabot.database.MySQL;
import de.glaubekeinemdev.coronabot.listener.BotInviteListener;
import de.glaubekeinemdev.coronabot.listener.BotLeaveGuildListener;
import de.glaubekeinemdev.coronabot.listener.GuildMessageListener;
import de.glaubekeinemdev.coronabot.utils.CityAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.ImageEditor;
import de.glaubekeinemdev.coronabot.utils.SetupData;
import de.glaubekeinemdev.coronabot.utils.objects.Population;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoronaBot {

    private static CoronaBot instance;

    private final String defaultColor = "#900C3F";
    private final ConcurrentHashMap<Member, SetupData> guildSetup = new ConcurrentHashMap<>();

    private final File configFile;
    private Configuration configuration;

    private JDA jda;

    private Population germanPopulation;
    private final CoronaAPI coronaAPI;
    private DailyUpdateHelper dailyUpdateHelper;
    private final Long startedTime;

    public CoronaBot() {
        instance = this;
        this.startedTime = System.currentTimeMillis();

        final File folder = new File("data/");

        if (!folder.exists())
            folder.mkdir();

        configFile = new File(folder, "configuration.json");

        if (configFile.exists()) {
            try {
                configuration = new Gson().fromJson(new FileReader(configFile), Configuration.class);
            } catch (FileNotFoundException e) {
                Logger.getLogger(CoronaBot.class.getName()).log(Level.SEVERE, null, e);
            }
        } else {
            configuration = new Configuration();

            try {
                configuration.save(configFile);
            } catch (IOException e) {
                Logger.getLogger(CoronaBot.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        try {
            jda = JDABuilder.create(this.configuration.getBotToken(), Arrays.asList(GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_PRESENCES))
                    .disableCache(Arrays.asList(CacheFlag.VOICE_STATE, CacheFlag.EMOTE,
                            CacheFlag.ACTIVITY, CacheFlag.MEMBER_OVERRIDES))
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.watching("covid-bot.de"))
                    .setBulkDeleteSplittingEnabled(true)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setAutoReconnect(true)
                    .build()
                    .awaitReady();
        } catch (LoginException | InterruptedException e) {
            Logger.getLogger(CoronaBot.class.getName()).log(Level.SEVERE, null, e);
        }

        final MySQL mySQL = new MySQL(configuration.getDataBaseCredential());
        final BackendHandler backendHandler = new BackendHandler(mySQL, jda);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new BotInviteListener(jda, backendHandler);
                new BotLeaveGuildListener(jda, backendHandler);
            }
        }, 10000);

        final CityAPI cityAPI = new CityAPI(folder);

        coronaAPI = new CoronaAPI(cityAPI);

        if (coronaAPI.init(configuration.getRestHost())) {
            final ImageEditor imageEditor = new ImageEditor(folder, coronaAPI);

            coronaAPI.setImageEditor(imageEditor);

            new GuildMessageListener(jda, backendHandler, coronaAPI);

            dailyUpdateHelper = new DailyUpdateHelper(jda, Executors.newScheduledThreadPool(1),
                    coronaAPI, backendHandler);

            cityAPI.init();
        }
    }

    public Long getStartedTime() {
        return startedTime;
    }

    public static CoronaBot getInstance() {
        return instance;
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

    public String getDefaultColor() {
        return defaultColor;
    }

    public ConcurrentHashMap<Member, SetupData> getGuildSetup() {
        return guildSetup;
    }

    public DailyUpdateHelper getDailyUpdateHelper() {
        return dailyUpdateHelper;
    }
}
