package net.foulest.pixeladdons.util;

public class FormatUtil {

    public static String ivColor(int stat) {
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

    public static String evColor(int stat) {
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

    public static String formatStat(String stat) {
        stat = stat.replace("enum.stat.", "");
        stat = stat.replace("hp", "HP");
        stat = stat.replace("attack", "Atk");
        stat = stat.replace("defence", "Def");
        stat = stat.replace("specialAtk", "SpA");
        stat = stat.replace("specialDef", "SpD");
        stat = stat.replace("speed", "Spe");
        return stat;
    }
}
