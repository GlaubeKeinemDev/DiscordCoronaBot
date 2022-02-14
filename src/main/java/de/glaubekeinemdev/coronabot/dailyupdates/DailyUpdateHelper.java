package de.glaubekeinemdev.coronabot.dailyupdates;

import de.glaubekeinemdev.coronabot.database.BackendHandler;
import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import de.glaubekeinemdev.coronabot.utils.objects.CoronaInformation;
import de.glaubekeinemdev.coronabot.utils.objects.VaccinationInformation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DailyUpdateHelper {

    private final DecimalFormat decimalFormat = new DecimalFormat("##,###");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("kk");
    private final List<String> availableTimes = Arrays.asList("01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24");

    private final ScheduledExecutorService scheduledExecutorService;
    private final CoronaAPI coronaAPI;
    private final BackendHandler backendHandler;

    private final JDA jda;

    public DailyUpdateHelper(JDA jda, ScheduledExecutorService scheduledExecutorService, CoronaAPI coronaAPI, BackendHandler backendHandler) {
        this.jda = jda;
        this.scheduledExecutorService = scheduledExecutorService;
        this.coronaAPI = coronaAPI;
        this.backendHandler = backendHandler;

        start();
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final String time = dateFormat.format(new Date());

            System.out.println("0");

            jda.getGuilds().forEach(eachGuild -> {
                GuildData guildData = backendHandler.getGuildData(eachGuild.getId());

                System.out.println("1");

                if(guildData == null) {
                    guildData = new GuildData(eachGuild.getId(), "!", new ArrayList<>(), new DailyUpdateInformation());

                    backendHandler.updateGuildData(eachGuild.getId(), guildData);
                }

                System.out.println("2");

                if(!guildData.getDailyUpdateInformation().isEnabled())
                    return;

                System.out.println("3");

                if(!availableTimes.contains(guildData.getDailyUpdateInformation().getSendTime()))
                    return;

                System.out.println("4");

                try {
                    Long.parseLong(guildData.getDailyUpdateInformation().getTextChannelId());
                } catch (NumberFormatException e) {
                    System.out.println("Daily updates will be disabled because entered textchannel id is not valid | Guild: " + guildData.getId());
                    return;
                }

                System.out.println("5");

                final TextChannel textChannel = eachGuild.getTextChannelById(guildData.getDailyUpdateInformation().getTextChannelId());

                if(textChannel == null)
                    return;

                System.out.println("6");

                if (time.equals(guildData.getDailyUpdateInformation().getSendTime())) {
                    System.out.println("7");
                    sendUpdate(textChannel);
                }
            });
        }, 5, 1, TimeUnit.HOURS);
    }

    private void sendUpdate(final TextChannel textChannel) {
        final VaccinationInformation vaccinationInformation = coronaAPI.getVaccinationInformation();
        final CoronaInformation coronaInformation = coronaAPI.getInformation();
        final CoronaEmbedBuilder botEmbedBuilder = new CoronaEmbedBuilder("Coronazahlen am " + getSimpleDate());

        botEmbedBuilder.addField("Infektionszahlen:",
                "\uD83E\uDDA0 Infizierte: `" + decimalFormat.format(coronaInformation.getCases()) + "` (+" + decimalFormat.format(coronaInformation.getCasesLast24Hours()) + ")\n" +
                        "☠️ Todesfälle: `" + decimalFormat.format(coronaInformation.getDeaths()) + "` (+" + decimalFormat.format(coronaInformation.getDeathsLast24Hours()) + ")\n" +
                        "\uD83C\uDFE5 Genesene: `" + decimalFormat.format(coronaInformation.getRecovered()) + "` (+" + decimalFormat.format(coronaInformation.getRecoveredLast24Hours()) + ")\n" +
                        "\uD83E\uDE84 Inzidenz: `" + decimalFormat.format(coronaInformation.getWeekIncidence()) + "`" + ((coronaInformation.getLastIncidence() == -1 || coronaInformation.getLastIncidence() == coronaInformation.getWeekIncidence()) ? "" : " (" + coronaInformation.getLastIncidence() + ")") + "\n" +
                        (coronaInformation.getrValue() == null ? "" : "↘ Reproduktionsfaktor: `" + coronaInformation.getrValue()) + "`");

        botEmbedBuilder.addField("Impfzahlen:",
                "\uD83D\uDC89 Verabreichte Dosen: `" + decimalFormat.format(vaccinationInformation.getTotalDosesGiven()) + "` (+" + decimalFormat.format(vaccinationInformation.getVaccinatedLast24Hours()) + ")\n" +
                        "\uD83D\uDEE1 Vollständig geimpfte: `" + decimalFormat.format(vaccinationInformation.getVaccinated()) + "` (" + vaccinationInformation.getSecondVaccinationInformation().getQuote() + "%)\n" +
                        "✅ Einmal geimpft: `" + decimalFormat.format(vaccinationInformation.getFirstDose() - (vaccinationInformation.getVaccinated() - vaccinationInformation.getFirstVaccinationInformation().getJannsen())) + "`\n" +
                        "\uD83D\uDC6A Impfquote bei 80%: `" + coronaAPI.getVaccinationQuoteDay(coronaInformation, vaccinationInformation, 0.80) + "`"
        );

        final File file = coronaAPI.getCoronaMap();

        botEmbedBuilder.setFooter(botEmbedBuilder.getCurrentDate());

        if (file != null) {
            botEmbedBuilder.setThumbnail("attachment://" + file.getName());

            textChannel.sendFile(file, file.getName()).setEmbeds(botEmbedBuilder.build()).queue();
        } else {
            textChannel.sendMessageEmbeds(botEmbedBuilder.build()).queue();
        }
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    private String getSimpleDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }
}
