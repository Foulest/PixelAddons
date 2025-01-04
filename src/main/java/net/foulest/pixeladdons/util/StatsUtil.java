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
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Pokerus;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokerusType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import lombok.Data;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class StatsUtil {

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
        IVStore iVs = pokemon.getIVs();
        int hpIV = iVs.getStat(StatsType.HP);
        int attackIV = iVs.getStat(StatsType.Attack);
        int defenceIV = iVs.getStat(StatsType.Defence);
        int spAttackIV = iVs.getStat(StatsType.SpecialAttack);
        int spDefenceIV = iVs.getStat(StatsType.SpecialDefence);
        int speedIV = iVs.getStat(StatsType.Speed);

        String playerName = player.getName();
        boolean isEgg = pokemon.isEgg();
        EnumSpecies species = pokemon.getSpecies();
        String pokemonName = species.getPokemonName();
        boolean shiny = pokemon.isShiny();
        Pokerus pokerus = pokemon.getPokerus();
        Gender gender = pokemon.getGender();
        int level = pokemon.getLevel();
        AbilityBase ability = pokemon.getAbility();
        String abilityName = ability.getLocalizedName();
        EnumNature nature = pokemon.getNature();
        String natureName = nature.getLocalizedName();
        String hiddenPowerName = HiddenPowerUtil.getHiddenPower(pokemon).getLocalizedName();
        String genderSymbol = "";

        // Gets the gender symbol for the Pokemon.
        switch (gender) {
            case Male:
                genderSymbol = "&b(M)";
                break;
            case Female:
                genderSymbol = "&d(F)";
                break;
            default:
                break;
        }

        // Define all placeholders and their corresponding values
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%color%", FormatUtil.getDisplayColor(pokemon));
        placeholders.put("%player%", playerName);
        placeholders.put("%pokemon%", (isEgg ? "Egg" : pokemonName));
        placeholders.put("%shinyStar%", shiny ? " &6★" : "");
        placeholders.put("%PKRS%", (pokerus != null && pokerus.type != EnumPokerusType.UNINFECTED) ? " &5(PKRS)" : "");
        placeholders.put("%gender%", genderSymbol);
        placeholders.put("%level%", String.valueOf(level));
        placeholders.put("%ability%", abilityName);
        placeholders.put("%nature%", natureName);
        placeholders.put("%natureEffect%", getNatureEffect(pokemon));
        placeholders.put("%hiddenPower%", hiddenPowerName);

        placeholders.put("%hpEV%", FormatUtil.evColor(hpEV) + hpEV);
        placeholders.put("%attackEV%", FormatUtil.evColor(attackEV) + attackEV);
        placeholders.put("%defenceEV%", FormatUtil.evColor(defenceEV) + defenceEV);
        placeholders.put("%spAttackEV%", FormatUtil.evColor(spAttackEV) + spAttackEV);
        placeholders.put("%spDefenceEV%", FormatUtil.evColor(spDefenceEV) + spDefenceEV);
        placeholders.put("%speedEV%", FormatUtil.evColor(speedEV) + speedEV);

        boolean hyperTrainedHP = iVs.isHyperTrained(StatsType.HP);
        boolean hyperTrainedAtk = iVs.isHyperTrained(StatsType.Attack);
        boolean hyperTrainedDef = iVs.isHyperTrained(StatsType.Defence);
        boolean hyperTrainedSpA = iVs.isHyperTrained(StatsType.SpecialAttack);
        boolean hyperTrainedSpD = iVs.isHyperTrained(StatsType.SpecialDefence);
        boolean hyperTrainedSpe = iVs.isHyperTrained(StatsType.Speed);

        placeholders.put("%hpIV%", (hyperTrainedHP ? "&6&o" : FormatUtil.ivColor(hpIV)) + hpIV);
        placeholders.put("%attackIV%", (hyperTrainedAtk ? "&6&o" : FormatUtil.ivColor(attackIV)) + attackIV);
        placeholders.put("%defenceIV%", (hyperTrainedDef ? "&6&o" : FormatUtil.ivColor(defenceIV)) + defenceIV);
        placeholders.put("%spAttackIV%", (hyperTrainedSpA ? "&6&o" : FormatUtil.ivColor(spAttackIV)) + spAttackIV);
        placeholders.put("%spDefenceIV%", (hyperTrainedSpD ? "&6&o" : FormatUtil.ivColor(spDefenceIV)) + spDefenceIV);
        placeholders.put("%speedIV%", (hyperTrainedSpe ? "&6&o" : FormatUtil.ivColor(speedIV)) + speedIV);

        placeholders.put("%evPercent%", getEVPercent(pokemon));
        placeholders.put("%ivPercent%", getIVPercent(pokemon));

        // Iterate over each line in the settings and replace placeholders
        for (String message : Settings.statsPanelMessage) {
            String line = message;

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                line = line.replace(key, value);
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

        String increasedName = increasedStat.getUnlocalizedName();
        String decreasedName = decreasedStat.getUnlocalizedName();

        // Format the stats
        String increasedStatFormatted = FormatUtil.formatStat(increasedName);
        String decreasedStatFormatted = FormatUtil.formatStat(decreasedName);

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
