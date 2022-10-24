package me.imlukas.orbitaleconomy.util.storage;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesFile extends YMLBase {

    @Getter
    private final String prefix, arrow;
    @Getter
    private boolean usePrefix, lessIntrusive, useActionBar;
    private String msg;

    public MessagesFile(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);

        prefix = StringEscapeUtils.unescapeJava(getConfiguration().getString("messages.prefix"));
        arrow = StringEscapeUtils.unescapeJava(getConfiguration().getString("messages.arrow"));
        usePrefix = getConfiguration().getBoolean("messages.use-prefix");
        useActionBar = getConfiguration().getBoolean("messages.actionbar.enabled");

    }

    public String setColor(String message) {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        int minorVer = Integer.parseInt(split[1]);

        if (minorVer >= 16) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                matcher = pattern.matcher(message);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String setMessage(String name) {
        return setMessage(name, (s) -> s);
    }

    private String setMessage(String name, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name))
            return "";

        if (usePrefix) {
            msg = prefix + " " + arrow + " " + getMessage(name);
        } else if (getMessage(name).contains("%prefix%")) {
            msg = msg.replace("%prefix%", prefix + " " + arrow + getMessage(name));
        } else {
            msg = getMessage(name);
        }
        msg = action.apply(msg);
        return setColor(msg);
    }

    public void sendStringMessage(CommandSender player, String msg) {
        player.sendMessage(setColor(msg));
    }

    public void sendMessage(CommandSender player, String name) {
        sendMessage(player, name, (s) -> s);
    }

    public void sendMessage(CommandSender player, String name, Function<String, String> action) {
        if (getConfiguration().isList("messages." + name)) {
            for (String str : getConfiguration().getStringList("messages." + name)) {
                msg = StringEscapeUtils.unescapeJava(str.replace("%prefix%", prefix));
                msg = action.apply(msg);
                player.sendMessage(setColor(msg));
            }
            return;
        }
        msg = setMessage(name, action);
        player.sendMessage(msg);
    }

    public void sendActionBarMessage(Player player, String key) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setMessage(key)));
    }

    public void sendActionBarMessage(Player player, String key, Function<String, String> action) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(setMessage(key, action)));
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }

    public boolean togglePrefix() {
        boolean isEnabled = usePrefix;
        if (isEnabled) {
            getConfiguration().set("messages.use-prefix", false);
        } else {
            getConfiguration().set("messages.use-prefix", true);
        }
        save();
        usePrefix = getConfiguration().getBoolean("messages.use-prefix");
        return !isEnabled;
    }

    public boolean toggleActionBar() {
        boolean isEnabled = useActionBar;
        if (isEnabled) {
            getConfiguration().set("messages.actionbar.enabled", false);
        } else {
            getConfiguration().set("messages.actionbar.enabled", true);
        }
        save();
        useActionBar = getConfiguration().getBoolean("messages.actionbar.enabled");
        return !isEnabled;
    }
}

