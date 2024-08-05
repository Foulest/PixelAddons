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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for formatting.
 *
 * @author Foulest
 * @project PixelAddons
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatUtil {

    /**
     * Gets the display color of a Pokemon.
     *
     * @param pokemon Pokemon to get the display color of.
     * @return Display color of the Pokemon.
     */
    public static String getDisplayColor(@NotNull Pokemon pokemon) {
        if (pokemon.isShiny()) {
            return Settings.shinyColor;
        } else if (pokemon.getSpecies().isLegendary()) {
            return Settings.legendaryColor;
        } else if (pokemon.getSpecies().isUltraBeast()) {
            return Settings.ultraBeastColor;
        } else if (pokemon.isEgg()) {
            return Settings.eggColor;
        }
        return Settings.normalColor;
    }

    /**
     * Gets the color of an IV based on its value.
     *
     * @param stat Stat to get color of.
     * @return Color of the stat.
     */
    @Contract(pure = true)
    static @NotNull String ivColor(int stat) {
        if (stat >= 0 && stat < 10) {
            return "&c";
        } else if (stat >= 10 && stat < 20) {
            return "&e";
        } else if (stat >= 20 && stat < 28) {
            return "&a";
        } else if (stat == 31) {
            return "&2&o";
        } else if (stat >= 28) {
            return "&2";
        }
        return "&7";
    }

    /**
     * Gets the color of an EV based on its value.
     *
     * @param stat Stat to get color of.
     * @return Color of the stat.
     */
    @Contract(pure = true)
    static @NotNull String evColor(int stat) {
        if (stat >= 0 && stat < 50) {
            return "&c";
        } else if (stat >= 50 && stat < 150) {
            return "&e";
        } else if (stat >= 150 && stat < 200) {
            return "&a";
        } else if (stat == 252) {
            return "&2&o";
        } else if (stat >= 200) {
            return "&2";
        }
        return "&7";
    }

    /**
     * Formats a stat's name.
     *
     * @param stat Stat to format.
     * @return Formatted stat.
     */
    static @NotNull String formatStat(String stat) {
        stat = stat.replace("enum.stat.", "");
        stat = stat.replace("hp", "HP");
        stat = stat.replace("attack", "Atk");
        stat = stat.replace("defence", "Def");
        stat = stat.replace("specialAtk", "SpA");
        stat = stat.replace("specialDef", "SpD");
        stat = stat.replace("speed", "Spe");
        return stat;
    }

    /**
     * Converts a StringBuilder to a List.
     *
     * @param builder StringBuilder to convert.
     * @return Converted List.
     */
    public static @NotNull List<String> convertToList(@NotNull CharSequence builder) {
        String[] splitData = builder.toString().split("\n");
        List<String> output = new ArrayList<>();

        Collections.addAll(output, splitData);
        return output;
    }
}
