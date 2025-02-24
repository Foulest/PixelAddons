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
package net.foulest.pixeladdons.data;

import lombok.Data;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class PlayerDataManager {

    // Map of player UUIDs to their stored data.
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Gets a player's data from the map.
     *
     * @param player The player to get.
     * @return The player's data.
     */
    public static PlayerData getPlayerData(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();

        if (playerDataMap.containsKey(uniqueId)) {
            return playerDataMap.get(uniqueId);
        } else {
            addPlayerData(player);
        }
        return playerDataMap.get(uniqueId);
    }

    /**
     * Adds a player's data to the map.
     *
     * @param player The player to add.
     */
    private static void addPlayerData(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();

        if (!playerDataMap.containsKey(uniqueId)) {
            @NotNull PlayerData data = new PlayerData(uniqueId, player);
            playerDataMap.put(uniqueId, data);
        }
    }

    /**
     * Removes a player's data from the map.
     *
     * @param player The player to remove.
     */
    public static void removePlayerData(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();
        playerDataMap.remove(uniqueId);
    }
}
