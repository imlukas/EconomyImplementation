package me.imlukas.orbitaleconomy.listeners;

import me.imlukas.orbitaleconomy.OrbitalEconomy;
import me.imlukas.orbitaleconomy.handler.EconomyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final EconomyHandler economyHandler;

    public PlayerJoinListener(OrbitalEconomy main) {

        this.economyHandler = main.getEconomyHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        economyHandler.addPlayer(player.getUniqueId());
    }
}
