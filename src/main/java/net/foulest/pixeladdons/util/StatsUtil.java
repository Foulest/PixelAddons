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
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokerusType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatsUtil {

    /**
     * Gets the stats panel of a Pokemon.
     *
     * @param player  The player to get the stats for.
     * @param pokemon The Pokemon to get the stats for.
     * @return The stats panel of the Pokemon.
     */
    public static @NotNull List<String> getStatsPanel(@NotNull Player player,
                                                      @NotNull Pokemon pokemon) {
        List<String> statsPanel = new ArrayList<>();

        // Get the EVs of the Pokemon
        int hpEV = pokemon.getEVs().getStat(StatsType.HP);
        int attackEV = pokemon.getEVs().getStat(StatsType.Attack);
        int defenceEV = pokemon.getEVs().getStat(StatsType.Defence);
        int spAttackEV = pokemon.getEVs().getStat(StatsType.SpecialAttack);
        int spDefenceEV = pokemon.getEVs().getStat(StatsType.SpecialDefence);
        int speedEV = pokemon.getEVs().getStat(StatsType.Speed);

        // Get the IVs of the Pokemon
        int hpIV = pokemon.getIVs().getStat(StatsType.HP);
        int attackIV = pokemon.getIVs().getStat(StatsType.Attack);
        int defenceIV = pokemon.getIVs().getStat(StatsType.Defence);
        int spAttackIV = pokemon.getIVs().getStat(StatsType.SpecialAttack);
        int spDefenceIV = pokemon.getIVs().getStat(StatsType.SpecialDefence);
        int speedIV = pokemon.getIVs().getStat(StatsType.Speed);

        // Define all placeholders and their corresponding values
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%color%", FormatUtil.getDisplayColor(pokemon));
        placeholders.put("%player%", player.getName());
        placeholders.put("%pokemon%", (pokemon.isEgg() ? "Egg" : pokemon.getSpecies().getPokemonName()));
        placeholders.put("%shinyStar%", pokemon.isShiny() ? " &6★" : "");
        placeholders.put("%PKRS%", (pokemon.getPokerus() != null && pokemon.getPokerus().type != EnumPokerusType.UNINFECTED) ? " &5(PKRS)" : "");
        placeholders.put("%gender%", pokemon.getGender() == Gender.Male ? "&b(M)" : (pokemon.getGender() == Gender.Female ? "&d(F)" : ""));
        placeholders.put("%level%", String.valueOf(pokemon.getLevel()));
        placeholders.put("%ability%", pokemon.getAbility().getLocalizedName());
        placeholders.put("%nature%", pokemon.getNature().getLocalizedName());
        placeholders.put("%natureEffect%", getNatureEffect(pokemon));
        placeholders.put("%hiddenPower%", HiddenPowerUtil.getHiddenPower(pokemon).getLocalizedName());

        placeholders.put("%hpEV%", FormatUtil.evColor(hpEV) + hpEV);
        placeholders.put("%attackEV%", FormatUtil.evColor(attackEV) + attackEV);
        placeholders.put("%defenceEV%", FormatUtil.evColor(defenceEV) + defenceEV);
        placeholders.put("%spAttackEV%", FormatUtil.evColor(spAttackEV) + spAttackEV);
        placeholders.put("%spDefenceEV%", FormatUtil.evColor(spDefenceEV) + spDefenceEV);
        placeholders.put("%speedEV%", FormatUtil.evColor(speedEV) + speedEV);

        placeholders.put("%hpIV%", (pokemon.getIVs().isHyperTrained(StatsType.HP) ? "&6&o" : FormatUtil.ivColor(hpIV)) + hpIV);
        placeholders.put("%attackIV%", (pokemon.getIVs().isHyperTrained(StatsType.Attack) ? "&6&o" : FormatUtil.ivColor(attackIV)) + attackIV);
        placeholders.put("%defenceIV%", (pokemon.getIVs().isHyperTrained(StatsType.Defence) ? "&6&o" : FormatUtil.ivColor(defenceIV)) + defenceIV);
        placeholders.put("%spAttackIV%", (pokemon.getIVs().isHyperTrained(StatsType.SpecialAttack) ? "&6&o" : FormatUtil.ivColor(spAttackIV)) + spAttackIV);
        placeholders.put("%spDefenceIV%", (pokemon.getIVs().isHyperTrained(StatsType.SpecialDefence) ? "&6&o" : FormatUtil.ivColor(spDefenceIV)) + spDefenceIV);
        placeholders.put("%speedIV%", (pokemon.getIVs().isHyperTrained(StatsType.Speed) ? "&6&o" : FormatUtil.ivColor(speedIV)) + speedIV);

        placeholders.put("%evPercent%", getEVPercent(pokemon));
        placeholders.put("%ivPercent%", getIVPercent(pokemon));

        // Iterate over each line in the settings and replace placeholders
        for (String message : Settings.statsPanelMessage) {
            String line = message;

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace(entry.getKey(), entry.getValue());
            }

            statsPanel.add(line);
        }
        return statsPanel;
    }

    /**
     * Gets the effect of a nature.
     *
     * @param pokemon The Pokemon to get the nature effect for.
     * @return The effect of the nature.
     */
    private static @NotNull String getNatureEffect(@NotNull Pokemon pokemon) {
        EnumNature nature = pokemon.getNature();

        // Get the increased and decreased stats
        StatsType increasedStat = nature.increasedStat;
        StatsType decreasedStat = nature.decreasedStat;

        // Format the stats
        String increasedStatFormatted = FormatUtil.formatStat(increasedStat.getUnlocalizedName());
        String decreasedStatFormatted = FormatUtil.formatStat(decreasedStat.getUnlocalizedName());

        // Check if the nature has an effect
        if (increasedStat == StatsType.None && decreasedStat == StatsType.None) {
            return "&7No Effect";
        } else {
            return "&a+" + increasedStatFormatted + " &c-" + decreasedStatFormatted;
        }
    }

    /**
     * Gets the percentage of EVs across all stats.
     *
     * @param pokemon The Pokemon to get the EV percentage for.
     * @return The percentage of EVs across all stats.
     */
    private static @NotNull String getEVPercent(@NotNull Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Get the total EVs across all stats
        int totalEVs = pokemon.getEVs().getStat(StatsType.HP)
                + pokemon.getEVs().getStat(StatsType.Attack)
                + pokemon.getEVs().getStat(StatsType.Defence)
                + pokemon.getEVs().getStat(StatsType.SpecialAttack)
                + pokemon.getEVs().getStat(StatsType.SpecialDefence)
                + pokemon.getEVs().getStat(StatsType.Speed);

        // Calculate the percentage
        double evPercent = ((double) totalEVs / 510) * 100;

        // Format and return the percentage
        return df.format(evPercent) + "%";
    }

    /**
     * Gets the percentage of IVs across all stats.
     *
     * @param pokemon The Pokemon to get the IV percentage for.
     * @return The percentage of IVs across all stats.
     */
    private static @NotNull String getIVPercent(@NotNull Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Calculate the total IVs across all stats
        int totalIVs = pokemon.getIVs().getStat(StatsType.HP)
                + pokemon.getIVs().getStat(StatsType.Attack)
                + pokemon.getIVs().getStat(StatsType.Defence)
                + pokemon.getIVs().getStat(StatsType.SpecialAttack)
                + pokemon.getIVs().getStat(StatsType.SpecialDefence)
                + pokemon.getIVs().getStat(StatsType.Speed);

        // Calculate the percentage
        double ivPercent = ((double) totalIVs / 186) * 100;

        // Format and return the percentage
        return df.format(ivPercent) + "%";
    }
}
