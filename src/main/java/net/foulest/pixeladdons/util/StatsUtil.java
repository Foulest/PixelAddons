package net.foulest.pixeladdons.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumPokerusType;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsUtil {

    public static List<String> getStats(Player player, Pokemon pokemon) {
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
        for (String line : Settings.statsPanelMessage) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace(entry.getKey(), entry.getValue());
            }
            statsPanel.add(line);
        }
        return statsPanel;
    }

    public static String getNatureEffect(Pokemon pokemon) {
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

    public static String getEVPercent(Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Get the total EVs across all stats
        int totalEVs = pokemon.getEVs().getStat(StatsType.HP) +
                pokemon.getEVs().getStat(StatsType.Attack) +
                pokemon.getEVs().getStat(StatsType.Defence) +
                pokemon.getEVs().getStat(StatsType.SpecialAttack) +
                pokemon.getEVs().getStat(StatsType.SpecialDefence) +
                pokemon.getEVs().getStat(StatsType.Speed);

        // Calculate the percentage
        double evPercent = ((double) totalEVs / 510) * 100;

        // Format and return the percentage
        return df.format(evPercent) + "%";
    }

    public static String getIVPercent(Pokemon pokemon) {
        DecimalFormat df = new DecimalFormat("#.#");

        // Calculate the total IVs across all stats
        int totalIVs = pokemon.getIVs().getStat(StatsType.HP) +
                pokemon.getIVs().getStat(StatsType.Attack) +
                pokemon.getIVs().getStat(StatsType.Defence) +
                pokemon.getIVs().getStat(StatsType.SpecialAttack) +
                pokemon.getIVs().getStat(StatsType.SpecialDefence) +
                pokemon.getIVs().getStat(StatsType.Speed);

        // Calculate the percentage
        double ivPercent = ((double) totalIVs / 186) * 100;

        // Format and return the percentage
        return df.format(ivPercent) + "%";
    }
}
