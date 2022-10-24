package me.imlukas.orbitaleconomy.handler;

import me.imlukas.orbitaleconomy.OrbitalEconomy;
import me.imlukas.orbitaleconomy.sql.SQLHandler;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyHandler {

    private final SQLHandler sqlHandler;
    private static final Map<UUID, Double> playerMoney = new ConcurrentHashMap<>();

    public EconomyHandler(OrbitalEconomy main) {
        this.sqlHandler = main.getSqlHandler();
    }

    // SQL Integration Methods
    public void addPlayer(UUID playerUUID) {
        if (sqlHandler.playerExists(playerUUID)) {
            playerMoney.put(playerUUID, sqlHandler.getMoney(playerUUID));
            return;
        }
        playerMoney.put(playerUUID, 0.0);
    }

    public void setBalance(UUID playerUUID, double amount) {
        playerMoney.put(playerUUID, amount);
    }

    public void savePlayer(UUID playerUUID) {
        if (sqlHandler.playerExists(playerUUID)) {
            sqlHandler.setMoney(playerUUID, playerMoney.get(playerUUID));
            playerMoney.remove(playerUUID);
            return;
        }
        sqlHandler.addPlayer(playerUUID, playerMoney.get(playerUUID));
        playerMoney.remove(playerUUID);
    }

    public void saveAllPlayers() {
        for (UUID playerUUID : playerMoney.keySet()) {
            savePlayer(playerUUID);
        }
        System.out.println("[OrbitalEconomy] Saved all players");
    }


    // Vault Integration
    public static boolean hasEnough(String playerName, double amount) {

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return false;
        }
        return playerMoney.get(player.getUniqueId()) >= amount;
    }

    public static boolean hasEnough(OfflinePlayer player, double amount) {

        if (player == null) {
            return false;
        }
        if (!player.hasPlayedBefore()) {
            return false;
        }

        return playerMoney.get(player.getUniqueId()) >= amount;
    }

    public static EconomyResponse withdraw(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
        }

        playerMoney.put(player.getUniqueId(), playerMoney.get(player.getUniqueId()) - amount);

        return new EconomyResponse(-amount, playerMoney.get(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);

    }

    public static EconomyResponse withdraw(OfflinePlayer player, double amount) {
        if (!player.hasPlayedBefore()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
        }

        playerMoney.put(player.getUniqueId(), playerMoney.get(player.getUniqueId()) - amount);

        return new EconomyResponse(-amount, playerMoney.get(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);
    }

    public static EconomyResponse deposit(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
        }

        playerMoney.put(player.getUniqueId(), playerMoney.get(player.getUniqueId()) + amount);

        return new EconomyResponse(amount, playerMoney.get(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);
    }

    public static EconomyResponse deposit(OfflinePlayer player, double amount) {
        if (!player.hasPlayedBefore()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player not found!");
        }

        playerMoney.put(player.getUniqueId(), playerMoney.get(player.getUniqueId()) + amount);

        return new EconomyResponse(amount, playerMoney.get(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, null);
    }

    public static double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return 0;
        }

        return playerMoney.get(player.getUniqueId());
    }

    public static double getBalance(OfflinePlayer player) {
        return playerMoney.get(player.getUniqueId());
    }


}
