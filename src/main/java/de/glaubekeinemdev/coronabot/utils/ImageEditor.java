package de.glaubekeinemdev.coronabot.utils;

import de.glaubekeinemdev.coronabot.utils.objects.MapLegendInformation;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageEditor {

    private final File dataFolder;
    private final CoronaAPI coronaAPI;

    // 0 - 50 #00FF59 green
    private final Color colorOne = Color.decode("#00FF59");
    // 50 - 100 #009434 dark green
    private final Color colorTwo = Color.decode("#009434");
    // 100 - 200 #C1FF00 yellow
    private final Color colorThree = Color.decode("#C1FF00");
    // 200 - 500 #A2A000 dark yellow
    private final Color colorFour = Color.decode("#DC6B00");
    // 500 - 1000 #DC6B00 light red
    private final Color colorFive = Color.decode("#FF0000");
    // 1000 - 1500 #FF0000 red
    private final Color colorSix = Color.decode("#6C0000");
    // > 1500 #000000 black
    private final Color colorSeven = Color.decode("#000000");

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

        graphics2D.dispose();

        final BufferedImage editedImage = editColors(list, bufferedImage);

        final File file = new File(dataFolder, name + ".png");
        ImageIO.write(editedImage, "png", file);


        return file;
    }

    private void writeText(final Graphics2D graphics2D, final ArrayList<MapLegendInformation> list) {
        final int downwardMultiplier = 25;

        for (int i = 1; i < 8; i++) {
            String text = "";

            if (i == 1) {
                graphics2D.setColor(colorOne);
                text = "0-50";
            }
            if (i == 2) {
                graphics2D.setColor(colorTwo);
                text = "50-100";
            }
            if (i == 3) {
                graphics2D.setColor(colorThree);
                text = "100-200";
            }
            if (i == 4) {
                graphics2D.setColor(colorFour);
                text = "200-500";
            }
            if (i == 5) {
                graphics2D.setColor(colorFive);
                text = "500-1000";
            }
            if (i == 6) {
                graphics2D.setColor(colorSix);
                text = "1000-1500";
            }
            if (i == 7) {
                graphics2D.setColor(colorSeven);
                text = "> 1500";
            }

            graphics2D.fillRect(0, (downwardMultiplier * i), 15, 15);

            graphics2D.setColor(Color.WHITE);
            graphics2D.setFont(new Font("Arial", Font.BOLD, 14));
            graphics2D.drawString(text, 20, ((downwardMultiplier * i) + 12));
        }
    }

    private BufferedImage editColors(ArrayList<MapLegendInformation> list, BufferedImage bufferedImage) {
        BufferedImage finalImage = bufferedImage;

        for (MapLegendInformation index : list) {
            if (index.getMin() < 50 && index.getMax() <= 50) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorOne), null).filter(finalImage, null);
            }
            if (index.getMin() == 50 && index.getMax() == 100) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorTwo), null).filter(finalImage, null);
            }
            if (index.getMin() == 100 && index.getMax() == 200) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorThree), null).filter(finalImage, null);
            }
            if (index.getMin() >= 200 && index.getMax() <= 500) {
                    final Color effectiveColor = Color.decode("#5e189b");
                    finalImage = new LookupOp(new ColorMapper(effectiveColor, colorFour), null).filter(finalImage, null);
                    finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorFour), null).filter(finalImage, null);
            }
            if (index.getMin() == 500 && index.getMax() == 1000) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorFive), null).filter(finalImage, null);
            }
            if (index.getMin() == 1000 && index.getMax() == 1500) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorSix), null).filter(finalImage, null);
            }
            if (index.getMin() >= 1500) {
                finalImage = new LookupOp(new ColorMapper(index.getDecodedColor(), colorSeven), null).filter(finalImage, null);
            }
        }

        return finalImage;
    }

}
