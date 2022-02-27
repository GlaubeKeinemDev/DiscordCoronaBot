package de.glaubekeinemdev.coronabot.commands.user;

import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class HelpCommand {

    public void execute(final Message message, final GuildData guildData) {
        final Member member = message.getMember();

        final CoronaEmbedBuilder coronaEmbedBuilder = new CoronaEmbedBuilder("Hilfe");

        coronaEmbedBuilder.addField("Befehle",
                "**" + guildData.getCommandInvoke() + "corona <Bundesland/Stadtkreis>** - Zeigt allgemeine Daten, Fakten und Vorhersagen.\n" +
                "**" + guildData.getCommandInvoke() + "inzidenz <Bundesland/Stadtkreis>** - Zeigt den Inzidenzverlauf an.\n" +
                "**" + guildData.getCommandInvoke() + "info** - Zeigt Informationen & Statistiken zum Bot.");

        if(member.isOwner() || member.hasPermission(Permission.MANAGE_SERVER)) {
            coronaEmbedBuilder.addField("Admin Befehle",
                    "**" + guildData.getCommandInvoke() + "setup** - Richte den Bot so ein wie du ihn brauchst.");
        }

        coronaEmbedBuilder.setColor(guildData.getColor());

        message.getTextChannel().sendMessageEmbeds(coronaEmbedBuilder.build()).queue();
    }

}
