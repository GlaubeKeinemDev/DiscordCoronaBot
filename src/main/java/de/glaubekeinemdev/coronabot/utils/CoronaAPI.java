package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.coronabot.utils.objects.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CoronaAPI {

    public static String RESTAPIHOST;

    private final CityAPI cityAPI;
    private ImageEditor imageEditor;

    public CoronaAPI(CityAPI cityAPI) {
        this.cityAPI = cityAPI;
    }

    public boolean init(final String restHost) {
        if (restHost == null) {
            System.out.println("[CoronaModul] konnte keine Verbindung zu einem RestServer herstellen!");
            return false;
        }

        try {
            URL url = new URL(restHost);
            url.openConnection();
        } catch (IOException e) {
            System.out.println("[CoronaModul] konnte keine Verbindung zu einem RestServer herstellen!");
            return false;
        }

        RESTAPIHOST = restHost;
        return true;
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }

    public CoronaInformation getInformation() {
        final JSONObject jsonObject = request(RESTAPIHOST + "germany");

        if (jsonObject == null)
            return null;

        final IncidenceInformation incidenceInformation = getIncidenceInformation();
        int lastIncidence = -1;

        if (incidenceInformation != null) {
            final List<Incidence> list = incidenceInformation.getIncidences();

            lastIncidence = (int) list.get(list.size() - 1).getWeekIncidence();
        }

        return CoronaInformation.parse(jsonObject, lastIncidence, formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public VaccinationInformation getVaccinationInformation() {
        final JSONObject jsonObject = request(RESTAPIHOST + "vaccinations");

        if (jsonObject == null)
            return null;

        return VaccinationInformation.parse((JSONObject) jsonObject.get("data"), formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public CoronaInformation getCityInformation(final String city) {
        final String cityCode = cityAPI.getCityCode(city);

        if (cityCode == null)
            return null;

        final JSONObject jsonObject = request(RESTAPIHOST + "districts/" + cityCode);

        if (jsonObject == null)
            return null;

        final IncidenceInformation incidenceInformation = getIncidenceCityInformation(city);
        int lastIncidence = -1;

        if (incidenceInformation != null) {
            final List<Incidence> list = incidenceInformation.getIncidences();

            lastIncidence = (int) list.get(list.size() - 1).getWeekIncidence();
        }

        return CoronaInformation.parse((JSONObject) ((JSONObject) jsonObject.get("data")).get(cityCode), lastIncidence, formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public CoronaInformation getStateInformation(final String state) {
        final String stateCode = States.getStateCode(state);

        if (stateCode == null)
            return null;

        final JSONObject jsonObject = request(RESTAPIHOST + "states/" + stateCode);

        if (jsonObject == null)
            return null;

        final IncidenceInformation incidenceInformation = getIncidenceStateInformation(state);
        int lastIncidence = -1;

        if (incidenceInformation != null) {
            final List<Incidence> list = incidenceInformation.getIncidences();

            lastIncidence = (int) list.get(list.size() - 1).getWeekIncidence();
        }

        return CoronaInformation.parse((JSONObject) ((JSONObject) jsonObject.get("data")).get(stateCode), lastIncidence,
                formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public IncidenceInformation getIncidenceInformation() {
        final JSONObject jsonObject = request(RESTAPIHOST + "germany/history/incidence/7");

        if (jsonObject == null)
            return null;

        return IncidenceInformation.parse(jsonObject, formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public IncidenceInformation getIncidenceStateInformation(final String state) {
        final String stateCode = States.getStateCode(state);

        if (stateCode == null)
            return null;

        final JSONObject jsonObject = request(RESTAPIHOST + "states/" + stateCode + "/history/incidence/7");

        if (jsonObject == null)
            return null;

        return IncidenceInformation.parse((JSONObject) ((JSONObject) jsonObject.get("data")).get(stateCode),
                formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public IncidenceInformation getIncidenceCityInformation(final String city) {
        final String cityCode = cityAPI.getCityCode(city);

        if (cityCode == null)
            return null;

        final JSONObject jsonObject = request(RESTAPIHOST + "districts/" + cityCode + "/history/incidence/7");

        if (jsonObject == null)
            return null;

        return IncidenceInformation.parse((JSONObject) ((JSONObject) jsonObject.get("data")).get(cityCode),
                formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public TestInformation getTestInformation() {
        final JSONObject jsonObject = request(RESTAPIHOST + "testing/history");

        if (jsonObject == null)
            return null;

        final ArrayList<JSONObject> list = (ArrayList<JSONObject>) ((JSONObject) jsonObject.get("data")).get("history");

        return TestInformation.parse(list.get((list.size() - 1)), formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public VaccinationInformation getStateVaccinationInformation(final String state) {
        final String stateCode = States.getStateCode(state);

        if (stateCode == null)
            return null;

        final JSONObject jsonObject = request(RESTAPIHOST + "vaccinations");

        if (jsonObject == null)
            return null;

        return VaccinationInformation.parse((JSONObject) ((JSONObject) ((JSONObject) jsonObject.get("data"))
                .get("states")).get(stateCode), formatDate(((JSONObject) jsonObject.get("meta")).get("lastUpdate").toString()));
    }

    public int getAverageVaccinationsPerDay() {
        final JSONObject jsonObject = request(RESTAPIHOST + "vaccinations/history/7");

        if (jsonObject == null)
            return 10000;

        final List<JSONObject> list = (List<JSONObject>) ((JSONObject) jsonObject.get("data")).get("history");

        int vaccinations = 0;

        for (JSONObject object : list) {
            vaccinations = vaccinations + (int) (long) object.get("firstVaccination");
            vaccinations = vaccinations + (int) (long) object.get("secondVaccination");
//            vaccinations = vaccinations + (int) (long) object.get("boosterVaccination");
        }

        return vaccinations / list.size();
    }

    public IntensiveBedInformation getIntensiveBedInformation() {
        final JSONObject jsonObject = request("https://www.intensivregister.de/api/public/reporting/laendertabelle?format=json&onlyErwachsenenBetten=true");

        if (jsonObject == null)
            return null;

        return IntensiveBedInformation.parse(jsonObject);
    }

    public int getPopulation() {
        final JSONObject jsonObject = request(RESTAPIHOST + "germany");

        // Return round about 83 million people if rest-server is not working
        if (jsonObject == null)
            return 83000000;

        final double weekIncidence = (double) jsonObject.get("weekIncidence");
        final double casesPerWeek = (double) (long) jsonObject.get("casesPerWeek");

        return (int) ((casesPerWeek * 100000) / weekIncidence);
    }

    public String getVaccinationQuoteDay(final CoronaInformation coronaInformation, final VaccinationInformation vaccinationInformation, final double quote) {
        final int mustVaccinated = (int) ((coronaInformation.getPopulation() * quote) - vaccinationInformation.getVaccinated()) * 2;
        final int dailyVaccinations = getAverageVaccinationsPerDay();

        final int days = (mustVaccinated / dailyVaccinations);

        long millies = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millies);

        return new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());
    }

    public File getCoronaMap() {
        return imageEditor.getTargetImage(RESTAPIHOST + "map/districts");
    }

    public JSONObject request(final String requestUrl) {
        try {
            final URL url = new URL(requestUrl);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setUseCaches(false);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(read(reader));
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String read(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        int i;
        while ((i = reader.read()) != -1) {
            stringBuilder.append((char) i);
        }
        return stringBuilder.toString();
    }

    public String formatDate(final String input) {
        String formattedDate = "";

        final String[] date = input.split("T")[0].split("-");

        formattedDate = date[2] + "." + date[1] + "." + date[0];

        formattedDate = formattedDate + " " + (input.split("T")[1]).split("\\.")[0];

        return formattedDate;
    }
}
