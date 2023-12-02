package net.foulest.pixeladdons.cmds;

import com.envyful.pixel.hunt.remastered.api.PixelHuntFactory;
import net.foulest.pixeladdons.data.PlayerData;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.command.Command;
import net.foulest.pixeladdons.util.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Foulest
 * @project PixelAddons
 */
@SuppressWarnings("MethodMayBeStatic")
public class RerollCmd {

    public static List<Player> votingToReroll = new ArrayList<>();

    @Command(name = "reroll", description = "Votes to re-roll the hunt.",
            usage = "/reroll", aliases = {"rtv"}, inGameOnly = true)
    public void onCommand(CommandArgs args) {
        Player player = args.getPlayer();
        PlayerData playerData = PlayerData.getInstance(player);

        if (playerData == null) {
            return;
        }

        if (args.length() != 0) {
            MessageUtil.messagePlayer(player, "&cUsage: /reroll");
            return;
        }

        if (votingToReroll.contains(player)) {
            votingToReroll.remove(player);
            MessageUtil.broadcastMessage("&e[Hunt] &f" + player.getName() + " &7has cancelled their re-roll vote."
                    + " &e(" + votingToReroll.size() + "/" + Bukkit.getOnlinePlayers().size() + ")");
        } else {
            votingToReroll.add(player);
            MessageUtil.broadcastMessage("&e[Hunt] &f" + player.getName() + " &7has voted to re-roll the hunt."
                    + " &e(" + votingToReroll.size() + "/" + Bukkit.getOnlinePlayers().size() + ")");
        }

        handleReroll();
    }

    public static void handleReroll() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            votingToReroll.clear();
        } else if (Bukkit.getOnlinePlayers().size() == votingToReroll.size()) {
            PixelHuntFactory.reloadHunts();
            MessageUtil.broadcastMessage("&e[Hunt] &7The hunt has been &fre-rolled&7!");
            votingToReroll.clear();
        }
    }
}
