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

import static com.pixelmonmod.pixelmon.api.command.PixelmonCommand.requireEntityPlayer;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class StopBattleCmd {

    @Command(name = "stopbattle", description = "Stops your current battle.",
            usage = "/stopbattle", inGameOnly = true)
    public void onCommand(CommandArgs args) throws CommandException {
        Player player = args.getPlayer();

        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /stopbattle");
            return;
        }

        EntityPlayerMP playerMP = requireEntityPlayer(player.getName());
        BattleControllerBase bc = BattleRegistry.getBattle(playerMP);

        if (bc != null) {
            ForceEndBattleEvent event = new ForceEndBattleEvent(bc, EnumBattleForceEndCause.ENDBATTLE);

            if (Pixelmon.EVENT_BUS.post(event)) {
                MessageUtil.messagePlayer(player, "&cFailed to end battle.");
            } else if (bc.removeSpectator(playerMP)) {
                Pixelmon.network.sendTo(new EndSpectate(), playerMP);
            } else {
                bc.endBattle(EnumBattleEndCause.FORCE);
                MessageUtil.messagePlayer(player, "&aBattle ended successfully.");
                BattleRegistry.deRegisterBattle(bc);
            }

        } else {
            MessageUtil.messagePlayer(player, "&cYou are not in a battle.");
            Pixelmon.network.sendTo(new ExitBattle(), playerMP);
        }
    }
}
