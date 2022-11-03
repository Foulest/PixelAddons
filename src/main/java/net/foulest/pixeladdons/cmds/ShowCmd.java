package net.foulest.pixeladdons.cmds;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.listeners.EventListener;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class ShowCmd {

    @Command(name = "show", description = "Shows the stats of your selected Pokemon in chat.",
            usage = "/show <slot>", inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, "&cUsage: /show <slot>");
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

        StringBuilder chatMessage = new StringBuilder("&r" + player.getDisplayName() + " is showing off their ");

        if (pokemon.isEgg()) {
            if (pokemon.isShiny()) {
                chatMessage.append("&6[Egg]");
            } else {
                chatMessage.append("&b[Egg]");
            }

        } else {
            if (pokemon.isShiny()) {
                chatMessage.append("&6[").append(pokemon.getSpecies().getPokemonName()).append("]");
            } else if (pokemon.isLegendary()) {
                chatMessage.append("&d[").append(pokemon.getSpecies().getPokemonName()).append("]");
            } else {
                chatMessage.append("&a[").append(pokemon.getSpecies().getPokemonName()).append("]");
            }
        }

        EventListener.printHoverMessage(owner, pokemon, String.valueOf(chatMessage));
    }
}
