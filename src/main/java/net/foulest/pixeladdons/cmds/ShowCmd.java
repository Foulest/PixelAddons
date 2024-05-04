package net.foulest.pixeladdons.cmds;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.util.FormatUtil;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.foulest.pixeladdons.util.Settings.*;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class ShowCmd {

    @Command(name = "show", description = "Shows the stats of your selected Pokemon in chat.",
            permission = "pixeladdons.show", usage = "/show <slot>",
            aliases = {"pkshow", "pokeshow"}, inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) {
        Player player = args.getPlayer();

        // Checks if the command is enabled.
        if (!showCommandEnabled) {
            MessageUtil.messagePlayer(player, commandDisabledMessage
                    .replace("%command%", "/show"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 1) {
            MessageUtil.messagePlayer(player, commandUsageMessage
                    .replace("%usage%", "/show <slot>"));
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
                    .replace("%reason%", "Not a number"));
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
                    .replace("%reason%", "Pokemon is missing"));
            return;
        }

        Player owner = Bukkit.getPlayer(pokemon.getOwnerPlayerUUID());

        // Checks if the owner is valid.
        if (owner == null) {
            MessageUtil.messagePlayer(player, commandInvalidUsageMessage
                    .replace("%reason%", "Owner is missing"));
            return;
        }

        String pokemonName = pokemon.getSpecies().getPokemonName();

        // Handles printing the stats.
        String chatMessage = showMessage
                .replace("%player%", owner.getName())
                .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                .replace("%pokemon%", pokemonName);

        // Prints the hover message.
        MessageUtil.printStatsHoverMessage(owner, pokemon, chatMessage);
    }
}
