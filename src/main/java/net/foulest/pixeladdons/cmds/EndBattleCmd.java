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
import com.pixelmonmod.pixelmon.api.command.PixelmonCommand;
import com.pixelmonmod.pixelmon.api.events.battles.ForceEndBattleEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.EndSpectate;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.ExitBattle;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleForceEndCause;
import lombok.NoArgsConstructor;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Foulest
 * @project PixelAddons
 */
@NoArgsConstructor
@SuppressWarnings("MethodMayBeStatic")
public class EndBattleCmd {

    @Command(name = "endbattle", description = "Ends your current battle.",
            permission = "pixeladdons.endbattle", usage = "/endbattle",
            aliases = {"stopbattle", "exitbattle"}, inGameOnly = true)
    public void onCommand(@NotNull CommandArgs args) throws CommandException {
        Player player = args.getPlayer();

        // Silently return to avoid NPEs.
        if (player == null) {
            return;
        }

        // Checks if the command is enabled.
        if (!Settings.endBattleCommandEnabled) {
            MessageUtil.messagePlayer(player, Settings.commandDisabledMessage
                    .replace("%command%", "/endbattle"));
            return;
        }

        // Checks for correct command usage.
        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, Settings.commandUsageMessage
                    .replace("%usage%", "/endbattle"));
            return;
        }

        EntityPlayerMP playerMP = PixelmonCommand.requireEntityPlayer(player.getName());
        BattleControllerBase battleController = BattleRegistry.getBattle(playerMP);

        // Checks if the player is in a battle.
        if (battleController != null) {
            ForceEndBattleEvent event = new ForceEndBattleEvent(battleController, EnumBattleForceEndCause.ENDBATTLE);

            // Removes the player from the battle.
            if (Pixelmon.EVENT_BUS.post(event)) {
                MessageUtil.messagePlayer(player, Settings.failedToEndBattleMessage);
            } else if (battleController.removeSpectator(playerMP)) {
                Pixelmon.network.sendTo(new EndSpectate(), playerMP);
            } else {
                battleController.endBattle(EnumBattleEndCause.FORCE);
                MessageUtil.messagePlayer(player, Settings.battleEndedSuccessfullyMessage);
                BattleRegistry.deRegisterBattle(battleController);
            }
        } else {
            // Notifies the player that they are not in a battle.
            MessageUtil.messagePlayer(player, Settings.notInBattleMessage);
            Pixelmon.network.sendTo(new ExitBattle(), playerMP);
        }
    }
}
