package me.imlukas.orbitaleconomy.sql;

public class SQLQueries {

    public static final String CREATE_MONEY_TABLE = "CREATE TABLE IF NOT EXISTS Economy (UUID VARCHAR(36) UNIQUE NOT NULL, money INT DEFAULT 0);";
    public static final String GET_PLAYER = "SELECT * FROM Economy WHERE UUID = ?";
    public static final String GET_MONEY = "SELECT money FROM Economy WHERE UUID = ?";
    public static final String SET_PLAYER_MONEY = "UPDATE Economy SET money = ? WHERE UUID = ?";
    public static final String INSERT_PLAYER_MONEY = "INSERT INTO Economy (UUID, money) VALUES (?, ?)";
}
