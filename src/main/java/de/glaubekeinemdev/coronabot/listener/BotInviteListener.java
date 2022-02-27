package de.glaubekeinemdev.coronabot.listener;

import de.glaubekeinemdev.coronabot.CoronaBot;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;
import de.glaubekeinemdev.coronabot.database.BackendHandler;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BotInviteListener extends ListenerAdapter {

    private final BackendHandler backendHandler;

    public BotInviteListener(final JDA jda, final BackendHandler backendHandler) {
        jda.addEventListener(this);
        this.backendHandler = backendHandler;

        jda.getGuilds().forEach(eachGuild -> {
            if (backendHandler.getGuildData(eachGuild.getId()) == null) {
                backendHandler.updateGuildData(eachGuild.getId(), new GuildData(eachGuild.getId(), "!",
                        new ArrayList<>(), new DailyUpdateInformation(), CoronaBot.getInstance().getDefaultColor()));

                try {
                    eachGuild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessageEmbeds(getMessage(jda)).queue();
                    });
                } catch (UnsupportedOperationException e) {
                    if(eachGuild.getCommunityUpdatesChannel() != null) {
                        eachGuild.getCommunityUpdatesChannel().sendMessageEmbeds(getMessage(jda)).queue();
                    }
                }
            }
        });
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        backendHandler.updateGuildData(event.getGuild().getId(), new GuildData(event.getGuild().getId(), "!",
                new ArrayList<>(), new DailyUpdateInformation(), CoronaBot.getInstance().getDefaultColor()));

        try {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessageEmbeds(getMessage(event.getJDA())).queue();
            });
        } catch (UnsupportedOperationException e) {
            if(event.getGuild().getCommunityUpdatesChannel() != null) {
                event.getGuild().getCommunityUpdatesChannel().sendMessageEmbeds(getMessage(event.getJDA())).queue();
            }
        }
    }

    private MessageEmbed getMessage(final JDA jda) {
        final CoronaEmbedBuilder embedBuilder = new CoronaEmbedBuilder();

        embedBuilder.setAuthor("» Danke", null, jda.getSelfUser().getAvatarUrl());
        embedBuilder.setDescription("Danke, dass du dich für Covid-Bot.de entschieden hast. Wir versuchen unseren Bot " +
                "tagtäglich zu verbessern. Sollte dir ein Fehler aufgefallen sein kannst du dich gerne per Discord an " +
                "mich wenden GlaubeKeinemDev#2115");
        embedBuilder.addField("Information:",
                "Der Bot macht automatisch voreinstellungen für deinen Server, wir empfehlen allerdings den " +
                        "Command **!setup** zu verwenden, diesen kannst du in einem beliebigen Channel auf deinem Server " +
                        "ausführen (auf den ich Zugriff haben muss). Durch diesen Command kannst du ein paar Feinheiten " +
                        "für deinen Server einstellen.");
        embedBuilder.addField("Hilfe:",
                "Solltest du Hilfe zu den Commands benötigen verwende **!help**");

        return embedBuilder.build();
    }


}
