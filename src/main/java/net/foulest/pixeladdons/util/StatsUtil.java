package net.foulest.pixeladdons.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumPokerusType;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsUtil {

    public static List<String> getStats(Player owner, Pokemon pokemon) {
        StringBuilder message = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.#");

        // Handles eggs.
        if (pokemon.isEgg()) {
            if (pokemon.isLegendary()) {
                message.append("&d").append(owner.getName()).append("&d's Egg");
            } else {
                message.append("&a").append(owner.getName()).append("&a's Egg");
            }

            if (pokemon.isShiny()) {
                message.append(" &6★");
            }

            message.append("\n").append("&fAbility: &e").append(pokemon.getAbility().getLocalizedName());
            message.append("\n").append("&fNature: &e").append(pokemon.getNature().getLocalizedName());

            String increasedStat = FormatUtil.formatStat(pokemon.getNature().increasedStat.getUnlocalizedName());
            String decreasedStat = FormatUtil.formatStat(pokemon.getNature().decreasedStat.getUnlocalizedName());

            if (pokemon.getNature().increasedStat == StatsType.None && pokemon.getNature().decreasedStat == StatsType.None) {
                message.append(" &7(No Effect)");
            } else {
                message.append(" &7(&a+").append(increasedStat).append(" &c-").append(decreasedStat).append("&7)");
            }

            message.append("\n").append("&fHidden Power: &e").append(HiddenPowerUtil.getHiddenPower(pokemon).getLocalizedName());
            message.append("\n").append("&fEgg Cycles: &e").append(pokemon.getEggCycles());
            return convertToList(message);
        }

        if (pokemon.isLegendary()) {
            message.append("&d").append(owner.getName()).append("&d's ").append(pokemon.getSpecies().getPokemonName());
        } else {
            message.append("&a").append(owner.getName()).append("&a's ").append(pokemon.getSpecies().getPokemonName());
        }

        if (pokemon.isShiny()) {
            message.append(" &6★");
        }

        if (pokemon.getPokerus() != null && pokemon.getPokerus().type != EnumPokerusType.UNINFECTED) {
            message.append(" &5(PKRS)");
        }

        switch (pokemon.getGender()) {
            case Male:
                message.append(" &b(M)");
                break;

            case Female:
                message.append(" &d(F)");
                break;

            default:
                break;
        }

        if (pokemon.hasSpecFlag("untradeable")) {
            message.append(" &7(Locked)");
        }

        message.append("\n").append("&fLevel: &e").append(pokemon.getLevel())
                .append(" &7┃ ").append("&fAbility: &e").append(pokemon.getAbility().getLocalizedName());

        message.append("\n").append("&fNature: &e").append(pokemon.getNature().getLocalizedName());

        String increasedStat = FormatUtil.formatStat(pokemon.getNature().increasedStat.getUnlocalizedName());
        String decreasedStat = FormatUtil.formatStat(pokemon.getNature().decreasedStat.getUnlocalizedName());

        if (pokemon.getNature().increasedStat == StatsType.None && pokemon.getNature().decreasedStat == StatsType.None) {
            message.append(" &7(No Effect)");
        } else {
            message.append(" &7(&a+").append(increasedStat).append(" &c-").append(decreasedStat).append("&7)");
        }

        message.append("\n").append("&fHidden Power: &e").append(HiddenPowerUtil.getHiddenPower(pokemon).getLocalizedName());

        message.append("\n").append(" ");
        message.append("\n").append("&7(HP/Atk/Def/SpA/SpD/Spe)");

        String hpEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.HP)) + pokemon.getEVs().getStat(StatsType.HP);
        String attackEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.Attack)) + pokemon.getEVs().getStat(StatsType.Attack);
        String DefenceEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.Defence)) + pokemon.getEVs().getStat(StatsType.Defence);
        String spAttackEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.SpecialAttack)) + pokemon.getEVs().getStat(StatsType.SpecialAttack);
        String spDefenceEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.SpecialDefence)) + pokemon.getEVs().getStat(StatsType.SpecialDefence);
        String speedEV = FormatUtil.evColor(pokemon.getEVs().getStat(StatsType.Speed)) + pokemon.getEVs().getStat(StatsType.Speed);

        double evPercent = Double.parseDouble(df.format(((double) (pokemon.getEVs().getStat(StatsType.HP) + pokemon.getEVs().getStat(StatsType.Attack)
                + pokemon.getEVs().getStat(StatsType.Defence) + pokemon.getEVs().getStat(StatsType.SpecialAttack)
                + pokemon.getEVs().getStat(StatsType.SpecialDefence) + pokemon.getEVs().getStat(StatsType.Speed)) / 510) * 100));

        message.append("\n").append("&fEVs: &7")
                .append(hpEV).append(" ")
                .append(attackEV).append(" ")
                .append(DefenceEV).append(" ")
                .append(spAttackEV).append(" ")
                .append(spDefenceEV).append(" ")
                .append(speedEV).append(" &7(")
                .append(evPercent).append("%)");

        String hpIV = pokemon.getIVs().isHyperTrained(StatsType.HP) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.HP)) + pokemon.getIVs().getStat(StatsType.HP);
        String attackIV = pokemon.getIVs().isHyperTrained(StatsType.Attack) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.Attack)) + pokemon.getIVs().getStat(StatsType.Attack);
        String defenceIV = pokemon.getIVs().isHyperTrained(StatsType.Defence) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.Defence)) + pokemon.getIVs().getStat(StatsType.Defence);
        String spAttackIV = pokemon.getIVs().isHyperTrained(StatsType.SpecialAttack) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.SpecialAttack)) + pokemon.getIVs().getStat(StatsType.SpecialAttack);
        String spDefenceIV = pokemon.getIVs().isHyperTrained(StatsType.SpecialDefence) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.SpecialDefence)) + pokemon.getIVs().getStat(StatsType.SpecialDefence);
        String speedIV = pokemon.getIVs().isHyperTrained(StatsType.Speed) ? "&6&o31" : FormatUtil.ivColor(pokemon.getIVs().getStat(StatsType.Speed)) + pokemon.getIVs().getStat(StatsType.Speed);

        int hpIVAdj = pokemon.getIVs().isHyperTrained(StatsType.HP) ? 31 : pokemon.getIVs().getStat(StatsType.HP);
        int attackIVAdj = pokemon.getIVs().isHyperTrained(StatsType.Attack) ? 31 : pokemon.getIVs().getStat(StatsType.Attack);
        int defenceIVAdj = pokemon.getIVs().isHyperTrained(StatsType.Defence) ? 31 : pokemon.getIVs().getStat(StatsType.Defence);
        int spAttackIVAdj = pokemon.getIVs().isHyperTrained(StatsType.SpecialAttack) ? 31 : pokemon.getIVs().getStat(StatsType.SpecialAttack);
        int spDefenceIVAdj = pokemon.getIVs().isHyperTrained(StatsType.SpecialDefence) ? 31 : pokemon.getIVs().getStat(StatsType.SpecialDefence);
        int speedIVAdj = pokemon.getIVs().isHyperTrained(StatsType.Speed) ? 31 : pokemon.getIVs().getStat(StatsType.Speed);

        double ivPercent = Double.parseDouble(df.format(((double) (hpIVAdj + attackIVAdj + defenceIVAdj
                + spAttackIVAdj + spDefenceIVAdj + speedIVAdj) / 186) * 100));

        message.append("\n").append("&fIVs: &7")
                .append(hpIV).append(" ")
                .append(attackIV).append(" ")
                .append(defenceIV).append(" ")
                .append(spAttackIV).append(" ")
                .append(spDefenceIV).append(" ")
                .append(speedIV).append(" &7(")
                .append(ivPercent).append("%)");

        return convertToList(message);
    }

    public static List<String> convertToList(StringBuilder builder) {
        String[] splitData = builder.toString().split("\n");
        List<String> output = new ArrayList<>();

        Collections.addAll(output, splitData);
        return output;
    }
}
