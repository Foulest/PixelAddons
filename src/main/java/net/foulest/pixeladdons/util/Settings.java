package net.foulest.pixeladdons.util;

import net.foulest.pixeladdons.PixelAddons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Settings {

    public static File file;
    public static FileConfiguration config;
    public static List<String> commandsOnJoin = new ArrayList<>();

    public static void setupSettings() {
        file = new File(PixelAddons.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                MessageUtil.log("Couldn't create the config file.");
                ex.printStackTrace();
                return;
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        config.addDefault("commands-on-first-join", Collections.<String>emptyList());
        config.options().copyDefaults(true);

        try {
            config.save(file);
        } catch (IOException ex) {
            MessageUtil.log("Couldn't save the config file.");
        }
    }

    public static void loadSettings() {
        config = YamlConfiguration.loadConfiguration(file);
        commandsOnJoin = config.getStringList("commands-on-first-join");
    }

    public static void saveSettings() {
        try {
            config.save(file);
        } catch (IOException ex) {
            MessageUtil.log("Couldn't save the config file.");
        }
    }
}
