package net.foulest.pixeladdons.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.foulest.pixeladdons.PixelAddons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

/**
 * Utility class for sending messages.
 *
 * @author Foulest
 * @project PixelAddons
 */
@Getter
@Setter
public final class MessageUtil {

    public static void messagePlayer(@NonNull CommandSender sender, @NonNull String message) {
        sender.sendMessage(colorize(message));
    }

    public static void log(@NonNull Level level, @NonNull String message) {
        Bukkit.getLogger().log(level, "[" + PixelAddons.instance.getPluginName() + "] " + message);
    }

    public static void broadcast(@NonNull String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            messagePlayer(player, message);
        }

        messagePlayer(Bukkit.getConsoleSender(), message);
    }

    public static void broadcastList(@NonNull List<String> message) {
        for (String line : message) {
            broadcast(line);
        }
    }

    public static void broadcastWithPerm(@NonNull String message, @NonNull String permission) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission(permission)) {
                messagePlayer(online, message);
            }
        }

        messagePlayer(Bukkit.getConsoleSender(), message);
    }

    public static String colorize(@NonNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColor(@NonNull String message) {
        return ChatColor.stripColor(message);
    }

    /**
     * Formats seconds into Xh Xm Xs.
     * Does not include hours or minutes if they are 0.
     * Example: 1h 2m 3s is kept, but 0h 2m 3s is formatted to 2m 3s.
     *
     * @param seconds Seconds to format.
     * @return Formatted time.
     */
    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder timeBuilder = new StringBuilder();

        if (hours > 0) {
            timeBuilder.append(hours).append("h ");
        }

        if (minutes > 0 || hours > 0) {
            timeBuilder.append(minutes).append("m ");
        }

        timeBuilder.append(secs).append("s");
        return timeBuilder.toString();
    }
}
