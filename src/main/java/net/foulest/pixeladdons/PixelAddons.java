package net.foulest.pixeladdons;

import lombok.Getter;
import net.foulest.pixeladdons.cmds.*;
import net.foulest.pixeladdons.data.PlayerDataManager;
import net.foulest.pixeladdons.listeners.EventListener;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Foulest
 * @project PixelAddons
 */
@Getter
public class PixelAddons extends JavaPlugin {

    public static PixelAddons instance;
    public final String pluginName = "PixelAddons";
    public CommandFramework framework;

    @Override
    public void onLoad() {
        // Sets the instance.
        instance = this;
    }

    @Override
    public void onEnable() {
        // Initializes the Command Framework.
        Bukkit.getLogger().info("[" + pluginName + "] Initializing Command Framework...");
        framework = new CommandFramework(this);

        // Creates the default settings config.
        Bukkit.getLogger().info("[" + pluginName + "] Loading Settings...");
        Settings.setupSettings();
        Settings.loadSettings();

        // Loads the plugin's listeners.
        Bukkit.getLogger().info("[" + pluginName + "] Loading Listeners...");
        loadListeners(new EventListener());

        // Loads the plugin's commands.
        Bukkit.getLogger().info("[" + pluginName + "] Loading Commands...");
        loadCommands(new HatchCmd(), new PixelAddonsCmd(), new RerollCmd(),
                new StatsCmd(), new ShowCmd(), new EndBattleCmd());

        Bukkit.getLogger().info("[" + pluginName + "] Loaded successfully.");
    }

    @Override
    public void onDisable() {
        // Saves all online players' player data.
        Bukkit.getLogger().info("[" + pluginName + "] Saving Player Data...");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerDataManager.removePlayerData(player);
        }

        // Saves the settings.
        Bukkit.getLogger().info("[" + pluginName + "] Saving Settings...");
        Settings.saveSettings();

        Bukkit.getLogger().info("[" + pluginName + "] Shut down successfully.");
    }

    /**
     * Loads the plugin's listeners.
     *
     * @param listeners Listener to load.
     */
    private void loadListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Loads the plugin's commands.
     *
     * @param commands Command to load.
     */
    private void loadCommands(Object... commands) {
        for (Object command : commands) {
            framework.registerCommands(command);
        }
    }
}
