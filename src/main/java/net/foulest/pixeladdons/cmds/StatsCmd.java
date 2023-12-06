package net.foulest.pixeladdons.cmds;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.StatsUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.foulest.pixeladdons.util.Settings.*;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class StatsCmd {

    @Command(name = "stats", description = "Shows the stats of your selected Pokemon.",
            aliases = {"ivs", "evs", "iv", "ev", "pkstats", "pokestats"},
            usage = "/stats <slot> [player]", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        // Checks if the command is enabled.
        if (!statsCommandEnabled) {
            MessageUtil.messagePlayer(player, commandDisabledMessage
                    .replace("%command%", "/stats"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() == 0 || args.length() > 2) {
            MessageUtil.messagePlayer(player, commandUsageMessage
                    .replace("%usage%", "/stats <slot> [player]"));
            return;
        }

        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        // Handles viewing other players' stats.
        if (args.length() == 2 && !args.getArgs(1).isEmpty()) {
            // Checks if viewing other players' stats is disabled.
            if (!statsCommandViewOtherPlayers) {
                MessageUtil.messagePlayer(player, statsViewOtherPlayersMessage);
                return;
            }

            // Checks if the player is invalid.
            if (Bukkit.getPlayer(args.getArgs(1)) == null) {
                MessageUtil.messagePlayer(player, invalidSlotMessage
                        .replace("%reason%", "Player is invalid"));
                return;
            }

            Player target = Bukkit.getPlayer(args.getArgs(1));

            // Checks if the player is offline.
            if (!target.isOnline()) {
                MessageUtil.messagePlayer(player, invalidSlotMessage
                        .replace("%reason%", "Player is offline"));
                return;
            }

            party = Pixelmon.storageManager.getParty(target.getUniqueId());
        }

        // Checks if the player has a starter Pokemon.
        if (!party.starterPicked) {
            MessageUtil.messagePlayer(player, starterNotFoundMessage);
            return;
        }

        // Checks if the slot is a number.
        try {
            Integer.parseInt(args.getArgs(0));
        } catch (Exception ex) {
            MessageUtil.messagePlayer(player, invalidSlotMessage
                    .replace("%reason%", "Number is invalid"));
            return;
        }

        int slot = Integer.parseInt(args.getArgs(0));

        // Checks if the slot is valid.
        if (slot <= 0 || slot > 6) {
            MessageUtil.messagePlayer(player, invalidSlotMessage
                    .replace("%reason%", "Slot is invalid"));
            return;
        }

        slot -= 1;

        // Checks if the slot is empty.
        if (party.get(slot) == null) {
            MessageUtil.messagePlayer(player, invalidSlotMessage
                    .replace("%reason%", "Slot is empty"));
            return;
        }

        Pokemon pokemon = party.get(slot);

        // Checks if the Pokemon is missing.
        if (pokemon == null) {
            MessageUtil.messagePlayer(player, invalidSlotMessage
                    .replace("%reason%", "Pokemon is missing"));
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        // Checks if the owner is missing.
        if (owner == null) {
            MessageUtil.messagePlayer(player, invalidSlotMessage
                    .replace("%reason%", "Owner is missing"));
            return;
        }

        // Handles printing the stats.
        MessageUtil.messagePlayer(player, "");

        for (String line : StatsUtil.getStats(owner, pokemon)) {
            MessageUtil.messagePlayer(player, line);
        }

        MessageUtil.messagePlayer(player, "");
    }
}
