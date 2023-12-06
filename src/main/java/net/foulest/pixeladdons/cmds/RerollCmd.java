package net.foulest.pixeladdons.cmds;

import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class RerollCmd {

    public static long lastReroll;
    public static List<Player> votingToReroll = new ArrayList<>();

    @Command(name = "reroll", description = "Votes to re-roll the hunt.",
            usage = "/reroll", aliases = {"rtv"}, inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        if (!Settings.pixelHuntIntegration) {
            MessageUtil.messagePlayer(player, "&cPixelHunt integration is disabled.");
            return;
        }

        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /reroll");
            return;
        }

        if (Settings.pixelHuntCooldown > 0) {
            long now = System.currentTimeMillis();
            long cooldownTimeRemainingMillis = (lastReroll + (Settings.pixelHuntCooldown * 1000)) - now; // Convert cooldown to milliseconds and calculate remaining time
            long cooldownTimeRemaining = cooldownTimeRemainingMillis / 1000; // Convert milliseconds back to seconds

            System.out.println("lastReroll=" + lastReroll + " now=" + now + " cooldown=" + Settings.pixelHuntCooldown + "s");
            String cooldownFormatted = MessageUtil.formatTime(cooldownTimeRemaining);

            if (cooldownTimeRemaining > 0) {
                MessageUtil.messagePlayer(player, "&cYou must wait &e" + cooldownFormatted + " &cbetween re-rolls.");
                return;
            }
        }

        if (votingToReroll.contains(player)) {
            votingToReroll.remove(player);
            MessageUtil.broadcast("&e[Hunt] &f" + player.getName() + " &7has cancelled their re-roll vote."
                    + " &e(" + votingToReroll.size() + "/" + Bukkit.getOnlinePlayers().size() + ")");
        } else {
            votingToReroll.add(player);

            if (Bukkit.getOnlinePlayers().size() > 1) {
                MessageUtil.broadcast("&e[Hunt] &f" + player.getName() + " &7has voted to re-roll the hunt."
                        + " &e(" + votingToReroll.size() + "/" + Bukkit.getOnlinePlayers().size() + ")");
            }
        }

        handleReroll();
    }

    /**
     * Handles the re-roll.
     * This is a separate method, so it can be called from other classes.
     */
    public static void handleReroll() {
        if (!Settings.pixelHuntIntegration) {
            return;
        }

        // Clears the list if there are no players online.
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            votingToReroll.clear();

        } else if (Bukkit.getOnlinePlayers().size() == votingToReroll.size()) {
            // Re-rolls the hunt if all players have voted.
            PixelHuntFactory.reloadHunts();

            // Broadcasts the re-roll.
            MessageUtil.broadcast("&e[Hunt] &7The hunt has been &fre-rolled"
                    + (votingToReroll.size() == 1 ? " &7by &f" + votingToReroll.get(0).getName() : "") + "&7!");

            lastReroll = System.currentTimeMillis();
            votingToReroll.clear();
        }
    }
}
