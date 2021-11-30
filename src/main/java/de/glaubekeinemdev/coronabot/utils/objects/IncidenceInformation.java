package de.glaubekeinemdev.coronabot.utils.objects;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IncidenceInformation {
    private final String name;
    private final String lastUpdated;

    private final List<Incidence> incidences;

    public IncidenceInformation(String name, String lastUpdated, List<Incidence> incidences) {
        this.name = name;
        this.lastUpdated = lastUpdated;
        this.incidences = incidences;
    }

    public static IncidenceInformation parse(final JSONObject jsonObject, final String lastUpdated) {
        final String name = (jsonObject.containsKey("name") ? jsonObject.get("name").toString() : "Deutschland");
        final List<JSONObject> list = (List<JSONObject>) (jsonObject.containsKey("data") ? jsonObject.get("data") : jsonObject.get("history"));
        final List<Incidence> incidences = new ArrayList<>();

        list.forEach(eachIncidenceObject -> {
            incidences.add(new Incidence((double) eachIncidenceObject.get("weekIncidence"), eachIncidenceObject.get("date").toString()));
        });

        return new IncidenceInformation(name, lastUpdated, incidences);
    }

    public String getName() {
        return name;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public List<Incidence> getIncidences() {
        return incidences;
    }
}
