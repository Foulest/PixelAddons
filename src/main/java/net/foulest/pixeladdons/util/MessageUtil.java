package net.foulest.pixeladdons.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Foulest
 * @project PixelAddons
 */
public final class MessageUtil {

    private MessageUtil() {
    }

    public static void messagePlayer(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void log(String message) {
        messagePlayer(Bukkit.getConsoleSender(), message);
    }

    public static void broadcastMessage(String message) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            messagePlayer(online, message);
        }
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
