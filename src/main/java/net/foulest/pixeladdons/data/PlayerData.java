package net.foulest.pixeladdons.data;

import lombok.Getter;
import lombok.Setter;
import net.foulest.pixeladdons.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
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
