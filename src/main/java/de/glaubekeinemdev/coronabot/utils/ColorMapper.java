package de.glaubekeinemdev.coronabot.utils;

import java.awt.*;
import java.awt.image.LookupTable;
import java.util.Arrays;

public class ColorMapper extends LookupTable {

    private final int[] from;
    private final int[] to;

    public ColorMapper(final Color from, final Color to) {
        super(0, 4);

        this.from = new int[]{
                from.getRed(),
                from.getGreen(),
                from.getBlue(),
                from.getAlpha(),
        };

        this.to = new int[]{
                to.getRed(),
                to.getGreen(),
                to.getBlue(),
                to.getAlpha(),
        };
    }

    @Override
    public int[] lookupPixel(int[] source, int[] destination) {
        if (destination == null)
            destination = new int[source.length];

        int[] newColor = (Arrays.equals(source, from) ? to : source);
        System.arraycopy(newColor, 0, destination, 0, newColor.length);

        return destination;
    }

}
