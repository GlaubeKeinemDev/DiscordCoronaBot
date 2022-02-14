package de.glaubekeinemdev.coronabot.commands.user;

import de.glaubekeinemdev.coronabot.CoronaBot;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class InfoCommand {

    private final DecimalFormat decimalFormat;

    public InfoCommand() {
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        this.decimalFormat = decimalFormat;
    }

    public void execute(final Message message) {

        final CoronaEmbedBuilder coronaEmbedBuilder = new CoronaEmbedBuilder("Informationen");

        coronaEmbedBuilder.setDescription("Unser Bot gibt dir tagtäglich die aktuellsten Coronazahlen, egal ob deutschlandweit, in deinem Bundesland oder in deinem Stadt-/Landkreis. " +
                "Dabei versuchen wir dir viele Daten und Vorhersagen trotzdem übersichtlich anzuzeigen.\n\n** **");
        coronaEmbedBuilder.addField("Datenquellen", "Bei unseren Datenquellen handelt es sich um die Interdisziplinäre Vereinigung für Intensiv- und Notfallmedizin (DIVI) e.V. und das RKI." +
                "Das DIVI verwenden wir für Daten um die Auslastung der Invensivstationen auszulesen. Das RKI verwenden wir für Neuinfektionen, Inzidenz und Impfzahlen\n\n** **");

        coronaEmbedBuilder.addField("Links",
                "[• Website](https://covid-bot.de)\n" +
                "[• Bot einladen](https://discord.com/api/oauth2/authorize?client_id=937118194802524241&permissions=125952&scope=bot)\n" +
                "[• DIVI](https://www.divi.de)\n" +
                "[• RKI](https://www.rki.de/DE/Home/homepage_node.html)", true);



        coronaEmbedBuilder.addField("Statistiken",
                "• " + decimalFormat.format(message.getJDA().getGuilds().size()) + " Server, " +
                        decimalFormat.format(CoronaBot.getInstance().getJda().getGuilds().stream().flatMapToInt(
                                guild -> IntStream.of(guild.getMembers().size())).sum()) + " Nutzer\n" +
                        "• Ping: " + getPing() + "ms\n" +
                        "• Uptime: " + getUptime(), true);


        int members = 0;

        for (Guild guild : message.getJDA().getGuilds()) {
            members += guild.getMembers().size();
        }


        message.getTextChannel().sendMessageEmbeds(coronaEmbedBuilder.build()).queue();
    }

    private long getPing() {
        try {
            InetAddress host = InetAddress.getByName("discord.com");
            long nanoTime = System.nanoTime();
            Socket socket = new Socket(host, 80);
            socket.close();
            return (System.nanoTime() - nanoTime) / 1000000;
        } catch (IOException ex) {
            Logger.getLogger(InfoCommand.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public String getUptime() {
        long time = System.currentTimeMillis() - CoronaBot.getInstance().getStartedTime();

        long seconds = (time / 1000L) % 60;
        long minutes = (time / 60000L % 60L);
        long hours = (time / 3600000L) % 24;
        long days = (time / 86400000L);

        String onlineTime = "";
        if (days > 0) {
            onlineTime += days + " Tage, ";
        }
        if (hours > 0) {
            onlineTime += hours + " Stunden, ";
        }
        if (minutes > 0) {
            onlineTime += minutes + " Minuten";
        }
        if (seconds > 0 && days == 0) {
            onlineTime += ", " + seconds + " Sekunden";
        }

        return onlineTime;
    }

}
