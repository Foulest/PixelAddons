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

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.util.FormatUtil;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Command for showing the stats of a selected Pokemon.
 *
 * @author Foulest
 */
@SuppressWarnings("MethodMayBeStatic")
public class ShowCmd {

    @Command(name = "show", description = "Shows the stats of your selected Pokemon in chat.",
            permission = "pixeladdons.show", usage = "/show <slot>",
            aliases = {"pkshow", "pokeshow"}, inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) {
        Player player = args.getPlayer();

        // Silently return to avoid NPEs.
        if (player == null) {
            return;
        }

        // Checks if the command is enabled.
        if (!Settings.showCommandEnabled) {
            MessageUtil.messagePlayer(player, Settings.commandDisabledMessage
                    .replace("%command%", "/show"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, Settings.commandUsageMessage
                    .replace("%usage%", "/show <slot>"));
            return;
        }

        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        // Checks if the player has a starter Pokemon.
        if (!party.starterPicked) {
            MessageUtil.messagePlayer(player, Settings.starterNotFoundMessage);
            return;
        }

        // Checks if the slot is a number.
        try {
            Integer.parseInt(args.getArgs(0));
        } catch (NumberFormatException ex) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Not a number"));
            return;
        }

        int slot = Integer.parseInt(args.getArgs(0));

        // Checks if the slot is valid.
        if (slot <= 0 || slot > 6) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Slot is invalid"));
            return;
        }

        slot -= 1;

        // Checks if the slot is empty.
        if (party.get(slot) == null) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Slot is empty"));
            return;
        }

        Pokemon pokemon = party.get(slot);

        // Checks if the Pokemon is valid.
        if (pokemon == null) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Pokemon is missing"));
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        // Checks if the owner is valid.
        if (owner == null) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Owner is missing"));
            return;
        }

        String pokemonName = pokemon.getSpecies().getPokemonName();

        // Handles printing the stats.
        String chatMessage = Settings.showMessage
                .replace("%player%", owner.getName())
                .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                .replace("%pokemon%", pokemonName);

        // Prints the hover message.
        MessageUtil.printStatsHoverMessage(owner, pokemon, chatMessage);
    }
}
