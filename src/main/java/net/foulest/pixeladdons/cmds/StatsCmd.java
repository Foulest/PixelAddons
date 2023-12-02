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

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class StatsCmd {

    @Command(name = "stats", description = "Shows the stats of your selected Pokemon.",
            aliases = {"ivs", "evs", "iv", "ev", "pkstats"}, usage = "/stats <slot> [player]", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0 || args.length() > 2) {
            MessageUtil.messagePlayer(player, "&cUsage: /stats <slot> [player]");
            return;
        }

        PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());

        if (args.length() == 2 && !args.getArgs(1).isEmpty()) {
            Player target = Bukkit.getPlayer(args.getArgs(1));
            party = Pixelmon.storageManager.getParty(target.getUniqueId());
        }

        if (!party.starterPicked) {
            MessageUtil.messagePlayer(player, "&cStarter Pokemon not found.");
            return;
        }

        try {
            Integer.parseInt(args.getArgs(0));
        } catch (Exception ex) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Number is invalid)");
            return;
        }

        int slot = Integer.parseInt(args.getArgs(0));

        if (slot <= 0 || slot > 6) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Number is invalid)");
            return;
        }

        slot -= 1;

        if (party.get(slot) == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Pokemon is missing)");
            return;
        }

        Pokemon pokemon = party.get(slot);

        if (pokemon == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Pokemon is missing)");
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        if (owner == null) {
            MessageUtil.messagePlayer(player, "&cInvalid slot. (Owner is missing)");
            return;
        }

        MessageUtil.messagePlayer(player, "");

        for (String line : StatsUtil.getStats(owner, pokemon)) {
            MessageUtil.messagePlayer(player, line);
        }

        MessageUtil.messagePlayer(player, "");
    }
}
