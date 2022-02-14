package de.glaubekeinemdev.coronabot.database;

public class DataBaseCredential {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public DataBaseCredential(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public static DataBaseCredential getDefault() {
        return new DataBaseCredential("127.0.0.1", "Database", "root", "passwort", 3306);
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
