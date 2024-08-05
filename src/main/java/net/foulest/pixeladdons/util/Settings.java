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

import lombok.*;
import net.foulest.pixeladdons.PixelAddons;
import net.foulest.pixeladdons.util.yaml.CustomYamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Utility class for settings.
 *
 * @author Foulest
 * @project PixelAddons
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Settings {

    // File settings
    public static File file;
    public static FileConfiguration config;
    public static String fileName = "config.yml";

    // Command settings
    public static List<String> commandsOnJoin = new ArrayList<>();
    public static boolean endBattleCommandEnabled;
    public static boolean hatchCommandEnabled;
    public static int hatchCommandCost;
    public static boolean rerollCommandEnabled;
    public static long rerollCommandCooldown;
    public static boolean showCommandEnabled;
    public static boolean statsCommandEnabled;
    public static boolean statsCommandViewOtherPlayers;

    // Pokemon colors
    public static String normalColor;
    public static String shinyColor;
    public static String eggColor;
    public static String legendaryColor;
    public static String ultraBeastColor;
    public static String pickupColor;

    // Misc command messages
    public static String commandNoPermissionMessage;
    public static String commandDisabledMessage;
    public static String commandUsageMessage;

    // End battle command messages
    public static String failedToEndBattleMessage;
    public static String battleEndedSuccessfullyMessage;
    public static String notInBattleMessage;

    // Hatch command messages
    public static String starterNotFoundMessage;
    public static String commandInvalidUsageMessage;
    public static String bankAccountNotFoundMessage;
    public static String notEnoughMoneyMessage;
    public static String pokemonHatchedMessage;
    public static String confirmHatchMessage;
    public static String hatchCommandCancelledMessage;

    // Reroll command messages
    public static String rerollCommandCooldownMessage;
    public static String rerollVoteSubmittedMessage;
    public static String rerollVoteCancelledMessage;
    public static String rerollHuntMessage;
    public static String rerollHuntMessageWithPlayer;
    public static String showMessage;

    // Event messages
    public static String evGainMessage;
    public static String evIncreaseMessage;
    public static String catchMessage;
    public static String pickupMessage;
    public static String eggHatchMessage;
    public static String fossilRevivalMessage;
    public static String chooseStarterMessage;

    // Receive pokemon messages
    public static String receivePokemonCustomMessage;
    public static String receivePokemonCommandMessage;
    public static String receivePokemonSelectMessage;
    public static String receivePokemonChristmasMessage;

    // Stats panel message
    public static List<String> statsPanelMessage;

    // Custom hidden ability rate settings
    public static boolean customHiddenAbilityRateEnabled;
    public static int customHiddenAbilityRateOdds;
    public static String customHiddenAbilityRatePermission;

    // Custom boss rate settings
    public static boolean customBossRateEnabled;
    public static int customBossRateOdds;
    public static String customBossRatePermission;

    // Custom shiny rate settings
    public static boolean customShinyRateEnabled;
    public static int customShinyRateOdds;
    public static String customShinyRatePermission;

    // Custom pokerus rate settings
    public static boolean customPokerusRateEnabled;
    public static int customPokerusRateOdds;
    public static String customPokerusRatePermission;

    /**
     * Loads the configuration file and values.
     */
    public static void loadSettings() {
        loadConfigFile();
        loadConfigValues();
    }

    /**
     * Initializes the configuration file and loads defaults.
     */
    @SuppressWarnings("OverlyBroadCatchBlock")
    private static void loadConfigFile() {
        try {
            // First, attempt to load the default configuration as a stream to check if it exists in the plugin JAR
            @Cleanup InputStream defConfigStream = PixelAddons.getInstance().getResource(fileName);

            if (defConfigStream == null) {
                // Log a warning if the default configuration cannot be found within the JAR
                MessageUtil.log(Level.WARNING, "Could not find " + fileName + " in the plugin JAR.");
                return;
            }

            // Proceed to check if the config file exists in the plugin's data folder
            // and save the default config from the JAR if not
            File dataFolder = PixelAddons.getInstance().getDataFolder();
            file = new File(dataFolder, fileName);
            if (!file.exists()) {
                PixelAddons.getInstance().saveResource(fileName, false);
            }

            // Now that we've ensured the file exists (either it already did, or we've just created it),
            // we can safely load it into our CustomYamlConfiguration object
            config = CustomYamlConfiguration.loadConfiguration(file);
            @Cleanup InputStreamReader reader = new InputStreamReader(defConfigStream, StandardCharsets.UTF_8);
            CustomYamlConfiguration defConfig = CustomYamlConfiguration.loadConfiguration(reader);

            // Ensure defaults are applied
            config.setDefaults(defConfig);
            config.options().copyDefaults(true);
            saveConfig(); // Save the config with defaults applied
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the configuration file.
     */
    private static void saveConfig() {
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads configuration values into the relevant static fields.
     */
    private static void loadConfigValues() {
        // Command settings
        commandsOnJoin = config.getStringList("pixeladdons.commands.on-first-join");
        endBattleCommandEnabled = config.getBoolean("pixeladdons.commands.end-battle.enabled");
        hatchCommandEnabled = config.getBoolean("pixeladdons.commands.hatch.enabled");
        hatchCommandCost = config.getInt("pixeladdons.commands.hatch.cost");
        rerollCommandEnabled = config.getBoolean("pixeladdons.commands.reroll.enabled");
        rerollCommandCooldown = config.getLong("pixeladdons.commands.reroll.cooldown");
        showCommandEnabled = config.getBoolean("pixeladdons.commands.show.enabled");
        statsCommandEnabled = config.getBoolean("pixeladdons.commands.stats.enabled");
        statsCommandViewOtherPlayers = config.getBoolean("pixeladdons.commands.stats.view-other-players");

        // Pokemon colors
        normalColor = config.getString("pixeladdons.messages.colors.normal");
        shinyColor = config.getString("pixeladdons.messages.colors.shiny");
        eggColor = config.getString("pixeladdons.messages.colors.egg");
        legendaryColor = config.getString("pixeladdons.messages.colors.legendary");
        ultraBeastColor = config.getString("pixeladdons.messages.colors.ultra-beast");
        pickupColor = config.getString("pixeladdons.messages.colors.pickup");

        // Misc command messages
        commandNoPermissionMessage = config.getString("pixeladdons.messages.commands.misc.no-permission");
        commandInvalidUsageMessage = config.getString("pixeladdons.messages.commands.invalid-usage");
        commandDisabledMessage = config.getString("pixeladdons.messages.commands.misc.disabled");
        commandUsageMessage = config.getString("pixeladdons.messages.commands.misc.usage");

        // End battle command messages
        failedToEndBattleMessage = config.getString("pixeladdons.messages.commands.end-battle.failed");
        battleEndedSuccessfullyMessage = config.getString("pixeladdons.messages.commands.end-battle.success");
        notInBattleMessage = config.getString("pixeladdons.messages.commands.end-battle.not-in-battle");

        // Hatch command messages
        starterNotFoundMessage = config.getString("pixeladdons.messages.commands.hatch.starter-not-found");
        bankAccountNotFoundMessage = config.getString("pixeladdons.messages.commands.hatch.bank-account-not-found");
        notEnoughMoneyMessage = config.getString("pixeladdons.messages.commands.hatch.not-enough-money");
        pokemonHatchedMessage = config.getString("pixeladdons.messages.commands.hatch.pokemon-hatched");
        confirmHatchMessage = config.getString("pixeladdons.messages.commands.hatch.confirm-hatch");
        hatchCommandCancelledMessage = config.getString("pixeladdons.messages.commands.hatch.cancelled-hatch");

        // Reroll command messages
        rerollCommandCooldownMessage = config.getString("pixeladdons.messages.commands.reroll.cooldown");
        rerollVoteSubmittedMessage = config.getString("pixeladdons.messages.commands.reroll.vote-submitted");
        rerollVoteCancelledMessage = config.getString("pixeladdons.messages.commands.reroll.vote-cancelled");
        rerollHuntMessage = config.getString("pixeladdons.messages.commands.reroll.success");
        rerollHuntMessageWithPlayer = config.getString("pixeladdons.messages.commands.reroll.success-other");

        // Show command messages
        showMessage = config.getString("pixeladdons.messages.commands.show.message");

        // Event messages
        evGainMessage = config.getString("pixeladdons.messages.events.ev-gain");
        evIncreaseMessage = config.getString("pixeladdons.messages.events.ev-increase");
        catchMessage = config.getString("pixeladdons.messages.events.catch");
        pickupMessage = config.getString("pixeladdons.messages.events.pickup");
        eggHatchMessage = config.getString("pixeladdons.messages.events.egg-hatch");
        fossilRevivalMessage = config.getString("pixeladdons.messages.events.fossil-revival");
        chooseStarterMessage = config.getString("pixeladdons.messages.events.choose-starter");

        // Pokemon receive messages
        receivePokemonCustomMessage = config.getString("pixeladdons.messages.receive-pokemon.custom");
        receivePokemonCommandMessage = config.getString("pixeladdons.messages.receive-pokemon.command");
        receivePokemonSelectMessage = config.getString("pixeladdons.messages.receive-pokemon.select");
        receivePokemonChristmasMessage = config.getString("pixeladdons.messages.receive-pokemon.christmas");

        // Stats panel message
        statsPanelMessage = config.getStringList("pixeladdons.messages.stats-panel");

        // Custom hidden ability rate settings
        customHiddenAbilityRateEnabled = config.getBoolean("pixeladdons.custom-rates.hidden-ability-rate.enabled");
        customHiddenAbilityRateOdds = Math.max(0, config.getInt("pixeladdons.custom-rates.hidden-ability-rate.new-odds"));
        customHiddenAbilityRatePermission = config.getString("pixeladdons.custom-rates.hidden-ability-rate.permission");

        // Custom boss rate settings
        customBossRateEnabled = config.getBoolean("pixeladdons.custom-rates.boss-rate.enabled");
        customBossRateOdds = Math.max(0, config.getInt("pixeladdons.custom-rates.boss-rate.new-odds"));
        customBossRatePermission = config.getString("pixeladdons.custom-rates.boss-rate.permission");

        // Custom shiny rate settings
        customShinyRateEnabled = config.getBoolean("pixeladdons.custom-rates.shiny-rate.enabled");
        customShinyRateOdds = Math.max(0, config.getInt("pixeladdons.custom-rates.shiny-rate.new-odds"));
        customShinyRatePermission = config.getString("pixeladdons.custom-rates.shiny-rate.permission");

        // Custom pokerus rate settings
        customPokerusRateEnabled = config.getBoolean("pixeladdons.custom-rates.pokerus-rate.enabled");
        customPokerusRateOdds = Math.max(0, config.getInt("pixeladdons.custom-rates.pokerus-rate.new-odds"));
        customPokerusRatePermission = config.getString("pixeladdons.custom-rates.pokerus-rate.permission");
    }
}
