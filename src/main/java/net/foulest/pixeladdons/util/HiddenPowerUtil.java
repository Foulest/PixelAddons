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
package net.foulest.pixeladdons.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for hidden power.
 *
 * @author Foulest
 * @project PixelAddons
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class HiddenPowerUtil {

    /**
     * Gets the hidden power type of a Pokemon.
     *
     * @param pokemon Pokemon to get the hidden power type of.
     * @return Hidden power type of the Pokemon.
     */
    static EnumType getHiddenPower(@NotNull Pokemon pokemon) {
        int hp = (pokemon.getIVs().getStat(StatsType.HP) % 2 == 0) ? 0 : 1;
        int atk = (pokemon.getIVs().getStat(StatsType.Attack) % 2 == 0) ? 0 : 1;
        int def = (pokemon.getIVs().getStat(StatsType.Defence) % 2 == 0) ? 0 : 1;
        int spa = (pokemon.getIVs().getStat(StatsType.SpecialAttack) % 2 == 0) ? 0 : 1;
        int spd = (pokemon.getIVs().getStat(StatsType.SpecialDefence) % 2 == 0) ? 0 : 1;
        int spe = (pokemon.getIVs().getStat(StatsType.Speed) % 2 == 0) ? 0 : 1;
        int hiddenPowerInt = (int) Math.floor((double) getHiddenPowerInt(hp, atk, def, spe, spa, spd) / 63);

        // Returns the hidden power type.
        switch (hiddenPowerInt) {
            case 0:
                return EnumType.getAllTypes().get(6);
            case 1:
                return EnumType.getAllTypes().get(9);
            case 2:
                return EnumType.getAllTypes().get(7);
            case 3:
                return EnumType.getAllTypes().get(8);
            case 4:
                return EnumType.getAllTypes().get(12);
            case 5:
                return EnumType.getAllTypes().get(11);
            case 6:
                return EnumType.getAllTypes().get(13);
            case 7:
                return EnumType.getAllTypes().get(16);
            case 8:
                return EnumType.getAllTypes().get(1);
            case 9:
                return EnumType.getAllTypes().get(2);
            case 10:
                return EnumType.getAllTypes().get(4);
            case 11:
                return EnumType.getAllTypes().get(3);
            case 12:
                return EnumType.getAllTypes().get(10);
            case 13:
                return EnumType.getAllTypes().get(5);
            case 14:
                return EnumType.getAllTypes().get(14);
            case 15:
                return EnumType.getAllTypes().get(15);
            default:
                return EnumType.getAllTypes().get(17);
        }
    }

    private static int getHiddenPowerInt(int hp, int atk, int def, int spe, int spa, int spd) {
        return (hp + (2 * atk) + (4 * def) + (8 * spe) + (16 * spa) + (32 * spd)) * 15;
    }
}
