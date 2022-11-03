package net.foulest.pixeladdons.data;

import lombok.Getter;
import lombok.Setter;
import net.foulest.pixeladdons.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public final class PlayerData {

    private static final Set<PlayerData> INSTANCES = new HashSet<>();
    private final Player player;
    private boolean confirmHatch = false;

    private PlayerData(Player player) {
        this.player = player;
        INSTANCES.add(this);
    }

    /**
     * Returns the player's PlayerData.
     */
    public static PlayerData getInstance(Player player) {
        if (INSTANCES.isEmpty()) {
            new PlayerData(player);
        }

        for (PlayerData playerData : INSTANCES) {
            if (playerData == null || playerData.getPlayer() == null
                    || playerData.getPlayer().getUniqueId() == null
                    || player == null || player.getUniqueId() == null) {
                MessageUtil.log("&cPlayer data for player '" + player + "' is null");
                return null;
            }

            if (playerData.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return playerData;
            }
        }

        return new PlayerData(player);
    }
}
