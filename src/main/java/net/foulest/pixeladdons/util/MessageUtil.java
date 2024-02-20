package net.foulest.pixeladdons.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for sending messages.
 *
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("unused")
public final class MessageUtil {

    private static final Logger logger = Bukkit.getLogger();

    /**
     * Logs a message to the console.
     *
     * @param level   The level to log the message at.
     * @param message The message to log.
     */
    public static void log(Level level, String message) {
        logger.log(level, "[PixelAddons] " + message);
    }

    /**
     * Prints a detailed message of an exception as a warning to the console, including its type,
     * message, cause, and the location where it occurred without printing the full stack trace.
     *
     * @param ex The exception to print.
     */
    public static void printException(@NotNull Throwable ex) {
        // Basic exception details
        StringBuilder message = new StringBuilder("An error occurred: ");
        message.append(ex.getClass().getName()); // Type of the exception
        message.append(": ").append(ex.getLocalizedMessage()); // Message of the exception

        // Cause of the exception
        Throwable cause = ex.getCause();
        if (cause != null) {
            message.append(" | Caused by: ").append(cause.getClass().getName());
            message.append(": ").append(cause.getLocalizedMessage());
        }

        // Location where the exception was thrown
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        if (stackTraceElements.length > 0) {
            StackTraceElement element = stackTraceElements[0]; // Getting the first element of the stack trace
            message.append(" | At: ").append(element.getClassName()); // Class name
            message.append(".").append(element.getMethodName()); // Method name
            message.append("(").append(element.getFileName()); // File name
            message.append(":").append(element.getLineNumber()).append(")"); // Line number
        }

        // Logging the detailed exception message
        logger.log(Level.WARNING, message.toString());
    }

    /**
     * Sends a message to the specified player.
     *
     * @param sender  The player to send the message to.
     * @param message The message to send.
     */
    public static void messagePlayer(CommandSender sender, String @NotNull ... message) {
        for (String line : message) {
            sender.sendMessage(colorize(line));
        }
    }

    /**
     * Broadcasts a message to all online players.
     *
     * @param message The message to send.
     */
    public static void broadcast(String @NotNull ... message) {
        for (String line : message) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                messagePlayer(player, line);
            }

            messagePlayer(Bukkit.getConsoleSender(), line);
        }
    }

    /**
     * Broadcasts a message to all online players.
     *
     * @param message The message to send.
     */
    public static void broadcast(@NotNull List<String> message) {
        for (String line : message) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                messagePlayer(player, line);
            }

            messagePlayer(Bukkit.getConsoleSender(), line);
        }
    }

    /**
     * Sends an alert to all online players with a specified permission.
     *
     * @param message    The message to send.
     * @param permission The permission to check.
     */
    public static void broadcastWithPerm(String permission, String @NotNull ... message) {
        for (String line : message) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission(permission)) {
                    messagePlayer(online, line);
                }
            }

            messagePlayer(Bukkit.getConsoleSender(), line);
        }
    }

    /**
     * Colorizes the specified message.
     *
     * @param message The message to colorize.
     */
    @Contract("_ -> new")
    public static @NotNull String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Strips the color from the specified message.
     *
     * @param message The message to strip the color from.
     */
    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }

    // Plugin specific methods

    /**
     * Formats seconds into Xh Xm Xs.
     * Does not include hours or minutes if they are 0.
     * Example: 1h 2m 3s is kept, but 0h 2m 3s is formatted to 2m 3s.
     *
     * @param seconds Seconds to format.
     * @return Formatted time.
     */
    public static @NotNull String formatTime(long seconds) {
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
    public static void printStatsHoverMessage(Player player,
                                              Pokemon pokemon,
                                              String chatMessage) {
        List<String> statsList = StatsUtil.getStatsPanel(player, pokemon);

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

    /**
     * Capitalizes the first letter of each word in a string.
     * Modified from <a href="https://github.com/apache/commons-lang">Apache Commons Lang</a>.
     *
     * @param str The string to capitalize.
     * @return The capitalized string.
     */
    public static @NotNull String capitalize(String str) {
        return capitalize(str, ' '); // Default delimiter is space if null is passed
    }

    /**
     * Capitalizes the first letter of each word in a string.
     * Modified from <a href="https://github.com/apache/commons-lang">Apache Commons Lang</a>.
     *
     * @param str        The string to capitalize.
     * @param delimiters The delimiters to use.
     * @return The capitalized string.
     */
    public static @NotNull String capitalize(@NotNull String str, char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }

        // Use a more efficient delimiter check if no custom delimiters are provided
        Set<Integer> delimiterSet = (delimiters != null && delimiters.length > 0)
                ? generateDelimiterSet(delimiters)
                : Collections.singleton((int) ' ');

        int strLen = str.length();
        StringBuilder sb = new StringBuilder(strLen);
        boolean capitalizeNext = true;

        for (int index = 0; index < strLen; ) {
            int codePoint = str.codePointAt(index);
            int charCount = Character.charCount(codePoint);

            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = true;
                sb.appendCodePoint(codePoint);
            } else if (capitalizeNext) {
                sb.appendCodePoint(Character.toTitleCase(codePoint));
                capitalizeNext = false;
            } else {
                sb.appendCodePoint(codePoint);
            }

            index += charCount;
        }
        return sb.toString();
    }

    /**
     * Generates a set of delimiters.
     * Modified from <a href="https://github.com/apache/commons-lang">Apache Commons Lang</a>.
     *
     * @param delimiters The delimiters to use.
     * @return The set of delimiters.
     */
    private static @NotNull Set<Integer> generateDelimiterSet(char... delimiters) {
        return delimiters == null ? Collections.singleton((int) ' ') :
                IntStream.range(0, delimiters.length)
                        .map(i -> delimiters[i])
                        .boxed()
                        .collect(Collectors.toSet());
    }
}
