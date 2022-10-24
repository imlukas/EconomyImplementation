package me.imlukas.orbitaleconomy.commands;

import me.imlukas.orbitaleconomy.OrbitalEconomy;
import me.imlukas.orbitaleconomy.util.storage.MessagesFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrbitalCommand implements CommandExecutor {

    private final MessagesFile messages;

    public OrbitalCommand(OrbitalEconomy main) {
        this.messages = main.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendStringMessage(sender, "You must be a player to use this command!");
            return true;
        }
        if (args.length == 0) {

            messages.sendStringMessage(sender, "&7COMMANDS");
            messages.sendStringMessage(sender, "&7/orbitaleco - main command");
            messages.sendStringMessage(sender, "&7/orbitaleco toggleprefix - toggle prefix");
            messages.sendStringMessage(sender, "&7/orbitaleco toggleactionbar - toggle actionbar");
            messages.sendStringMessage(sender, "&7ECONOMY COMMANDS");
            messages.sendStringMessage(sender, "&7/bal - check your balance");
            messages.sendStringMessage(sender, "&7give <player> <amount> - give money to player");
            messages.sendStringMessage(sender, "&7setbal <player> <amount> - set player balance");
            messages.sendStringMessage(sender, "&7take <player> <amount> - take money from player");
        }
        String action = args[0];

        if (action.equalsIgnoreCase("toggleprefix")) {
            if (messages.togglePrefix()) {
                messages.sendMessage(player, "global.feature-on", (message) -> message.replace("%feature%", "Prefix"));
            } else {
                messages.sendMessage(player, "global.feature-off", (message) -> message.replace("%feature%", "Prefix"));
            }
            return true;
        }
        if (action.equalsIgnoreCase("toggleactionbar")) {
            if (messages.toggleActionBar()) {
                messages.sendMessage(player, "global.feature-on", (message) -> message.replace("%feature%", "ActionBar"));
            } else {
                messages.sendMessage(player, "global.feature-off", (message) -> message.replace("%feature%", "ActionBar"));
            }
            return true;
        }

        return true;
    }
}
