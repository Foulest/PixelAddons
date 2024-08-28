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

import lombok.Getter;
import lombok.Setter;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Main command for PixelAddons.
 *
 * @author Foulest
 */
@Getter
@Setter
public class PixelAddonsCmd {

    @SuppressWarnings("MethodMayBeStatic")
    @Command(name = "pixeladdons", description = "Main command for PixelAddons.",
            permission = "pixeladdons.main", usage = "/pixeladdons")
    public void onCommand(@NotNull CommandArgs args) {
        CommandSender sender = args.getSender();

        // No additional arguments, display help menu.
        if (args.length() == 0) {
            handleHelp(sender, args);
            return;
        }

        // Handle sub-commands.
        String subCommand = args.getArgs(0);

        //noinspection SwitchStatementWithTooFewBranches
        switch (subCommand.toLowerCase(Locale.ROOT)) {
            case "reload":
                if (!sender.hasPermission("pixeladdons.reload")) {
                    MessageUtil.messagePlayer(sender, Settings.commandNoPermissionMessage);
                    return;
                }

                if (args.length() != 1) {
                    MessageUtil.messagePlayer(sender, Settings.commandUsageMessage
                            .replace("%usage%", "/pixeladdons reload"));
                    return;
                }

                Settings.loadSettings();
                MessageUtil.messagePlayer(sender, "&aReloaded the config files successfully.");
                break;

            default:
                handleHelp(sender, args);
                break;
        }
    }

    /**
     * Handles the help command.
     *
     * @param sender The command sender
     * @param args   The command arguments
     */
    private static void handleHelp(@NotNull CommandSender sender, CommandArgs args) {
        if (!sender.hasPermission("pixeladdons.main")) {
            MessageUtil.messagePlayer(sender, Settings.commandNoPermissionMessage);
            return;
        }

        // A list of available commands with their usages.
        List<String> commands = Collections.singletonList(
                "&f/pixeladdons reload &7- Reloads the config."
        );

        int itemsPerPage = 4;
        int maxPages = (int) Math.ceil((double) commands.size() / itemsPerPage);
        int page = 1;

        if (args.length() > 1) {
            try {
                page = Integer.parseInt(args.getArgs(1));
            } catch (NumberFormatException ignored) {
            }
        }

        if (page > maxPages || page < 1) {
            MessageUtil.messagePlayer(sender, "&cInvalid page number. Choose between 1 and " + maxPages + ".");
            return;
        }

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(commands.size(), startIndex + itemsPerPage);

        MessageUtil.messagePlayer(sender, "");
        MessageUtil.messagePlayer(sender, "&ePixelAddons Help &7(Page " + page + "/" + maxPages + ")");

        for (int i = startIndex; i < endIndex; i++) {
            MessageUtil.messagePlayer(sender, commands.get(i));
        }

        MessageUtil.messagePlayer(sender, "");
        MessageUtil.messagePlayer(sender, "&7Type &f/pixeladdons help <page> &7for more commands.");
        MessageUtil.messagePlayer(sender, "");
    }
}
