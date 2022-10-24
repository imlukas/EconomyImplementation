package me.imlukas.orbitaleconomy.sql;

import lombok.AccessLevel;
import lombok.Getter;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static me.imlukas.orbitaleconomy.sql.SQLQueries.CREATE_MONEY_TABLE;
import static me.imlukas.orbitaleconomy.sql.SQLQueries.GET_MONEY;

public class SQLSetup {

    private static final String[] TABLES = {
            CREATE_MONEY_TABLE
    };
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    private final Logger log = Logger.getLogger("SQL");
    @Getter(AccessLevel.PACKAGE)
    private Connection connection;
    private PreparedStatement query;

    public SQLSetup(String host, String database, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
        openConnection();
    }

    public boolean openConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);


            connection.setAutoCommit(true);
            System.out.println("[OrbitalEconomy] Connected to MySQL server.");
        } catch (Exception e) {
            log.info(e.toString());
            System.out.println("[OrbitalEconomy] Failed to connect to MySQL server.");
            return false;
        }
        return true;
    }

    public Connection get() {
        try {
            if (connection != null || !connection.isValid(1)) {
                openConnection();
            }
        } catch (SQLException e) {
            openConnection();
        }
        return connection;
    }

    public void createTables() {
        CompletableFuture.runAsync(() -> {
            try {
                for (String query : TABLES) {
                    connection.createStatement().execute(query);
                }
            } catch (Exception e) {
                log.info(e.toString());
            }
        });
        System.out.println("[OrbitalEconomy] Tables created.");
    }

    public Double getMoney(UUID playerUUID) {
        try {
            query = connection.prepareStatement(GET_MONEY);
            query.setString(1, playerUUID.toString());

            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("money");
            }
            resultSet.close();
        } catch (Exception e) {
            log.info(e.toString());

        }
        return 0.0;
    }
}
