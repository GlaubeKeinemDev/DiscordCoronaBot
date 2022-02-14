package de.glaubekeinemdev.coronabot.database;

import com.google.gson.Gson;
import de.glaubekeinemdev.coronabot.CoronaBot;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import net.dv8tion.jda.api.JDA;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class BackendHandler {

    private final MySQL mySQL;
    private final ConcurrentHashMap<String, GuildData> guildDataCache = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final Timer timer = new Timer();
    private int executedCommands;

    public BackendHandler(final MySQL mySQL, final JDA jda) {
        this.mySQL = mySQL;

        mySQL.getResult("SELECT * FROM Statistics", resultSet -> {
            try {
                if (!resultSet.next()) {
                    mySQL.update("INSERT INTO Statistics (id, guilds, clients, excommands) VALUES ('1', '0', '0', '0')");
                } else {
                    executedCommands = Integer.parseInt(resultSet.getString("excommands"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        mySQL.getResult("SELECT * FROM guilddata", resultSet -> {
            try {
                while (resultSet.next()) {
                    final String id = resultSet.getString("guildId");
                    final GuildData data = gson.fromJson(resultSet.getString("guildData"), GuildData.class);

                    guildDataCache.put(id, data);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final int guilds = jda.getGuilds().size();
                final int clients = CoronaBot.getInstance().getJda().getGuilds().stream().flatMapToInt(guild -> IntStream.of(guild.getMembers().size())).sum();

                mySQL.update("UPDATE Statistics SET guilds='" + guilds + "' WHERE id='1';");
                mySQL.update("UPDATE Statistics SET clients='" + clients + "' WHERE id='1';");
                mySQL.update("UPDATE Statistics SET excommands='" + executedCommands + "' WHERE id='1';");
            }
        }, 1000 * 60, 1000 * 60 * 5);
    }

    public void updateGuildData(final String guildId, final GuildData guildData) {
        guildDataCache.put(guildId, guildData);

        mySQL.getResult("SELECT * FROM guilddata WHERE guildId='" + guildId + "'", resultSet -> {
            try {
                if (resultSet.next()) {
                    mySQL.update("UPDATE guilddata SET guildData='" + gson.toJson(guildData) + "' WHERE guildId='" + guildId + "';");
                } else {
                    mySQL.update("INSERT INTO guilddata (guildId, guildData) VALUES ('" + guildId + "', '" + gson.toJson(guildData) + "')");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public GuildData getGuildData(final String guildId) {
        return guildDataCache.getOrDefault(guildId, null);
    }

    public void removeGuild(final String guildId) {
        guildDataCache.remove(guildId);

        mySQL.update("REMOVE FROM guilddata WHERE guildId='" + guildId + "'");
    }

    public void shutdown() {
        timer.cancel();
    }

    public void addExecutedCommand() {
        executedCommands = executedCommands + 1;
    }


}
