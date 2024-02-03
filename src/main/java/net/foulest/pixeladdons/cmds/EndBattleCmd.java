package net.foulest.pixeladdons.cmds;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.ForceEndBattleEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.EndSpectate;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.ExitBattle;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleForceEndCause;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.pixelmonmod.pixelmon.api.command.PixelmonCommand.requireEntityPlayer;
import static net.foulest.pixeladdons.util.Settings.*;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class EndBattleCmd {

    @Command(name = "endbattle", description = "Ends your current battle.",
            usage = "/endbattle", aliases = {"stopbattle", "exitbattle"}, inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) throws CommandException {
        Player player = args.getPlayer();

        // Checks if the command is enabled.
        if (!endBattleCommandEnabled) {
            MessageUtil.messagePlayer(player, commandDisabledMessage
                    .replace("%command%", "/endbattle"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, commandUsageMessage
                    .replace("%usage%", "/endbattle"));
            return;
        }

        EntityPlayerMP playerMP = requireEntityPlayer(player.getName());
        BattleControllerBase battleController = BattleRegistry.getBattle(playerMP);

        // Checks if the player is in a battle.
        if (battleController != null) {
            ForceEndBattleEvent event = new ForceEndBattleEvent(battleController, EnumBattleForceEndCause.ENDBATTLE);

            // Removes the player from the battle.
            if (Pixelmon.EVENT_BUS.post(event)) {
                MessageUtil.messagePlayer(player, failedToEndBattleMessage);
            } else if (battleController.removeSpectator(playerMP)) {
                Pixelmon.network.sendTo(new EndSpectate(), playerMP);
            } else {
                battleController.endBattle(EnumBattleEndCause.FORCE);
                MessageUtil.messagePlayer(player, battleEndedSuccessfullyMessage);
                BattleRegistry.deRegisterBattle(battleController);
            }
        } else {
            // Notifies the player that they are not in a battle.
            MessageUtil.messagePlayer(player, notInBattleMessage);
            Pixelmon.network.sendTo(new ExitBattle(), playerMP);
        }
    }
}
