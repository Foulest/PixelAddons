package net.foulest.pixeladdons.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.foulest.pixeladdons.PixelAddons;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
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
@SuppressWarnings("unused")
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

    /**
     * Prints a hover message with the Pokemon's stats.
     *
     * @param player      The player to send the message to.
     * @param pokemon     The Pokemon to get the stats from.
     * @param chatMessage The message to send.
     */
    public static void printStatsHoverMessage(@NonNull Player player,
                                              @NonNull Pokemon pokemon,
                                              @NonNull String chatMessage) {
        List<String> statsList = StatsUtil.getStats(player, pokemon);

        for (Player online : Bukkit.getOnlinePlayers()) {
            TextComponent message = new TextComponent(MessageUtil.colorize(chatMessage));
            TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
            TextComponent hoverMessage = new TextComponent(new ComponentBuilder("").create());

            for (String line : statsList) {
                hoverMessage.addExtra(new TextComponent(MessageUtil.colorize(line)));

                if (!statsList.get(statsList.size() - 1).equals(line)) {
                    hoverMessage.addExtra(newLine);
                }
            }

            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{hoverMessage}));
            online.spigot().sendMessage(message);
        }
    }
}
