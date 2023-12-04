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

import java.text.DecimalFormat;
import java.util.Optional;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class HatchCmd {

    @Command(name = "hatch", description = "Hatches the selected Pokemon egg.",
            usage = "/hatch <slot>", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        PlayerData playerData = PlayerDataManager.getPlayerData(player);
        DecimalFormat df = new DecimalFormat("###,###.###");

        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, "&cUsage: /hatch <slot>");
            return;
        }

        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        if (!party.starterPicked) {
            MessageUtil.messagePlayer(player, "&cStarter Pokemon not found.");
            return;
        }

        try {
            Integer.parseInt(args.getArgs(0));
        } catch (Exception ex) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Not a number)");
            return;
        }

        int slot = Integer.parseInt(args.getArgs(0));

        if (slot <= 0 || slot > 6) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Number is invalid)");
            return;
        }

        slot -= 1;

        if (party.get(slot) == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Egg is missing)");
            return;
        }

        Pokemon pokemon = party.get(slot);

        if (pokemon == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Egg is missing)");
            return;
        }

        if (!pokemon.isEgg()) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Egg is missing)");
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        if (owner == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Owner is missing)");
            return;
        }

        Optional<? extends IPixelmonBankAccount> bankAccount = Pixelmon.moneyManager.getBankAccount(player.getUniqueId());

        if (!bankAccount.isPresent()) {
            MessageUtil.messagePlayer(player, "&cBank account not found.");
            return;
        }

        int hatchCost = 5000;

        if (bankAccount.get().getMoney() < hatchCost) {
            MessageUtil.messagePlayer(player, "&cYou need at least $" + df.format(hatchCost) + " to hatch this egg.");
            return;
        }

        if (playerData.isConfirmHatch()) {
            bankAccount.get().setMoney(bankAccount.get().getMoney() - hatchCost);
            pokemon.hatchEgg();

            MessageUtil.messagePlayer(player, "&aYour " + pokemon.getSpecies().getPokemonName()
                    + " was successfully hatched for $" + df.format(hatchCost) + ".");

            playerData.setConfirmHatch(false);

        } else {
            playerData.setConfirmHatch(true);
            MessageUtil.messagePlayer(player, "&eHatch this egg for &a$5,000&e? Run the command again to confirm.");

            Bukkit.getScheduler().runTaskLater(PixelAddons.instance, () -> {
                if (playerData.isConfirmHatch()) {
                    playerData.setConfirmHatch(false);
                    MessageUtil.messagePlayer(player, "&cHatch command cancelled due to inactivity.");
                }
            }, 400L);
        }
    }
}
