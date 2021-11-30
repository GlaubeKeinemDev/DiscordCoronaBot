package de.glaubekeinemdev.coronabot.commands;

import de.glaubekeinemdev.coronabot.utils.CoronaAPI;
import de.glaubekeinemdev.coronabot.utils.CoronaEmbedBuilder;
import de.glaubekeinemdev.coronabot.utils.States;
import de.glaubekeinemdev.coronabot.utils.objects.IncidenceInformation;
import de.glaubekeinemdev.discordutilities.commands.core.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class IncidenceCommand extends Command {

    private final CoronaAPI coronaAPI;

    private final DecimalFormat decimalFormat = new DecimalFormat("##,###");

    public IncidenceCommand(String[] neededPermissionRoles, List<String> availableChannels, final CoronaAPI coronaAPI) {
        super(neededPermissionRoles, availableChannels);

        this.coronaAPI = coronaAPI;

        setArguments("<Stadt/Landkreis/Bundesland>");
    }

    @Override
    public List<String> alias() {
        return new ArrayList<>();
    }

    @Override
    public String commandName() {
        return "inzidenz";
    }

    @Override
    public String description() {
        return "Zeigt den Corona Inzidenzverlauf von Deutschland/Stadt/Landkreis/Bundesland an";
    }

    @Override
    public void execute(String[] args, String s, Member member, TextChannel textChannel, Message message) {
        if(args.length == 0) {
            final IncidenceInformation incidenceInformation = coronaAPI.getIncidenceInformation();

            if(incidenceInformation == null) {
                textChannel.sendMessage("Es ist ein Fehler unterlaufen. Bitte versuche es später erneut").queue();
                return;
            }

            getMessage(incidenceInformation, textChannel, member).queue();
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        int length = stringBuilder.length();
        stringBuilder.setLength(length - 1);

        if(States.getStateCode(stringBuilder.toString()) != null) {
            IncidenceInformation incidenceInformation = coronaAPI.getIncidenceStateInformation(stringBuilder.toString());

            if(incidenceInformation == null) {
                textChannel.sendMessage("Es ist ein Fehler unterlaufen. Bitte versuche es später erneut").queue();
                return;
            }

            getMessage(incidenceInformation, textChannel, member).queue();
            return;
        }

        IncidenceInformation incidenceInformation = coronaAPI.getIncidenceCityInformation(stringBuilder.toString());

        if(incidenceInformation == null) {
            textChannel.sendMessage("Es wurde keine Stadt/Landkreis/Gemeinde/Bundesland mit dem Namen `" + stringBuilder.toString() + "` gefunden").queue();
            return;
        }

        getMessage(incidenceInformation, textChannel, member).queue();
    }

    private MessageAction getMessage(final IncidenceInformation incidenceInformation, final TextChannel textChannel, final Member member) {
        final CoronaEmbedBuilder botEmbedBuilder = new CoronaEmbedBuilder("Inzidenzverlauf von " + incidenceInformation.getName());

        final StringBuilder stringBuilder = new StringBuilder();

        incidenceInformation.getIncidences().forEach(incidence -> {
            stringBuilder.append("Inzidenz: `").append(decimalFormat.format(incidence.getWeekIncidence())).append("`\n")
                    .append("Datum: `").append(coronaAPI.formatDate(incidence.getDate()).split(" ")[0]).append("`\n\n");
        });

        botEmbedBuilder.addField("Inzidenzverlauf:", stringBuilder.toString());
        botEmbedBuilder.addField("Letztes Update:", incidenceInformation.getLastUpdated());

        final File file = coronaAPI.getCoronaMap();

        botEmbedBuilder.setDefaultFooter(member);

        botEmbedBuilder.setThumbnail("attachment://" + file.getName());

        return textChannel.sendFile(file, file.getName()).embed(botEmbedBuilder.build());
    }

}
