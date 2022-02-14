package de.glaubekeinemdev.coronabot.listener;

import de.glaubekeinemdev.coronabot.database.BackendHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotLeaveGuildListener extends ListenerAdapter {

    private final BackendHandler backendHandler;

    public BotLeaveGuildListener(final JDA jda, final BackendHandler backendHandler) {
        jda.addEventListener(this);
        this.backendHandler = backendHandler;
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        this.backendHandler.removeGuild(event.getGuild().getId());
    }
}
