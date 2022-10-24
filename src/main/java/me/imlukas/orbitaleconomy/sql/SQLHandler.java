package me.imlukas.orbitaleconomy.sql;

import me.imlukas.orbitaleconomy.OrbitalEconomy;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static me.imlukas.orbitaleconomy.sql.SQLQueries.*;

public class SQLHandler {

    private final SQLSetup sqlSetup;
    private PreparedStatement query;
    private final Connection getConnection;
    private final FileConfiguration config;

    public SQLHandler(OrbitalEconomy main) {
        this.config = main.getConfig();
        sqlSetup = new SQLSetup(
                config.getString("mysql.host"),
                config.getString("mysql.database"),
                config.getString("mysql.username"),
                config.getString("mysql.password"),
                config.getInt("mysql.port"));
        sqlSetup.createTables();
        getConnection = sqlSetup.get();
    }

    public void setMoney(UUID playerUUID, double money) {
        CompletableFuture.runAsync(() -> {
            try {
                query = getConnection.prepareStatement(SET_PLAYER_MONEY);
                query.setDouble(1, money);
                query.setString(2, playerUUID.toString());
                query.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addPlayer(UUID playerUUID, double money) {

        CompletableFuture.runAsync(() -> {
            try {
                query = getConnection.prepareStatement(INSERT_PLAYER_MONEY);
                query.setString(1, playerUUID.toString());
                query.setDouble(2, money);
                query.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean playerExists(UUID playerUUID) {
        try {
            query = getConnection.prepareStatement(GET_PLAYER);
            query.setString(1, playerUUID.toString());
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return rs.getDouble("money") >= 0;
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Double getMoney(UUID playerUUID) {
        return sqlSetup.getMoney(playerUUID);
    }
}
