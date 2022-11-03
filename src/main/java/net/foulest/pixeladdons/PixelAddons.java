package net.foulest.pixeladdons;

import net.foulest.pixeladdons.cmds.*;
import net.foulest.pixeladdons.listeners.EventListener;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Foulest
 * @project PixelAddons
 */
public class PixelAddons extends JavaPlugin {

    private static PixelAddons instance;
    private CommandFramework framework;

    public static PixelAddons getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        framework = new CommandFramework(this);

        Settings.setupSettings();
        Settings.loadSettings();

        loadListeners(new EventListener());

        loadCommands(new HatchCmd(), new StatsCmd(), new ShowCmd(), new StopBattleCmd());
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
