package de.glaubekeinemdev.coronabot.commands.admin;

import de.glaubekeinemdev.coronabot.CoronaBot;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import de.glaubekeinemdev.coronabot.utils.SetupData;
import net.dv8tion.jda.api.entities.Message;

public class SetupCommand {

    public void execute(final Message message, GuildData guildData) {
        CoronaBot.getInstance().getGuildSetup().put(message.getMember(), new SetupData(0, guildData));

        message.getTextChannel().sendMessage(message.getMember().getAsMention() +
                " Ok beginnen wir mit dem Commandprefix. Du kannst das Setup jederzeit mit `cancelsetup` beenden." +
                " Bitte gebe einen Commandprefix (1 Zeichen) ein." +
                " Aktueller Commandprefix **" + guildData.getCommandInvoke() + "**").queue();
    }

}
