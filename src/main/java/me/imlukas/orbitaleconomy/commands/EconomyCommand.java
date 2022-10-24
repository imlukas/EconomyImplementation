package me.imlukas.orbitaleconomy.commands;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.imlukas.orbitaleconomy.OrbitalEconomy;
import me.imlukas.orbitaleconomy.handler.EconomyHandler;
import me.imlukas.orbitaleconomy.util.storage.MessagesFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final Economy economy;
    private final EconomyHandler economyHandler;
    private final MessagesFile messages;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Cache<UUID, Boolean> earnCooldown = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    private final static List<String> SUB_COMMANDS = List.of("give", "take", "setbal");

    public EconomyCommand(OrbitalEconomy main) {
        this.economy = main.getEconomy();
        this.economyHandler = main.getEconomyHandler();
        this.messages = main.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendMessages("bal-self", player, player, 0);
            return true;
        }
        if (args[0].equalsIgnoreCase("earn")) {
            if (earnCooldown.getIfPresent(player.getUniqueId()) != null) {
                sendMessages("cooldown", player, player, 0);
                return true;
            }
            double amount = Math.floor(random.nextDouble(1, 6));
            amount = Math.round(amount * 100.0) / 100.0;
            economy.depositPlayer(player, amount);
            sendMessages("earn", player, player, amount);
            earnCooldown.put(player.getUniqueId(), true);
            return true;
        }
        if (args.length == 1) {
            sendMessages("bal-other", player, Bukkit.getPlayer(args[0]), 0);
            return true;
        }
        if (args.length < 3) {
            messages.sendMessage(player, "economy.invalid-args");
            return true;
        }
        String action = args[0];
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            messages.sendMessage(sender, "global.player-not-found");
            return true;
        }

        double amount = 0.0;

        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            messages.sendStringMessage(sender, "&cInvalid Amount. Please insert positive integer numbers.");
            return true;
        }


        if (target.getUniqueId() == player.getUniqueId()) {
            messages.sendMessage(player, "economy.cannot-self");
            return true;
        }

        if (action.equalsIgnoreCase("give")) {


            if (economy.has(player, amount)) {
                economy.withdrawPlayer(player, amount);
                economy.depositPlayer(target, amount);

                sendMessages("give", player, target, amount);
                sendMessages("received", target, player, amount);
            } else {
                sendMessages("not-enough-money", player, player, amount);
            }
            return true;
        }
        if (action.equalsIgnoreCase("setbal")) {
            economyHandler.setBalance(target.getUniqueId(), amount);

            sendMessages("set", player, target, amount);

            return true;
        }
        if (action.equalsIgnoreCase("take")) {
            if (!(economy.has(target, amount))) {
                sendMessages("not-enough-money", player, target, amount);
            }
            economy.withdrawPlayer(target, amount);
            sendMessages("take", player, target, amount);
            return true;
        }

        return true;
    }

    private void sendMessages(String key, Player player, Player target, double amount) {
        if (messages.isUseActionBar()) {
            messages.sendActionBarMessage(player, "economy." + key, (message) -> message
                    .replace("%balance%", "" + economy.getBalance(target))
                    .replace("%amount%", "" + amount)
                    .replace("%player%", target.getName()));
            return;
        }
        messages.sendMessage(player, "economy." + key, (message) -> message
                .replace("%balance%", "" + economy.getBalance(target))
                .replace("%amount%", "" + amount)
                .replace("%player%", target.getName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            final List<String> completions = new ArrayList<>();
            //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
            StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
            return completions;
        }
        if (args.length == 2) {
            final List<String> completions = new ArrayList<>();
            //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
            StringUtil.copyPartialMatches(args[1], Arrays.asList(Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)), completions);
            return completions;
        }

        return Collections.emptyList();
    }
}
