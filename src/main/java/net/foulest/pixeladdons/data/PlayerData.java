package net.foulest.pixeladdons.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public final class PlayerData {

    // Player data
    private UUID uniqueId;
    private Player player;

    // Hatch data
    private boolean confirmHatch = false;

    public PlayerData(UUID uniqueId, Player player) {
        this.uniqueId = uniqueId;
        this.player = player;
    }
}
