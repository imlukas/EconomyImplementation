package me.imlukas.orbitaleconomy;

import lombok.Getter;
import me.imlukas.orbitaleconomy.commands.EconomyCommand;
import me.imlukas.orbitaleconomy.commands.OrbitalCommand;
import me.imlukas.orbitaleconomy.handler.EconomyHandler;
import me.imlukas.orbitaleconomy.integration.VaultEconomyProvider;
import me.imlukas.orbitaleconomy.listeners.PlayerJoinListener;
import me.imlukas.orbitaleconomy.listeners.PlayerLeaveListener;
import me.imlukas.orbitaleconomy.sql.SQLHandler;
import me.imlukas.orbitaleconomy.util.storage.MessagesFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Getter
public final class OrbitalEconomy extends JavaPlugin {

    private Economy economy;
    private SQLHandler sqlHandler;
    private EconomyHandler economyHandler;
    private MessagesFile messages;
    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new MessagesFile(this);
        sqlHandler = new SQLHandler(this);
        economyHandler = new EconomyHandler(this);
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getServer().getServicesManager().register(Economy.class, new VaultEconomyProvider(this), this, ServicePriority.Highest);
            economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            System.out.println("[OrbitalEconomy] Vault integration enabled");
        }

        getCommand("orbitaleco").setExecutor(new OrbitalCommand(this));
        getCommand("money").setExecutor(new EconomyCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
    }

    @Override
    public void onDisable() {
        economyHandler.saveAllPlayers();
    }

    public Economy getEconomy() {
        return economy;
    }
}
