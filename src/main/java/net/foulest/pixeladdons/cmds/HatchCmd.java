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
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import lombok.NoArgsConstructor;
import net.foulest.pixeladdons.PixelAddons;
import net.foulest.pixeladdons.data.PlayerData;
import net.foulest.pixeladdons.data.PlayerDataManager;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Optional;

/**
 * @author Foulest
 * @project PixelAddons
 */
@NoArgsConstructor
@SuppressWarnings("MethodMayBeStatic")
public class HatchCmd {

    @Command(name = "hatch", description = "Hatches the selected Pokemon egg.",
            permission = "pixeladdons.hatch", usage = "/hatch <slot>", inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) {
        Player player = args.getPlayer();

        // Silently return to avoid NPEs.
        if (player == null) {
            return;
        }

        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        DecimalFormat df = new DecimalFormat("###,###.###");
        String formattedCost = df.format(Settings.hatchCommandCost);

        // Checks if the command is enabled.
        if (!Settings.hatchCommandEnabled) {
            MessageUtil.messagePlayer(player, Settings.commandDisabledMessage
                    .replace("%command%", "/hatch"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, Settings.commandUsageMessage
                    .replace("%usage%", "/hatch <slot>"));
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
                    .replace("%reason%", "Number is invalid"));
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
                    .replace("%reason%", "Egg is missing"));
            return;
        }

        // Checks if the Pokemon is an egg.
        if (!pokemon.isEgg()) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Not an egg"));
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        // Checks if the owner is valid.
        if (owner == null) {
            MessageUtil.messagePlayer(player, Settings.commandInvalidUsageMessage
                    .replace("%reason%", "Owner is missing"));
            return;
        }

        Optional<? extends IPixelmonBankAccount> bankAccount
                = Pixelmon.moneyManager.getBankAccount(player.getUniqueId());

        // Checks if the player has a bank account.
        if (!bankAccount.isPresent()) {
            MessageUtil.messagePlayer(player, Settings.bankAccountNotFoundMessage);
            return;
        }

        // Checks if the player has enough money.
        if (bankAccount.get().getMoney() < Settings.hatchCommandCost) {
            MessageUtil.messagePlayer(player, Settings.notEnoughMoneyMessage
                    .replace("%amount%", formattedCost));
            return;
        }

        // Handles hatching the egg.
        if (playerData.isConfirmHatch()) {
            bankAccount.get().setMoney(bankAccount.get().getMoney() - Settings.hatchCommandCost);
            pokemon.hatchEgg();

            playerData.setConfirmHatch(false);
            MessageUtil.messagePlayer(player, Settings.pokemonHatchedMessage
                    .replace("%pokemon%", pokemon.getSpecies().getPokemonName())
                    .replace("%amount%", formattedCost));

        } else {
            playerData.setConfirmHatch(true);
            MessageUtil.messagePlayer(player, Settings.confirmHatchMessage
                    .replace("%amount%", formattedCost));

            Bukkit.getScheduler().runTaskLater(PixelAddons.instance, () -> {
                if (playerData.isConfirmHatch()) {
                    playerData.setConfirmHatch(false);
                    MessageUtil.messagePlayer(player, Settings.hatchCommandCancelledMessage);
                }
            }, 400L);
        }
    }
}
