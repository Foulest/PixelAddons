package net.foulest.pixeladdons.util;

import net.foulest.pixeladdons.PixelAddons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Settings {

    public static File file;
    public static FileConfiguration config;

    public static List<String> commandsOnJoin = new ArrayList<>();
    public static boolean pixelHuntIntegration = true;
    public static long pixelHuntCooldown = 900;

    /**
     * Initialize and set up default configuration values.
     */
    public static void setupSettings() {
        initConfig();
        setDefaultConfigValues();
        saveConfig();
    }

    /**
     * Loads configuration values into the relevant static fields.
     */
    public static void loadSettings() {
        if (!file.exists()) {
            setupSettings();
        }

        config = YamlConfiguration.loadConfiguration(file);

        commandsOnJoin = config.getStringList("commands-on-first-join");
        pixelHuntIntegration = config.getBoolean("addons.pixelhunt.enabled");
        pixelHuntCooldown = config.getLong("addons.pixelhunt.cooldown");
    }

    /**
     * Saves the current settings into the configuration file.
     */
    public static void saveSettings() {
        config.set("commands-on-first-join", commandsOnJoin);
        config.set("addons.pixelhunt.enabled", pixelHuntIntegration);
        config.set("addons.pixelhunt.cooldown", pixelHuntCooldown);

        saveConfig();
    }

    /**
     * Initializes the configuration file.
     */
    private static void initConfig() {
        file = new File(PixelAddons.instance.getDataFolder(), "settings.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                MessageUtil.log(Level.WARNING, "Couldn't create the config file.");
                ex.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Sets the default values for the configuration file.
     */
    private static void setDefaultConfigValues() {
        config.addDefault("commands-on-first-join", Collections.<String>emptyList());
        config.addDefault("addons.pixelhunt.enabled", true);
        config.addDefault("addons.pixelhunt.cooldown", 900);

        config.options().copyDefaults(true);
    }

    /**
     * Saves the configuration file.
     */
    private static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException ex) {
            MessageUtil.log(Level.WARNING, "Couldn't save the config file.");
        }
    }
}
