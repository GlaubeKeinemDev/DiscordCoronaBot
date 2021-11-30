package de.glaubekeinemdev.coronabot.dailyupdates;

import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.objects.CoronaInformation;
import de.glaubekeinemdev.coronabot.utils.objects.VaccinationInformation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
    private final DailyUpdateInformation dailyUpdateInformation;
    private final CoronaAPI coronaAPI;

    private final JDA jda;

    private TextChannel textChannel;

    public DailyUpdateHelper(JDA jda, ScheduledExecutorService scheduledExecutorService, DailyUpdateInformation dailyUpdateInformation, CoronaAPI coronaAPI) {
        this.jda = jda;
        this.scheduledExecutorService = scheduledExecutorService;
        this.dailyUpdateInformation = dailyUpdateInformation;
        this.coronaAPI = coronaAPI;
    }

    public void start() {
        if(!dailyUpdateInformation.isEnabled()) {
            System.out.println("Daily updates is disabled it will not be used");
            return;
        }
        if(!availableTimes.contains(dailyUpdateInformation.getSendTime())) {
            System.out.println("Daily updates will be disbaled because the sendTime is not valid use number from "
                    + availableTimes.get(0) + " and " + (availableTimes.get(availableTimes.size() - 1)));
            return;
        }
        try {
            Long.parseLong(dailyUpdateInformation.getGuildId());
        } catch (NumberFormatException e) {
            System.out.println("Daily updates will be disabled because entered guild id is not valid");
            return;
        }

        final Guild guild = jda.getGuildById(dailyUpdateInformation.getGuildId());

        if(guild == null) {
            System.out.println("Daily updates will be disabled because entered guild id is not valid");
            return;
        }

        try {
            Long.parseLong(dailyUpdateInformation.getTextChannelId());
        } catch (NumberFormatException e) {
            System.out.println("Daily updates will be disabled because entered textchannel id is not valid");
            return;
        }

        textChannel = guild.getTextChannelById(dailyUpdateInformation.getTextChannelId());

        if(textChannel == null) {
            System.out.println("Daily updates will be disabled because entered textchannel id is not valid");
            return;
        }

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final String time = dateFormat.format(new Date());

                if (time.equals(dailyUpdateInformation.getSendTime())) {
                    sendUpdate();
                }
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    private void sendUpdate() {
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

            textChannel.sendFile(file, file.getName()).embed(botEmbedBuilder.build()).queue();
        } else {
            textChannel.sendMessage(botEmbedBuilder.build()).queue();
        }
    }

    private String getSimpleDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }
}
