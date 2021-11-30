package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.coronabot.utils.objects.MapLegendInformation;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageEditor {

    private final File dataFolder;
    private final CoronaAPI coronaAPI;

    public ImageEditor(final File dataFolder, final CoronaAPI coronaAPI) {
        this.dataFolder = dataFolder;
        this.coronaAPI = coronaAPI;

        if(!dataFolder.exists())
            dataFolder.mkdir();
    }

    public File getTargetImage(final String url) {
        String currentDate = getCurrentDate();

        final File targetFileShould = new File(dataFolder, currentDate);
        if(targetFileShould.exists())
            return targetFileShould;

        try {
            final File tempFile = downloadFile(url, currentDate + "-s");

            if (tempFile == null)
                return null;

            final JSONObject jsonObject = coronaAPI.request(CoronaAPI.RESTAPIHOST + "map/districts/legend");

            if (jsonObject == null) {
                return tempFile;
            }

            final ArrayList<JSONObject> list = (ArrayList<JSONObject>) jsonObject.get("incidentRanges");
            final ArrayList<MapLegendInformation> mapLegendInformations = new ArrayList<>();

            for(JSONObject eachObject : list) {
                if(eachObject.get("max") != null) {
                    mapLegendInformations.add(new MapLegendInformation((int) (long) eachObject.get("min"),
                            (int) (long) eachObject.get("max"), eachObject.get("color").toString()));
                }
            }

            final File targetFile = editImage(tempFile, currentDate, mapLegendInformations);

            tempFile.delete();

            return targetFile;
        } catch(IOException exception) {
            return null;
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }

    public File downloadFile(final String link, final String name) throws IOException {
        URL url = new URL(link);
        InputStream inputStream = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        int i = 0;

        while(-1 != (i = inputStream.read(buf))) {
            outputStream.write(buf, 0, i);
        }

        outputStream.close();
        inputStream.close();
        byte[] response = outputStream.toByteArray();

        File targetFile = new File(dataFolder, name + ".png");

        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        fileOutputStream.write(response);
        fileOutputStream.close();

        return targetFile;
    }


    public File editImage(final File mainFile, final String name, final ArrayList<MapLegendInformation> list) throws IOException {
        // Main stuff
        final BufferedImage mainImage = ImageIO.read(mainFile);

        if (mainImage == null)
            return null;

        final BufferedImage bufferedImage = new BufferedImage(mainImage.getWidth(), mainImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        final Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

        graphics2D.drawImage(mainImage, 1, 2, null);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9F));

        writeText(graphics2D, list);

        final File file = new File(dataFolder, name + ".png");
        ImageIO.write(bufferedImage, "png", file);

        graphics2D.dispose();

        return file;
    }

    private void writeText(final Graphics2D graphics2D, final ArrayList<MapLegendInformation> list) {
        Integer count = 1;

        for(MapLegendInformation eachLegend : list) {
            graphics2D.setColor(Color.decode(eachLegend.getColor()));
            graphics2D.fillRect(0, (20 * count), 12, 12);

            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("Arial", Font.BOLD, 10));

            graphics2D.drawString(eachLegend.getMin() + (eachLegend.getMax() != -1 ? "-" + eachLegend.getMax() : ""),
                    16, ((20 * count) + 10));

            count++;
        }
    }

}
