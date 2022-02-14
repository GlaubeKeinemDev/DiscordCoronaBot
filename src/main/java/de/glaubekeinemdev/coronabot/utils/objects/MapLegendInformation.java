package de.glaubekeinemdev.coronabot.utils.objects;

import java.awt.*;

public class MapLegendInformation {

    private final Integer min;
    private final Integer max;
    private final String color;

    public MapLegendInformation(Integer min, Integer max, String color) {
        this.min = min;
        this.max = max;
        this.color = color;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public String getColor() {
        return color;
    }

    public Color getDecodedColor() {
        return Color.decode(this.color);
    }
}
