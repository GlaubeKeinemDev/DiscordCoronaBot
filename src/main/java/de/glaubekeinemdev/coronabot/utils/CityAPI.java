package de.glaubekeinemdev.coronabot.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class CityAPI {

    private final File file;
    private CityCache cityCache;

    public CityAPI(final File dataFolder) {
        this.file = new File(dataFolder, "cities.json");
    }

    public void init() {
        downloadCities();

        if (!this.file.exists()) {
            downloadCities();
        } else {
            try {
                cityCache = new Gson().fromJson(new FileReader(file), CityCache.class);
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    public String getCityCode(final String city) {
        return cityCache.getCode(city);
    }

    public void downloadCities() {
        try {
            final URL url = new URL(CoronaAPI.RESTAPIHOST + "districts");

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setUseCaches(false);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final JSONParser parser = new JSONParser();
            final JSONObject jsonObject = (JSONObject) ((JSONObject) parser.parse(read(reader))).get("data");


            final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

            jsonObject.keySet().forEach(keys -> map.put(((JSONObject) jsonObject.get(keys))
                    .get("name").toString(), keys.toString()));

            cityCache = new CityCache(map);
            cityCache.save(file);

        } catch(final Exception exception) {
            exception.printStackTrace();
        }
    }

    private String read(final BufferedReader reader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        int i;
        while((i = reader.read()) != -1) {
            stringBuilder.append((char) i);
        }
        return stringBuilder.toString();
    }

    private class CityCache {

        private final ConcurrentHashMap<String, String> cache;

        public CityCache(ConcurrentHashMap<String, String> cache) {
            this.cache = cache;
        }

        public String getCode(final String key) {
            if(!cache.containsKey(key))
                return null;

            return cache.get(key);
        }

        public void save(final File file) throws IOException {
            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
            fileWriter.flush();
            fileWriter.close();
        }
    }

}
