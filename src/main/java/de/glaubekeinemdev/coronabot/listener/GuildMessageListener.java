package de.glaubekeinemdev.coronabot.listener;

import de.glaubekeinemdev.coronabot.CoronaBot;
import de.glaubekeinemdev.coronabot.commands.admin.SetupCommand;
import de.glaubekeinemdev.coronabot.commands.user.CoronaCommand;
import de.glaubekeinemdev.coronabot.commands.user.HelpCommand;
import de.glaubekeinemdev.coronabot.commands.user.IncidenceCommand;
import de.glaubekeinemdev.coronabot.commands.user.InfoCommand;
import de.glaubekeinemdev.coronabot.dailyupdates.DailyUpdateInformation;
import de.glaubekeinemdev.coronabot.database.BackendHandler;
import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import de.glaubekeinemdev.coronabot.utils.SetupData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuildMessageListener extends ListenerAdapter {

    private final BackendHandler backendHandler;

    private final CoronaCommand coronaCommand;
    private final HelpCommand helpCommand;
    private final IncidenceCommand incidenceCommand;
    private final InfoCommand infoCommand;
    private final SetupCommand setupCommand;

    public GuildMessageListener(final JDA jda, final BackendHandler backendHandler, final CoronaAPI coronaAPI) {
        jda.addEventListener(this);
        this.backendHandler = backendHandler;

        this.coronaCommand = new CoronaCommand(coronaAPI);
        this.helpCommand = new HelpCommand();
        this.incidenceCommand = new IncidenceCommand(coronaAPI);
        this.infoCommand = new InfoCommand();
        this.setupCommand = new SetupCommand();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild())
            return;
        if (event.getMember() == null)
            return;
        if (event.getMember().getUser().isBot())
            return;

        if (CoronaBot.getInstance().getGuildSetup().containsKey(event.getMember())) {
            final SetupData setupData = CoronaBot.getInstance().getGuildSetup().get(event.getMember());
            final String message = event.getMessage().getContentRaw();
            final Member member = event.getMember();
            final TextChannel textChannel = event.getTextChannel();

            if (message.equalsIgnoreCase("cancelsetup")) {
                textChannel.sendMessage(member.getAsMention() + " Alles klar, das Setup wurde abgebrochen.").queue();
                CoronaBot.getInstance().getGuildSetup().remove(member);
                return;
            }

            if (setupData.getStep() == 0) {
                if (message.length() > 1 || message.equalsIgnoreCase(" ")) {
                    textChannel.sendMessage(member.getAsMention() + " Bitte gebe nur **1** (gültiges) Zeichen ein!").queue();
                    return;
                }
                textChannel.sendMessage(member.getAsMention() + " " +
                        "Erfolgreich **" + message + "** wurde als Commandprefix gesetzt." +
                        " Möchtest du die Textkanäle begrenzen, in welchen die Corona-Commands ausgeführt werden können `(y/n)`").queue();
                setupData.getGuildData().setCommandInvoke(message);
            }
            if (setupData.getStep() == 1) {
                if (!message.equalsIgnoreCase("y") && !message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Bitte gebe `y` für Ja oder `n` für Nein ein.").queue();
                    return;
                }
                if (message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Möchtest du tägliche Updates verwenden," +
                            " damit werden jeden Tag um eine festgelegte Uhrzeit aktuelle Coronazahlen in einen Channel gesendet `(y/n)`").queue();
                    setupData.getGuildData().getAllowedChannels().clear();
                    CoronaBot.getInstance().getGuildSetup().put(event.getMember(), setupData);
                    setupData.setStep(4);
                    return;
                }
                if (message.equalsIgnoreCase("y")) {
                    setupData.getGuildData().getAllowedChannels().clear();
                    CoronaBot.getInstance().getGuildSetup().put(event.getMember(), setupData);
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Bitte gebe als nächsten Schritt den Kanal ein. Gebe `clear` ein um die Commands überall zu erlauben.").queue();
                }
            }
            if (setupData.getStep() == 2) {
                if(message.equalsIgnoreCase("clear")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar, die Command können nun überall " +
                            "benutzt werden. Möchtest du tägliche Updates verwenden, damit werden jeden Tag um eine " +
                            "festgelegte Uhrzeit aktuelle Coronazahlen in einen Channel gesendet `(y/n)`").queue();

                    setupData.setStep(4);
                    setupData.getGuildData().getAllowedChannels().clear();
                    CoronaBot.getInstance().getGuildSetup().put(event.getMember(), setupData);
                    return;
                }

                final TextChannel targetChannel = getChannel(event.getGuild(), message);

                if(targetChannel == null) {
                    textChannel.sendMessage(member.getAsMention() +
                            " Es konnte kein Textchannel mit dem Namen/Id **" + message + "** gefunden werden. " +
                            "Bitte gebe eine gültige Eingabe ein!").queue();
                    return;
                }

                textChannel.sendMessage(member.getAsMention() + " Alles klar " + targetChannel.getAsMention() +
                        " wurde hinzugefügt! Möchtest du einen weiteren Channel hinzufügen `(y/n)`.").queue();
                setupData.getGuildData().getAllowedChannels().add(targetChannel.getId());
            }
            if(setupData.getStep() == 3) {
                if (!message.equalsIgnoreCase("y") && !message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Bitte gebe `y` für Ja, `n` für Nein ein").queue();
                    return;
                }
                if (message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Möchtest du tägliche Updates verwenden," +
                            " damit werden jeden Tag um eine festgelegte Uhrzeit aktuelle Coronazahlen in einen Channel gesendet `(y/n)`").queue();
                }
                if (message.equalsIgnoreCase("y")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Bitte gebe als nächsten Schritt den Kanal ein.").queue();
                    setupData.setStep(2);
                    CoronaBot.getInstance().getGuildSetup().put(event.getMember(), setupData);
                    return;
                }
            }
            if(setupData.getStep() == 4) {
                if (!message.equalsIgnoreCase("y") && !message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Bitte gebe `y` für Ja, `n` für Nein ein").queue();
                    return;
                }
                if (message.equalsIgnoreCase("n")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Tägliche Updates werden deaktiviert," +
                            " das Setup ist beendet").queue();
                    final DailyUpdateInformation dailyUpdateInformation = setupData.getGuildData().getDailyUpdateInformation();
                    dailyUpdateInformation.setEnabled(false);
                    setupData.getGuildData().setDailyUpdateInformation(dailyUpdateInformation);

                    backendHandler.updateGuildData(setupData.getGuildData().getId(), setupData.getGuildData());

                    CoronaBot.getInstance().getGuildSetup().remove(member);
                    return;
                }
                if (message.equalsIgnoreCase("y")) {
                    textChannel.sendMessage(member.getAsMention() + " Alles klar. Bitte gebe als nächsten Schritt " +
                            "den Kanal ein, in welchen tägliche Updates gesendet werden sollen.").queue();
                    final DailyUpdateInformation dailyUpdateInformation = setupData.getGuildData().getDailyUpdateInformation();
                    dailyUpdateInformation.setEnabled(true);
                    setupData.getGuildData().setDailyUpdateInformation(dailyUpdateInformation);
                }
            }
            if(setupData.getStep() == 5) {
                final TextChannel targetChannel = getChannel(event.getGuild(), message);

                if(targetChannel == null) {
                    textChannel.sendMessage(member.getAsMention() +
                            " Es konnte kein Textchannel mit dem Namen/Id **" + message + "** gefunden werden. " +
                            "Bitte gebe eine gültige Eingabe ein!").queue();
                    return;
                }

                textChannel.sendMessage(member.getAsMention() + " Alles klar " + targetChannel.getAsMention() +
                        " wurde für tägliche Updates eingerichtet! Bitte gebe im nächsten Schritt die Zeit (Stunde) an," +
                        " in welcher die täglichen Updates gesendet werden sollen `(01-24)`.").queue();
                final DailyUpdateInformation dailyUpdateInformation = setupData.getGuildData().getDailyUpdateInformation();
                dailyUpdateInformation.setTextChannelId(targetChannel.getId());
                setupData.getGuildData().setDailyUpdateInformation(dailyUpdateInformation);
            }
            if(setupData.getStep() == 6) {
                if(!CoronaBot.getInstance().getDailyUpdateHelper().getAvailableTimes().contains(message)) {
                    textChannel.sendMessage(member.getAsMention() + " Bitte gebe eine gültige Zeit an (Stunde) `(01-24)`").queue();
                    return;
                }

                final DailyUpdateInformation dailyUpdateInformation = setupData.getGuildData().getDailyUpdateInformation();
                dailyUpdateInformation.setSendTime(message);
                setupData.getGuildData().setDailyUpdateInformation(dailyUpdateInformation);

                textChannel.sendMessage(member.getAsMention() + " Alles klar. Tägliche Updates wurden konfiguriert." +
                        " Bitte konfiguriere im nächsten Schritt eine Farbe in der die Nachrichten gesendet werden sollen," +
                        " gebe nur HTML/HEX Farbcodes an, gebe `n` ein um eine Standardfarbe zu verwenden").queue();
            }
            if(setupData.getStep() == 7) {
                final String color;

                if(message.equalsIgnoreCase("n")) {
                    color = CoronaBot.getInstance().getDefaultColor();

                    textChannel.sendMessage(member.getAsMention() + " Alles klar, die Farbe wurde auf eine " +
                            "Standardfarbe gesetzt. Das Setup ist beendet.").queue();
                } else {
                    if (message.contains("#")) {
                        color = message;
                    } else {
                        color = "#" + message;
                    }

                    try {
                        Color.decode(color);
                    } catch (NumberFormatException e) {
                        textChannel.sendMessage(member.getAsMention() + " Bitte übergebe eine gültige Farbe (HTML/HEX Color code)").queue();
                        return;
                    }

                    textChannel.sendMessage(member.getAsMention() + " Alles klar, die Farbe wurde auf **" + color + "** gesetzt. Das Setup ist beendet.").queue();
                }

                setupData.getGuildData().setColor(color);
                backendHandler.updateGuildData(setupData.getGuildData().getId(), setupData.getGuildData());

                CoronaBot.getInstance().getGuildSetup().remove(member);
                return;
            }

            setupData.increaseStep();
            CoronaBot.getInstance().getGuildSetup().put(event.getMember(), setupData);
            return;
        }

        GuildData guildData = backendHandler.getGuildData(event.getGuild().getId());

        if (guildData == null) {
            guildData = new GuildData(event.getGuild().getId(), "!", new ArrayList<>(),
                    new DailyUpdateInformation(), CoronaBot.getInstance().getDefaultColor());

            backendHandler.updateGuildData(event.getGuild().getId(), guildData);
        }

        final String command = event.getMessage().getContentRaw().replace(guildData.getCommandInvoke(), "").split(" ")[0];

        if(command.equalsIgnoreCase("setup") && (event.getMember().isOwner() || event.getMember().hasPermission(Permission.MANAGE_SERVER))) {
            setupCommand.execute(event.getMessage(), guildData);
            backendHandler.addExecutedCommand();
            return;
        }

        if (!guildData.getAllowedChannels().isEmpty() && !guildData.getAllowedChannels().contains(event.getChannel().getId()))
            return;

        if(event.getMessage().getContentRaw().isEmpty())
            return;

        if (!(Character.toString(event.getMessage().getContentRaw().charAt(0)).equals(guildData.getCommandInvoke())))
            return;

        if (command.equalsIgnoreCase("inzidenz")) {
            incidenceCommand.execute(event.getMessage(), guildData);
            backendHandler.addExecutedCommand();
        }
        if (command.equalsIgnoreCase("corona")) {
            coronaCommand.execute(event.getMessage(), guildData);
            backendHandler.addExecutedCommand();
        }
        if (command.equalsIgnoreCase("info")) {
            infoCommand.execute(event.getMessage(), guildData);
            backendHandler.addExecutedCommand();
        }
        if (command.equalsIgnoreCase("help")) {
            helpCommand.execute(event.getMessage(), guildData);
            backendHandler.addExecutedCommand();
        }
    }

    public TextChannel getChannel(final Guild guild, final String input) {
        TextChannel targetChannel = null;

        final List<TextChannel> channels = guild.getTextChannelsByName(input, true);

        if (!channels.isEmpty())
            targetChannel = channels.get(0);

        if (targetChannel == null) {
            targetChannel = guild.getTextChannelById(
                    input.replace("<", "").replace(">", "")
                            .replace("#", ""));

            if (targetChannel == null) {
                try {
                    Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    return null;
                }
                targetChannel = guild.getTextChannelById(input);
            }
        }
        return targetChannel;
    }
}
