package de.glaubekeinemdev.coronabot.utils;

public enum States {

    SCHLESWIGHOLSTEIN("SH", "SCHLESWIG_HOLSTEIN"),
    HAMBURG("HH", "HAMBURG"),
    NIEDERSACHSEN("NI", "NIEDERSACHSEN"),
    BREMEN("HB", "BREMEN"),
    NORDRHEINWESTFALEN("NW", "NORDRHEIN_WESTFALEN"),
    HESSEN("HE", "HESSEN"),
    RHEINLANDPFALZ("RP", "RHEINLAND_PFALZ"),
    BADENWÜRTTEMBERG("BW", "BADEN_WUERTTEMBERG"),
    BAYERN("BY", "BAYERN"),
    SAARLAND("SL", "SAARLAND"),
    BERLIN("BE", "BERLIN"),
    BRANDENBURG("BB", "BRANDENBURG"),
    MECKLENBURGVORPOMMERN("MV", "MECKLENBURG_VORPOMMERN"),
    SACHSEN("SN", "SACHSEN"),
    SACHSENANHALT("ST", "SACHSEN_ANHALT"),
    THÜRINGEN("TH", "THUERINGEN");

    private final String code;
    private final String name;

    States(final String code, final String name) {
        this.name = name;
        this.code = code;
    }

    public static String getStateCode(final String state) {
        for (States value : States.values()) {
            if (value.name().equalsIgnoreCase(state))
                return value.getCode();
        }
        return null;
    }

    public static String getStateName(final String state) {
        for (States value : States.values()) {
            if (value.name().equalsIgnoreCase(state))
                return value.getName();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
