package net.foulest.pixeladdons.util;

import net.foulest.pixeladdons.PixelAddons;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Settings {

    public static File file;
    public static FileConfiguration config;

    // Commands on first join
    public static List<String> commandsOnJoin = new ArrayList<>();

    // End battle command
    public static boolean endBattleCommandEnabled = true;

    // Hatch command
    public static boolean hatchCommandEnabled = true;
    public static int hatchCommandCost = 5000;

    // Reroll command
    public static boolean rerollCommandEnabled = true;
    public static long rerollCommandCooldown = 900;

    // Show command
    public static boolean showCommandEnabled = true;

    // Stats command
    public static boolean statsCommandEnabled = true;
    public static boolean statsCommandViewOtherPlayers = true;

    // Pokemon colors
    public static String normalColor = "&a";
    public static String shinyColor = "&6";
    public static String eggColor = "&b";
    public static String legendaryColor = "&d";
    public static String ultraBeastColor = "&d";
    public static String pickupColor = "&e";

    // Command messages
    public static String commandNoPermissionMessage = "&cNo permission.";
    public static String commandDisabledMessage = "&cThe %command% command is disabled.";
    public static String commandUsageMessage = "&cUsage: %usage%";

    // End battle command messages
    public static String failedToEndBattleMessage = "&cFailed to end battle.";
    public static String battleEndedSuccessfullyMessage = "&aBattle ended successfully.";
    public static String notInBattleMessage = "&cYou are not in a battle.";

    // Hatch command messages
    public static String starterNotFoundMessage = "&cStarter Pokemon not found.";
    public static String invalidSlotMessage = "&cInvalid slot. (%reason%)";
    public static String bankAccountNotFoundMessage = "&cBank account not found.";
    public static String notEnoughMoneyMessage = "&cYou need at least $%amount% to hatch this egg.";
    public static String pokemonHatchedMessage = "&aYour %pokemon% was successfully hatched for $%amount%.";
    public static String confirmHatchMessage = "&eHatch this egg for &a$%amount%&e? Run the command again to confirm.";
    public static String hatchCommandCancelledMessage = "&cHatch command cancelled due to inactivity.";

    // Reroll command messages
    public static String rerollCommandCooldownMessage = "&cYou must wait &e%time% &cbetween re-rolls.";
    public static String rerollVoteSubmittedMessage = "&e[Hunt] &f%player% &7has voted to re-roll the hunt. &e(%votes%/%online%)";
    public static String rerollVoteCancelledMessage = "&e[Hunt] &f%player% &7has cancelled their re-roll vote. &e(%votes%/%online%)";
    public static String rerollHuntMessage = "&e[Hunt] &7The hunt has been &fre-rolled&7!";
    public static String rerollHuntMessageWithPlayer = "&e[Hunt] &7The hunt has been &fre-rolled &7by &f%player%&7!";

    // Show command messages
    public static String showMessage = "&r%player% is showing off their %color%[%pokemon%]";

    // Stats command messages
    public static String statsViewOtherPlayersMessage = "&cYou do not have permission to view other players' stats.";

    // EV gain messages
    public static String evGainMessage = "&fYour &a%pokemon% &fgained %evGains% &fEVs!";
    public static String evIncreaseMessage = "&a+%diff% %stat% &7(%newEVs%)";

    // Catch messages
    public static String catchMessage = "&r%player% caught a wild %color%[%pokemon%]";

    // Pickup messages
    public static String pickupMessage = "&rYour &e%pokemon% &fpicked up %an% %color%[%itemName%]";

    // Egg hatch messages
    public static String eggHatchMessage = "&r%player%'s egg hatched into %color%[%pokemon%]";

    // Pokemon receive messages
    public static String pokemonReceiveCustomMessage = "&7(Custom) &r%player% received a wild %color%[%pokemon%]";
    public static String pokemonReceiveCommandMessage = "&7(Command) &r%player% received a wild %color%[%pokemon%]";
    public static String pokemonReceiveSelectMessage = "&7(Select) &r%player% received a wild %color%[%pokemon%]";
    public static String pokemonReceiveChristmasMessage = "&r%player% received a wild %color%[%pokemon%] &rfor christmas!";

    // Fossil revival messages
    public static String fossilRevivalMessage = "&r%player% revived %an% %color%[%pokemon%] &rfrom a fossil!";

    // Choose starter message
    public static String chooseStarterMessage = "&r%player% chose %color%[%pokemon%] &ras their starter!";

    // Stats panel message
    public static List<String> statsPanelMessage = Arrays.asList(
            "&r%color%%player%'s %pokemon%%shinyStar%%PKRS% %gender%",
            "&rLevel: &e%level% &7┃ &rAbility: &e%ability%",
            "&rNature: &e%nature% &7(%natureEffect%&7)",
            "&rHidden Power: &e%hiddenPower%",
            "&7",
            "&7(HP/Atk/Def/SpA/SpD/Spe)",
            "&rEVs: &r%hpEV% %attackEV% %defenceEV% %spAttackEV% %spDefenceEV% %speedEV% &7(%evPercent%)",
            "&rIVs: &r%hpIV% %attackIV% %defenceIV% %spAttackIV% %spDefenceIV% %speedIV% &7(%ivPercent%)");

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

        commandsOnJoin = config.getStringList("commands.on-first-join");

        endBattleCommandEnabled = config.getBoolean("commands.end-battle.enabled");

        hatchCommandEnabled = config.getBoolean("commands.hatch.enabled");
        hatchCommandCost = config.getInt("commands.hatch.cost");

        rerollCommandEnabled = config.getBoolean("commands.reroll.enabled");
        rerollCommandCooldown = config.getLong("commands.reroll.cooldown");

        showCommandEnabled = config.getBoolean("commands.show.enabled");

        statsCommandEnabled = config.getBoolean("commands.stats.enabled");
        statsCommandViewOtherPlayers = config.getBoolean("commands.stats.view-other-players");

        normalColor = config.getString("messages.colors.normal");
        shinyColor = config.getString("messages.colors.shiny");
        eggColor = config.getString("messages.colors.egg");
        legendaryColor = config.getString("messages.colors.legendary");
        ultraBeastColor = config.getString("messages.colors.ultra-beast");
        pickupColor = config.getString("messages.colors.pickup");

        commandNoPermissionMessage = config.getString("messages.commands.misc.no-permission");
        commandDisabledMessage = config.getString("messages.commands.misc.disabled");
        commandUsageMessage = config.getString("messages.commands.misc.usage");

        failedToEndBattleMessage = config.getString("messages.commands.end-battle.failed");
        battleEndedSuccessfullyMessage = config.getString("messages.commands.end-battle.success");
        notInBattleMessage = config.getString("messages.commands.end-battle.not-in-battle");

        starterNotFoundMessage = config.getString("messages.commands.hatch.starter-not-found");
        invalidSlotMessage = config.getString("messages.commands.hatch.invalid-slot");
        bankAccountNotFoundMessage = config.getString("messages.commands.hatch.bank-account-not-found");
        notEnoughMoneyMessage = config.getString("messages.commands.hatch.not-enough-money");
        pokemonHatchedMessage = config.getString("messages.commands.hatch.pokemon-hatched");
        confirmHatchMessage = config.getString("messages.commands.hatch.confirm-hatch");
        hatchCommandCancelledMessage = config.getString("messages.commands.hatch.cancelled");

        rerollCommandCooldownMessage = config.getString("messages.commands.reroll.cooldown");
        rerollVoteSubmittedMessage = config.getString("messages.commands.reroll.vote-submitted");
        rerollVoteCancelledMessage = config.getString("messages.commands.reroll.vote-cancelled");
        rerollHuntMessage = config.getString("messages.commands.reroll.success");
        rerollHuntMessageWithPlayer = config.getString("messages.commands.reroll.success-other");

        showMessage = config.getString("messages.misc-messages.show-message");

        statsViewOtherPlayersMessage = config.getString("messages.commands.stats.view-others");

        pokemonReceiveCustomMessage = config.getString("messages.misc-messages.pokemon-receive.custom");
        pokemonReceiveCommandMessage = config.getString("messages.misc-messages.pokemon-receive.command");
        pokemonReceiveSelectMessage = config.getString("messages.misc-messages.pokemon-receive.select");
        pokemonReceiveChristmasMessage = config.getString("messages.misc-messages.pokemon-receive.christmas");

        evGainMessage = config.getString("messages.misc-messages.ev-gain");
        evIncreaseMessage = config.getString("messages.misc-messages.ev-increase");
        catchMessage = config.getString("messages.misc-messages.catch");
        pickupMessage = config.getString("messages.misc-messages.pickup");
        eggHatchMessage = config.getString("messages.misc-messages.egg-hatch");
        fossilRevivalMessage = config.getString("messages.misc-messages.fossil-revival");
        chooseStarterMessage = config.getString("messages.misc-messages.choose-starter");

        statsPanelMessage = config.getStringList("messages.stats-panel");
    }

    /**
     * Saves the current settings into the configuration file.
     */
    public static void saveSettings() {
        config.set("commands.on-first-join", commandsOnJoin);

        config.set("commands.end-battle.enabled", endBattleCommandEnabled);

        config.set("commands.hatch.enabled", hatchCommandEnabled);
        config.set("commands.hatch.cost", hatchCommandCost);

        config.set("commands.reroll.enabled", rerollCommandEnabled);
        config.set("commands.reroll.cooldown", rerollCommandCooldown);

        config.set("commands.show.enabled", showCommandEnabled);

        config.set("commands.stats.enabled", statsCommandEnabled);
        config.set("commands.stats.view-other-players", statsCommandViewOtherPlayers);

        config.set("messages.colors.normal", normalColor);
        config.set("messages.colors.shiny", shinyColor);
        config.set("messages.colors.egg", eggColor);
        config.set("messages.colors.legendary", legendaryColor);
        config.set("messages.colors.ultra-beast", ultraBeastColor);
        config.set("messages.colors.pickup", pickupColor);

        config.set("messages.commands.misc.no-permission", commandNoPermissionMessage);
        config.set("messages.commands.misc.command-disabled", commandDisabledMessage);
        config.set("messages.commands.misc.command-usage", commandUsageMessage);

        config.set("messages.commands.end-battle.failed", failedToEndBattleMessage);
        config.set("messages.commands.end-battle.success", battleEndedSuccessfullyMessage);
        config.set("messages.commands.end-battle.not-in-battle", notInBattleMessage);

        config.set("messages.commands.hatch.starter-not-found", starterNotFoundMessage);
        config.set("messages.commands.hatch.invalid-slot", invalidSlotMessage);
        config.set("messages.commands.hatch.bank-account-not-found", bankAccountNotFoundMessage);
        config.set("messages.commands.hatch.not-enough-money", notEnoughMoneyMessage);
        config.set("messages.commands.hatch.pokemon-hatched", pokemonHatchedMessage);
        config.set("messages.commands.hatch.confirm-hatch", confirmHatchMessage);
        config.set("messages.commands.hatch.cancelled", hatchCommandCancelledMessage);

        config.set("messages.commands.reroll.cooldown", rerollCommandCooldownMessage);
        config.set("messages.commands.reroll.vote-submitted", rerollVoteSubmittedMessage);
        config.set("messages.commands.reroll.vote-cancelled", rerollVoteCancelledMessage);
        config.set("messages.commands.reroll.success", rerollHuntMessage);
        config.set("messages.commands.reroll.success-other", rerollHuntMessageWithPlayer);

        config.set("messages.commands.stats.view-others", statsViewOtherPlayersMessage);

        config.set("messages.misc-messages.pokemon-receive.custom", pokemonReceiveCustomMessage);
        config.set("messages.misc-messages.pokemon-receive.command", pokemonReceiveCommandMessage);
        config.set("messages.misc-messages.pokemon-receive.select", pokemonReceiveSelectMessage);
        config.set("messages.misc-messages.pokemon-receive.christmas", pokemonReceiveChristmasMessage);

        config.set("messages.misc-messages.ev-gain", evGainMessage);
        config.set("messages.misc-messages.ev-increase", evIncreaseMessage);
        config.set("messages.misc-messages.catch", catchMessage);
        config.set("messages.misc-messages.pickup", pickupMessage);
        config.set("messages.misc-messages.egg-hatch", eggHatchMessage);
        config.set("messages.misc-messages.fossil-revival", fossilRevivalMessage);
        config.set("messages.misc-messages.choose-starter", chooseStarterMessage);
        config.set("messages.misc-messages.show-message", showMessage);

        config.set("messages.stats-panel", statsPanelMessage);

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
        config.addDefault("commands.on-first-join", commandsOnJoin);

        config.addDefault("commands.end-battle.enabled", endBattleCommandEnabled);

        config.addDefault("commands.hatch.enabled", hatchCommandEnabled);
        config.addDefault("commands.hatch.cost", hatchCommandCost);

        config.addDefault("commands.reroll.enabled", rerollCommandEnabled);
        config.addDefault("commands.reroll.cooldown", rerollCommandCooldown);

        config.addDefault("commands.show.enabled", showCommandEnabled);

        config.addDefault("commands.stats.enabled", statsCommandEnabled);
        config.addDefault("commands.stats.view-other-players", statsCommandViewOtherPlayers);

        config.addDefault("messages.colors.normal", normalColor);
        config.addDefault("messages.colors.shiny", shinyColor);
        config.addDefault("messages.colors.egg", eggColor);
        config.addDefault("messages.colors.legendary", legendaryColor);
        config.addDefault("messages.colors.ultra-beast", ultraBeastColor);
        config.addDefault("messages.colors.pickup", pickupColor);

        config.addDefault("messages.commands.misc.no-permission", commandNoPermissionMessage);
        config.addDefault("messages.commands.misc.disabled", commandDisabledMessage);
        config.addDefault("messages.commands.misc.usage", commandUsageMessage);

        config.addDefault("messages.commands.end-battle.failed", failedToEndBattleMessage);
        config.addDefault("messages.commands.end-battle.success", battleEndedSuccessfullyMessage);
        config.addDefault("messages.commands.end-battle.not-in-battle", notInBattleMessage);

        config.addDefault("messages.commands.hatch.starter-not-found", starterNotFoundMessage);
        config.addDefault("messages.commands.hatch.invalid-slot", invalidSlotMessage);
        config.addDefault("messages.commands.hatch.bank-account-not-found", bankAccountNotFoundMessage);
        config.addDefault("messages.commands.hatch.not-enough-money", notEnoughMoneyMessage);
        config.addDefault("messages.commands.hatch.pokemon-hatched", pokemonHatchedMessage);
        config.addDefault("messages.commands.hatch.confirm-hatch", confirmHatchMessage);
        config.addDefault("messages.commands.hatch.cancelled", hatchCommandCancelledMessage);

        config.addDefault("messages.commands.reroll.cooldown", rerollCommandCooldownMessage);
        config.addDefault("messages.commands.reroll.vote-submitted", rerollVoteSubmittedMessage);
        config.addDefault("messages.commands.reroll.vote-cancelled", rerollVoteCancelledMessage);
        config.addDefault("messages.commands.reroll.success", rerollHuntMessage);
        config.addDefault("messages.commands.reroll.success-other", rerollHuntMessageWithPlayer);

        config.addDefault("messages.commands.stats.view-others", statsViewOtherPlayersMessage);

        config.addDefault("messages.misc-messages.ev-gain", evGainMessage);
        config.addDefault("messages.misc-messages.ev-increase", evIncreaseMessage);
        config.addDefault("messages.misc-messages.catch", catchMessage);
        config.addDefault("messages.misc-messages.pickup", pickupMessage);
        config.addDefault("messages.misc-messages.egg-hatch", eggHatchMessage);
        config.addDefault("messages.misc-messages.pokemon-receive.custom", pokemonReceiveCustomMessage);
        config.addDefault("messages.misc-messages.pokemon-receive.command", pokemonReceiveCommandMessage);
        config.addDefault("messages.misc-messages.pokemon-receive.select", pokemonReceiveSelectMessage);
        config.addDefault("messages.misc-messages.pokemon-receive.christmas", pokemonReceiveChristmasMessage);
        config.addDefault("messages.misc-messages.fossil-revival", fossilRevivalMessage);
        config.addDefault("messages.misc-messages.choose-starter", chooseStarterMessage);
        config.addDefault("messages.misc-messages.show-message", showMessage);

        config.addDefault("messages.stats-panel", statsPanelMessage);

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
