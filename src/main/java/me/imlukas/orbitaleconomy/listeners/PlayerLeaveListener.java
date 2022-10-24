package me.imlukas.orbitaleconomy.listeners;

import me.imlukas.orbitaleconomy.OrbitalEconomy;
import me.imlukas.orbitaleconomy.handler.EconomyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final EconomyHandler economyHandler;

    public PlayerLeaveListener(OrbitalEconomy main) {
        this.economyHandler = main.getEconomyHandler();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        economyHandler.savePlayer(player.getUniqueId());
    }
}

