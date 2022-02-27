package de.glaubekeinemdev.coronabot.commands.user;

import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.GuildData;
import de.glaubekeinemdev.coronabot.utils.States;
import de.glaubekeinemdev.coronabot.utils.objects.CoronaInformation;
import de.glaubekeinemdev.coronabot.utils.objects.IntensiveBedInformation;
import de.glaubekeinemdev.coronabot.utils.objects.TestInformation;
import de.glaubekeinemdev.coronabot.utils.objects.VaccinationInformation;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.decimal4j.util.DoubleRounder;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoronaCommand {

    private final CoronaAPI coronaAPI;

    private final DecimalFormat decimalFormat = new DecimalFormat("##,###");

    public CoronaCommand(CoronaAPI coronaAPI) {
        this.coronaAPI = coronaAPI;
    }

    public void execute(final Message message, final GuildData guildData) {
        String[] args = new String[]{};
        String[] messageSplitted = message.getContentRaw().split(" ");

        if(!message.getContentRaw().replace(messageSplitted[0], "").isEmpty()) {
            final String line = message.getContentRaw();
            args = line.replace(messageSplitted[0], "").substring(1).split(" ");
        }

        if (args.length == 0) {
            final CoronaInformation coronaInformation = coronaAPI.getInformation();
            final VaccinationInformation vaccinationInformation = coronaAPI.getVaccinationInformation();
            final TestInformation testInformation = coronaAPI.getTestInformation();
            final IntensiveBedInformation intensiveBedInformation = coronaAPI.getIntensiveBedInformation();

            if (coronaInformation == null || vaccinationInformation == null || testInformation == null) {
                message.getTextChannel().sendMessage("Es ist ein Fehler unterlaufen." +
                        " Bitte versuche es später erneut").queue();
                return;
            }

            getMessage(coronaInformation, vaccinationInformation, testInformation, intensiveBedInformation,
                    message.getTextChannel(), message.getMember(), guildData).queue();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        int length = stringBuilder.length();
        stringBuilder.setLength(length - 1);

        if (States.getStateCode(stringBuilder.toString()) != null) {
            final CoronaInformation coronaInformation = coronaAPI.getStateInformation(stringBuilder.toString());
            final VaccinationInformation vaccinationInformation =
                    coronaAPI.getStateVaccinationInformation(stringBuilder.toString());
            if (coronaInformation == null || vaccinationInformation == null) {
                message.getTextChannel().sendMessage("Es ist ein Fehler unterlaufen. " +
                        "Bitte versuche es später erneut").queue();
                return;
            }

            getMessage(coronaInformation, vaccinationInformation, null, null,
                    message.getTextChannel(), message.getMember(), guildData).queue();
            return;
        }

        CoronaInformation coronaInformation = coronaAPI.getCityInformation(stringBuilder.toString());

        if (coronaInformation == null) {
            message.getTextChannel().sendMessage("Es wurde keine Stadt/Landkreis/Gemeinde/Bundesland mit dem Namen `"
                    + stringBuilder + "` gefunden").queue();
            return;
        }

        getMessage(coronaInformation, null, null, null,
                message.getTextChannel(), message.getMember(), guildData).queue();
    }


    private MessageAction getMessage(final CoronaInformation coronaInformation,
                                     final VaccinationInformation vaccinationInformation,
                                     final TestInformation testInformation,
                                     final IntensiveBedInformation intensiveBedInformation,
                                     final TextChannel textChannel, final Member member, final GuildData guildData) {
        final CoronaEmbedBuilder botEmbedBuilder = new CoronaEmbedBuilder("Corona in "
                + coronaInformation.getName() + " am " + getSimpleDate());

        final String runningInfections = decimalFormat.format((coronaInformation.getCases() -
                (coronaInformation.getRecovered() + coronaInformation.getDeaths())));
        final String casesLast24Hours = decimalFormat.format(coronaInformation.getCasesLast24Hours());
        final String deaths = decimalFormat.format(coronaInformation.getDeaths());
        final String deathsLast24Hours = decimalFormat.format(coronaInformation.getDeathsLast24Hours());
        final String recovered = decimalFormat.format(coronaInformation.getRecovered());
        final String recoveredLast24Hours = decimalFormat.format(coronaInformation.getRecoveredLast24Hours());

        botEmbedBuilder.addField("Stastistiken:",
                "\uD83E\uDDA0 Aktiv infiziert: `" + runningInfections + "`\n" +
                        "☠️ Todesfälle: `" + deaths + "` (+" + deathsLast24Hours + ")\n" +
                        "\uD83C\uDFE5 Genesen: `" + recovered + "` (+" + recoveredLast24Hours + ")\n" +
                        "\n" +
                        "⚠️ Neuinfektionen: `+" + casesLast24Hours + "`\n" +
                        (coronaInformation.getrValue() == null ? "" : "↘ Reproduktionsfaktor: `"
                                + coronaInformation.getrValue() + "`\n") +
                        "\n" +
                        "\uD83E\uDE84 Inzidenz: `" + decimalFormat.format(coronaInformation.getWeekIncidence()) + "`\n");

        if (vaccinationInformation == null) {
            botEmbedBuilder.addField("Impfstatus:",
                    "Impfinformationen sind für Städte/Landkreise nicht verfügbar");
        } else {
            final double vaccinatedQuote = vaccinationInformation.getFirstVaccinationInformation().getQuote();
            final double vaccinatedQuoteLastDay = calculateQuoteLastDay(coronaInformation.getPopulation(),
                    vaccinatedQuote, vaccinationInformation.getFirstVaccinationInformation().getVaccinatedLast24Hours());
            final double fullVaccinatedQuote = vaccinationInformation.getSecondVaccinationInformation().getQuote();
            final double fullVaccinatedQuoteLastDay = calculateQuoteLastDay(coronaInformation.getPopulation(),
                    fullVaccinatedQuote,
                    vaccinationInformation.getSecondVaccinationInformation().getVaccinatedLast24Hours());
            final String vaccinationsLast24Hours = decimalFormat.format(vaccinationInformation.getVaccinatedLast24Hours());
            final String firstVaccination = decimalFormat.format(vaccinationInformation
                    .getFirstVaccinationInformation().getVaccinatedLast24Hours());
            final String secondVaccination = decimalFormat.format(vaccinationInformation
                    .getSecondVaccinationInformation().getVaccinatedLast24Hours());
            final String boosterVaccination = decimalFormat.format(vaccinationInformation
                    .getBoosterVaccinationInformation().getVaccinatedLast24Hours());


            botEmbedBuilder.addField("Impfstatus:",
                    "✅ Momentan geimpft: `" + vaccinatedQuote +
                            "%` (+" + DoubleRounder.round((vaccinatedQuote - vaccinatedQuoteLastDay), 1)
                            + "%)\n" +
                            "\uD83D\uDEE1 Davon vollständig: `" + fullVaccinatedQuote + "%` (+"
                            + DoubleRounder.round((fullVaccinatedQuote - fullVaccinatedQuoteLastDay), 1)
                            + "%)\n" +
                            "\n" +
                            "\uD83D\uDC89 Neue Impfungen: `+" + vaccinationsLast24Hours + "`\n" +
                            "   \uD83D\uDD39 Erstimpfung: `" + firstVaccination + "`\n" +
                            "   \uD83D\uDD39 Zweitimpfung: `" + secondVaccination + "`\n" +
                            "   \uD83D\uDD39 Boosterimpfung: `" + boosterVaccination + "`\n" +
                            "\n" +
                            (coronaInformation.getName().equals("Deutschland") ? "\uD83D\uDC6A Impfquote bei 85%: `"
                                    + coronaAPI.getVaccinationQuoteDay(coronaInformation,
                                    vaccinationInformation, 0.85) + "`\n" : ""));
        }

        if (intensiveBedInformation != null) {
            final int intensiveBeds = intensiveBedInformation.getBedsFree() + intensiveBedInformation.getBedsUsed();
            final String bedsUsed = decimalFormat.format(intensiveBedInformation.getBedsUsed());
            final String bedsFree = decimalFormat.format(intensiveBedInformation.getBedsFree());
            final double usedBedsQuote = DoubleRounder.round(((((double) intensiveBedInformation.getBedsUsed())
                    / ((double) intensiveBeds)) * 100.0D), 2);
            final double covidPatientsQuote = DoubleRounder.round(((((double) intensiveBedInformation.getCovidPatients())
                    / ((double) intensiveBedInformation.getBedsUsed())) * 100.0D), 2);
            final String covidPatients = decimalFormat.format(intensiveBedInformation.getCovidPatients());
            final String ventilatedPatients = decimalFormat.format(intensiveBedInformation.getCovidPatientsVentilated());
            final double ventilatedPatientsQuote = DoubleRounder.round(((((double) intensiveBedInformation
                    .getCovidPatientsVentilated()) / ((double) intensiveBedInformation.getCovidPatients())) * 100.0D),
                    2);
            final double bedsFreePerLocation = DoubleRounder.round((intensiveBedInformation.getBedsFreePerLocation()),
                    2);

            botEmbedBuilder.addField("Auslastung der Intensivstationen:",
                    "\uD83D\uDECC Belegt: `" + bedsUsed + " / " + decimalFormat.format(intensiveBeds) +
                            "` (" + usedBedsQuote + "% | " + bedsFree + " frei)\n" +
                            "\n" +
                            "\uD83E\uDDA0 Davon Corona: `" + covidPatients + "` (" + covidPatientsQuote + "%)\n" +
                            "   \uD83D\uDD38 Davon beatmet: `" + ventilatedPatients +
                            "` (" + ventilatedPatientsQuote + "%)\n" +
                            "   \uD83D\uDD39 Betten frei pro Standort: `" + bedsFreePerLocation + "`\n");
        }

        if (testInformation != null) {
            final String performedTests = decimalFormat.format(testInformation.getPerformedTests());
            final String positiveTests = decimalFormat.format(testInformation.getPositiveTests());
            final String positiveTestsQuote = decimalFormat.format(testInformation.getPositivityRate() * 100);

            botEmbedBuilder.addField("PCR-Tests:",
                    "\uD83E\uDDEA Durchgeführte Tests: `" + performedTests + "`\n" +
                            "\uD83E\uDDA0 Positive Tests: `" + positiveTests + "` (" + positiveTestsQuote + "%)\n");

        }

        final File file = coronaAPI.getCoronaMap();

        botEmbedBuilder.setDefaultFooter(member);
        botEmbedBuilder.setColor(guildData.getColor());

        if (file == null) {
            return textChannel.sendMessageEmbeds(botEmbedBuilder.build());
        } else {
            botEmbedBuilder.setThumbnail("attachment://" + file.getName());
            return textChannel.sendFile(file, file.getName()).setEmbeds(botEmbedBuilder.build());
        }
    }

    private double calculateQuoteLastDay(int population, double currentQuote, int vaccinatedLast24Hours) {
        final double actualQuote = currentQuote / 100.0D;
        final int populationQuote = (int) (population * actualQuote);
        final int populationQuoteLastDay = populationQuote - vaccinatedLast24Hours;
        final double quoteLastDay = ((double) populationQuoteLastDay / population) * 100;

        final String quoteString = String.valueOf(quoteLastDay);
        final String floatingValue = quoteString.split("\\.")[1];

        int firstCharacter = Integer.parseInt(String.valueOf(floatingValue.charAt(0)));
        final int secondCharacter = Integer.parseInt(String.valueOf(floatingValue.charAt(1)));

        if (secondCharacter > firstCharacter)
            firstCharacter++;

        final double finalQuote = Double.parseDouble(quoteString.split("\\.")[0] + "." + firstCharacter);

        return finalQuote;
    }

    private String getSimpleDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }

}
