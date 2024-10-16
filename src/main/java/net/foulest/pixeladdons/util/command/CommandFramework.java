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
package net.foulest.pixeladdons.util.command;

import lombok.Getter;
import lombok.Setter;
import net.foulest.pixeladdons.util.MessageUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 * Command framework for Bukkit, allowing easy registration of commands and command completers.
 * This class is part of the CommandFramework.
 *
 * @author minnymin3
 * @see <a href="https://github.com/mcardy/CommandFramework">CommandFramework GitHub</a>
 */
@Getter
@Setter
public class CommandFramework implements CommandExecutor {

    private final Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<>();
    private final Plugin plugin;
    private CommandMap map;

    /**
     * Constructor for the CommandFramework.
     *
     * @param plugin The plugin associated with this command framework.
     */
    public CommandFramework(@NotNull Plugin plugin) {
        this.plugin = plugin;

        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();

            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Default command handler for commands that are disabled on the server.
     *
     * @param args The CommandArgs object representing the command arguments.
     */
    private static void defaultCommand(@NotNull CommandArgs args) {
        args.getSender().sendMessage(args.getLabel() + " is disabled on this server.");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             org.bukkit.command.@NotNull Command cmd,
                             @NotNull String label,
                             String[] args) {
        handleCommand(sender, cmd, label, args);
        return true;
    }

    /**
     * Handles the execution of a command.
     *
     * @param sender The CommandSender executing the command.
     * @param cmd    The executed command.
     * @param label  The label of the command.
     * @param args   The arguments provided to the command.
     */
    private void handleCommand(CommandSender sender,
                               org.bukkit.command.Command cmd,
                               String label,
                               String @NotNull [] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase(Locale.ROOT));

            for (int x = 0; x < i; x++) {
                buffer.append(".").append(args[x].toLowerCase(Locale.ROOT));
            }

            String cmdLabel = buffer.toString();

            if (commandMap.containsKey(cmdLabel)) {
                Method key = commandMap.get(cmdLabel).getKey();
                Object value = commandMap.get(cmdLabel).getValue();
                Command command = key.getAnnotation(Command.class);

                if (!("").equals(command.permission()) && !sender.hasPermission(command.permission())) {
                    MessageUtil.messagePlayer(sender, command.noPermission());
                    return;
                }

                if (command.inGameOnly() && !(sender instanceof Player)) {
                    MessageUtil.messagePlayer(sender, "&cOnly players may execute this command.");
                    return;
                }

                try {
                    key.invoke(value, new CommandArgs(sender, cmd, label, args,
                            cmdLabel.split("\\.").length - 1));
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }

        defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
    }

    /**
     * Registers commands and completers from an object.
     *
     * @param obj The object containing command and completer methods.
     */
    public void registerCommands(@NotNull Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            if (method.getAnnotation(Command.class) != null) {
                Command command = method.getAnnotation(Command.class);

                if (method.getParameterTypes().length > 1 || method.getParameterTypes()[0] != CommandArgs.class) {
                    MessageUtil.log(Level.WARNING, "&cUnable to register command "
                            + method.getName() + ". Unexpected method arguments"
                    );
                    continue;
                }

                registerCommand(command, command.name(), method, obj);

                for (String alias : command.aliases()) {
                    registerCommand(command, alias, method, obj);
                }

            } else if (method.getAnnotation(Completer.class) != null) {
                Completer completer = method.getAnnotation(Completer.class);

                if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0] != CommandArgs.class) {
                    MessageUtil.log(Level.WARNING, "Unable to register tab completer "
                            + method.getName() + ". Unexpected method arguments"
                    );
                    continue;
                }

                if (method.getReturnType() != List.class) {
                    MessageUtil.log(Level.WARNING, "Unable to register tab completer "
                            + method.getName() + ". Unexpected return type"
                    );
                    continue;
                }

                registerCompleter(completer.name(), method, obj);

                for (String alias : completer.aliases()) {
                    registerCompleter(alias, method, obj);
                }
            }
        }
    }

    /**
     * Registers a command with the framework.
     *
     * @param command The command annotation.
     * @param label   The label of the command.
     * @param method  The method representing the command handler.
     * @param obj     The object containing the command method.
     */
    private void registerCommand(@NotNull Command command,
                                 @NotNull String label,
                                 Method method,
                                 Object obj) {
        commandMap.put(label.toLowerCase(Locale.ROOT), new AbstractMap.SimpleEntry<>(method, obj));
        commandMap.put(plugin.getName() + ':' + label.toLowerCase(Locale.ROOT), new AbstractMap.SimpleEntry<>(method, obj));

        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase(Locale.ROOT);

        if (map.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, this, plugin);
            map.register(plugin.getName(), cmd);
        }

        if (!("").equalsIgnoreCase(command.description()) && cmdLabel.equalsIgnoreCase(label)) {
            Objects.requireNonNull(map.getCommand(cmdLabel)).setDescription(command.description());
        }

        if (!("").equalsIgnoreCase(command.usage()) && cmdLabel.equalsIgnoreCase(label)) {
            Objects.requireNonNull(map.getCommand(cmdLabel)).setUsage(command.usage());
        }
    }

    /**
     * Registers a tab completer with the framework.
     *
     * @param label  The label of the command.
     * @param method The method representing the tab completer.
     * @param obj    The object containing the tab completer method.
     */
    private void registerCompleter(@NotNull String label, Method method, Object obj) {
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase(Locale.ROOT);

        if (map.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command command = new BukkitCommand(cmdLabel, this, plugin);
            map.register(plugin.getName(), command);
        }

        if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
            BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);

            if (command == null) {
                MessageUtil.log(Level.WARNING, "&cUnable to register tab completer: "
                        + method.getName() + ". A command with that name doesn't exist!"
                );
                return;
            }

            if (command.completer == null) {
                command.completer = new BukkitCompleter();
            }

            command.completer.addCompleter(label, method, obj);

        } else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                Object command = map.getCommand(cmdLabel);

                if (command == null) {
                    MessageUtil.log(Level.WARNING, "&cUnable to register tab completer: "
                            + method.getName() + ". A command with that name doesn't exist!"
                    );
                    return;
                }

                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);

                if (field.get(command) == null) {
                    BukkitCompleter completer = new BukkitCompleter();
                    completer.addCompleter(label, method, obj);
                    field.set(command, completer);

                } else if (field.get(command) instanceof BukkitCompleter) {
                    BukkitCompleter completer = (BukkitCompleter) field.get(command);
                    completer.addCompleter(label, method, obj);

                } else {
                    MessageUtil.log(Level.WARNING, "&cUnable to register tab completer: "
                            + method.getName() + ". A tab completer is already registered for that command!"
                    );
                }
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                ex.printStackTrace();
            }
        }
    }
}
