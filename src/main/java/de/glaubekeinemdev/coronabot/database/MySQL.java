package de.glaubekeinemdev.coronabot.database;

import de.glaubekeinemdev.coronabot.utils.LoggingUtil;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {

    // instance of the mysql connection
    private Connection connection;

    // instance of the database credential
    private final DataBaseCredential dataBaseCredential;

    // instance of the Executorservice
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // connects to the mysql database
    public MySQL(final DataBaseCredential dataBaseCredential) {
        this.dataBaseCredential = dataBaseCredential;

        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + dataBaseCredential.getHost() + ":"
                                + dataBaseCredential.getPort() + "/" +
                                dataBaseCredential.getDatabase() + "?autoReconnect=true", dataBaseCredential.getUsername(),
                        dataBaseCredential.getPassword());
                LoggingUtil.sendInfo("Verbindung zur Datenbank erfolgreich hergestellt");
            } catch (SQLException e) {
                Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, e);
                LoggingUtil.sendError("Verbindung zur Datenbank konnte nicht hergestellt werden.");
            }
        }
    }

    // disconnects from the database
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            LoggingUtil.sendInfo("MySQL Verbindung beendet");
        }
    }

    // returns a boolean which defines the connection
    public boolean isConnected() {
        return connection != null;
    }

    /**
     * Update query to mysql
     *
     * @param qry the query
     */
    public void update(final String qry) {
        executorService.execute(() -> {
            if (isConnected()) {
                try {
                    final Statement statement = connection.createStatement();
                    statement.executeUpdate(qry);
                    statement.close();
                } catch (SQLException e) {
                    Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    /**
     * gets a resultset from mysql
     *
     * @param qry the query
     */
    public void getResult(final String qry, final Consumer<ResultSet> consumer) {
        executorService.execute(() -> {
            ResultSet resultSet = null;

            try {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(qry);
            } catch (SQLException e) {
                consumer.accept(null);
                Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, e);
            }
            consumer.accept(resultSet);
        });
    }


    // Try to reconnect to mysql
    public void TryToReconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + dataBaseCredential.getHost() + ":"
                            + dataBaseCredential.getPort() + "/" +
                            dataBaseCredential.getDatabase() + "?autoReconnect=true", dataBaseCredential.getUsername(),
                    dataBaseCredential.getPassword());
        } catch (SQLException e) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
