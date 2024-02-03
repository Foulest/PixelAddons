package net.foulest.pixeladdons.cmds;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.PixelAddons;
import net.foulest.pixeladdons.data.PlayerData;
import net.foulest.pixeladdons.data.PlayerDataManager;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Optional;

import static net.foulest.pixeladdons.util.Settings.*;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class HatchCmd {

    @Command(name = "hatch", description = "Hatches the selected Pokemon egg.",
            usage = "/hatch <slot>", inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) {
        Player player = args.getPlayer();
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        DecimalFormat df = new DecimalFormat("###,###.###");
        String formattedCost = df.format(hatchCommandCost);

        // Checks if the command is enabled.
        if (!hatchCommandEnabled) {
            MessageUtil.messagePlayer(player, commandDisabledMessage
                    .replace("%command%", "/hatch"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, commandUsageMessage
                    .replace("%usage%", "/hatch <slot>"));
            return;
        }

        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        // Checks if the player has a starter Pokemon.
        if (!party.starterPicked) {
            MessageUtil.messagePlayer(player, starterNotFoundMessage);
            return;
        }

        // Checks if the slot is a number.
        try {
            Integer.parseInt(args.getArgs(0));
        } catch (Exception ex) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Number is invalid"));
            return;
        }

        int slot = Integer.parseInt(args.getArgs(0));

        // Checks if the slot is valid.
        if (slot <= 0 || slot > 6) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Slot is invalid"));
            return;
        }

        slot -= 1;

        // Checks if the slot is empty.
        if (party.get(slot) == null) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Slot is empty"));
            return;
        }

        Pokemon pokemon = party.get(slot);

        // Checks if the Pokemon is valid.
        if (pokemon == null) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Egg is missing"));
            return;
        }

        // Checks if the Pokemon is an egg.
        if (!pokemon.isEgg()) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Not an egg"));
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        // Checks if the owner is valid.
        if (owner == null) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Owner is missing"));
            return;
        }

        Optional<? extends IPixelmonBankAccount> bankAccount
                = Pixelmon.moneyManager.getBankAccount(player.getUniqueId());

        // Checks if the player has a bank account.
        if (!bankAccount.isPresent()) {
            MessageUtil.messagePlayer(player, bankAccountNotFoundMessage);
            return;
        }

        // Checks if the player has enough money.
        if (bankAccount.get().getMoney() < hatchCommandCost) {
            MessageUtil.messagePlayer(player, notEnoughMoneyMessage
                    .replace("%amount%", formattedCost));
            return;
        }

        // Handles hatching the egg.
        if (playerData.isConfirmHatch()) {
            bankAccount.get().setMoney(bankAccount.get().getMoney() - hatchCommandCost);
            pokemon.hatchEgg();

            playerData.setConfirmHatch(false);
            MessageUtil.messagePlayer(player, pokemonHatchedMessage
                    .replace("%pokemon%", pokemon.getSpecies().getPokemonName())
                    .replace("%amount%", formattedCost));

        } else {
            playerData.setConfirmHatch(true);
            MessageUtil.messagePlayer(player, confirmHatchMessage
                    .replace("%amount%", formattedCost));

            Bukkit.getScheduler().runTaskLater(PixelAddons.instance, () -> {
                if (playerData.isConfirmHatch()) {
                    playerData.setConfirmHatch(false);
                    MessageUtil.messagePlayer(player, hatchCommandCancelledMessage);
                }
            }, 400L);
        }
    }
}
