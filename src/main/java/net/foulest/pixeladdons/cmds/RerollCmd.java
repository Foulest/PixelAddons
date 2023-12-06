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

import static net.foulest.pixeladdons.util.Settings.*;

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

        // Checks if the command is enabled.
        if (!Settings.rerollCommandEnabled) {
            MessageUtil.messagePlayer(player, commandDisabledMessage
                    .replace("%command%", "/reroll"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, commandUsageMessage
                    .replace("%usage%", "/reroll"));
            return;
        }

        // Checks if the re-roll command is on cooldown.
        if (Settings.rerollCommandCooldown > 0) {
            long now = System.currentTimeMillis();
            long cooldownTimeRemainingMillis = (lastReroll + (Settings.rerollCommandCooldown * 1000)) - now; // Convert cooldown to milliseconds and calculate remaining time
            long cooldownTimeRemaining = cooldownTimeRemainingMillis / 1000; // Convert milliseconds back to seconds
            String cooldownFormatted = MessageUtil.formatTime(cooldownTimeRemaining);

            if (cooldownTimeRemaining > 0) {
                MessageUtil.messagePlayer(player, rerollCommandCooldownMessage
                        .replace("%time%", cooldownFormatted));
                return;
            }
        }

        // Counts the player's vote and broadcasts it.
        // If the player has already voted, it removes their vote.
        if (votingToReroll.contains(player)) {
            votingToReroll.remove(player);

            // Broadcasts the cancellation of the vote.
            MessageUtil.broadcast(rerollVoteCancelledMessage
                    .replace("%player%", player.getName())
                    .replace("%votes%", String.valueOf(votingToReroll.size()))
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())));
        } else {
            votingToReroll.add(player);

            // Only broadcasts the vote if there are other players online.
            // Otherwise, it would be pointless to broadcast it.
            if (Bukkit.getOnlinePlayers().size() > 1) {
                MessageUtil.broadcast(rerollVoteSubmittedMessage
                        .replace("%player%", player.getName())
                        .replace("%votes%", String.valueOf(votingToReroll.size()))
                        .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())));
            }
        }

        // Executes the re-roll if all players have voted.
        handleReroll();
    }

    /**
     * Handles the re-roll.
     * This is a separate method, so it can be called from other classes.
     */
    public static void handleReroll() {
        // Checks if the command is enabled.
        if (!Settings.rerollCommandEnabled) {
            return;
        }

        // Clears the list if there are no players online.
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            votingToReroll.clear();

        } else if (Bukkit.getOnlinePlayers().size() == votingToReroll.size()) {
            // Re-rolls the hunt if all players have voted.
            PixelHuntFactory.reloadHunts();

            // Broadcasts the re-roll.
            if (votingToReroll.size() == 1) {
                MessageUtil.broadcast(rerollHuntMessageWithPlayer
                        .replace("%player%", votingToReroll.get(0).getName()));
            } else {
                MessageUtil.broadcast(rerollHuntMessage);
            }

            // Resets the cooldown and clears the list.
            lastReroll = System.currentTimeMillis();
            votingToReroll.clear();
        }
    }
}
