/*
 * PixelAddons - a Spigot plugin that improves the Pixelmon Reforged experience.
 * Copyright (C) 2024 Foulest (https://github.com/Foulest)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package net.foulest.pixeladdons.cmds;

import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Command for voting to re-roll the hunt.
 *
 * @author Foulest
 */
@SuppressWarnings("MethodMayBeStatic")
public class RerollCmd {

    private static long lastReroll;
    public static final List<Player> votingToReroll = new ArrayList<>();

    @Command(name = "reroll", permission = "pixeladdons.reroll", aliases = "rtv",
            description = "Votes to re-roll the hunt.",
            usage = "/reroll", inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) {
        @Nullable Player player = args.getPlayer();

        // Silently return to avoid NPEs.
        if (player == null) {
            return;
        }

        // Checks if the command is enabled.
        if (!Settings.rerollCommandEnabled) {
            MessageUtil.messagePlayer(player, Settings.commandDisabledMessage
                    .replace("%command%", "/reroll"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, Settings.commandUsageMessage
                    .replace("%usage%", "/reroll"));
            return;
        }

        // Checks if the re-roll command is on cooldown.
        if (Settings.rerollCommandCooldown > 0) {
            long now = System.currentTimeMillis();
            long cooldownTimeRemainingMillis = (lastReroll + (Settings.rerollCommandCooldown * 1000)) - now; // Convert cooldown to milliseconds and calculate remaining time
            long cooldownTimeRemaining = cooldownTimeRemainingMillis / 1000; // Convert milliseconds back to seconds
            @NotNull String cooldownFormatted = MessageUtil.formatTime(cooldownTimeRemaining);

            if (cooldownTimeRemaining > 0) {
                MessageUtil.messagePlayer(player, Settings.rerollCommandCooldownMessage
                        .replace("%time%", cooldownFormatted));
                return;
            }
        }

        String playerName = player.getName();

        // Counts the player's vote and broadcasts it.
        // If the player has already voted, it removes their vote.
        if (votingToReroll.contains(player)) {
            votingToReroll.remove(player);
            int votingSize = votingToReroll.size();
            int onlineSize = Bukkit.getOnlinePlayers().size();

            // Broadcasts the cancellation of the vote.
            MessageUtil.broadcast(Settings.rerollVoteCancelledMessage
                    .replace("%player%", playerName)
                    .replace("%votes%", String.valueOf(votingSize))
                    .replace("%online%", String.valueOf(onlineSize)));
        } else {
            votingToReroll.add(player);
            int votingSize = votingToReroll.size();
            int onlineSize = Bukkit.getOnlinePlayers().size();

            // Only broadcasts the vote if there are other players online.
            // Otherwise, it would be pointless to broadcast it.
            if (onlineSize > 1) {
                MessageUtil.broadcast(Settings.rerollVoteSubmittedMessage
                        .replace("%player%", playerName)
                        .replace("%votes%", String.valueOf(votingSize))
                        .replace("%online%", String.valueOf(onlineSize)));
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
                Player player = votingToReroll.get(0);
                String playerName = player.getName();
                MessageUtil.broadcast(Settings.rerollHuntMessageWithPlayer.replace("%player%", playerName));
            } else {
                MessageUtil.broadcast(Settings.rerollHuntMessage);
            }

            // Resets the cooldown and clears the list.
            lastReroll = System.currentTimeMillis();
            votingToReroll.clear();
        }
    }
}
