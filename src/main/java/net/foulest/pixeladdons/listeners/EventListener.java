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
package net.foulest.pixeladdons.listeners;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ReceiveType;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.PickupEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonReceivedEvent;
import com.pixelmonmod.pixelmon.api.events.pokemon.EVsGainedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnActionPokemon;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import lombok.NoArgsConstructor;
import net.foulest.pixeladdons.PixelAddons;
import net.foulest.pixeladdons.cmds.RerollCmd;
import net.foulest.pixeladdons.data.PlayerDataManager;
import net.foulest.pixeladdons.util.FormatUtil;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@NoArgsConstructor
public class EventListener implements Listener {

    /**
     * Handles player data loading and first-join commands.
     *
     * @param event PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager.getPlayerData(player);

        // Handles first-join commands.
        if (!player.hasPlayedBefore()) {
            for (String line : Settings.commandsOnJoin) {
                if (line.isEmpty()) {
                    break;
                }

                // Replaces %player% with the player's name.
                String replace = line.replace("%player%", player.getName());

                // Runs the command as console.
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replace);
            }
        }
    }

    /**
     * Handles player data unloading and re-roll voting.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager.removePlayerData(player);

        // Removes the player from the re-roll list if they are on it.
        if (Settings.rerollCommandEnabled) {
            RerollCmd.votingToReroll.remove(player);
            RerollCmd.handleReroll();
        }
    }

    /**
     * Handles EV gain messages.
     *
     * @param event ForgeEvent
     */
    @EventHandler
    public static void onEVGain(@NotNull ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Returns if the event is null.
        if (forgeEvent == null) {
            return;
        }

        // Handles EV gain messages.
        if (forgeEvent instanceof EVsGainedEvent) {
            EVsGainedEvent eVsGainedEvent = (EVsGainedEvent) forgeEvent;

            // Checks if the Pokemon has an owner.
            if (eVsGainedEvent.pokemon.getOwnerPlayer() != null
                    && Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID()) != null) {
                Player player = Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID());

                // Returns if the player is null.
                if (player == null) {
                    return;
                }

                EVStore evStore = eVsGainedEvent.evStore;
                PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
                int[] oldEVs = evStore.getArray();

                // Returns if the player is offline.
                if (!player.isOnline()) {
                    return;
                }

                // Handles EV gain messages.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Pokemon pokemon = party.get(party.getSlot(eVsGainedEvent.pokemon.getUUID()));

                        // Returns if the Pokemon is null.
                        if (pokemon == null) {
                            return;
                        }

                        // Calculates the EV differences.
                        int[] newEVs = pokemon.getEVs().getArray();
                        int hpDiff = newEVs[0] - oldEVs[0];
                        int atkDiff = newEVs[1] - oldEVs[1];
                        int defDiff = newEVs[2] - oldEVs[2];
                        int spaDiff = newEVs[3] - oldEVs[3];
                        int spdDiff = newEVs[4] - oldEVs[4];
                        int speDiff = newEVs[5] - oldEVs[5];
                        List<String> msgList = new ArrayList<>();

                        if (hpDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(hpDiff))
                                    .replace("%stat%", "HP")
                                    .replace("%newEVs%", String.valueOf(newEVs[0])));
                        }

                        if (atkDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(atkDiff))
                                    .replace("%stat%", "Atk")
                                    .replace("%newEVs%", String.valueOf(newEVs[1])));
                        }

                        if (defDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(defDiff))
                                    .replace("%stat%", "Def")
                                    .replace("%newEVs%", String.valueOf(newEVs[2])));
                        }

                        if (spaDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(spaDiff))
                                    .replace("%stat%", "SpA")
                                    .replace("%newEVs%", String.valueOf(newEVs[3])));
                        }

                        if (spdDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(spdDiff))
                                    .replace("%stat%", "SpD")
                                    .replace("%newEVs%", String.valueOf(newEVs[4])));
                        }

                        if (speDiff > 0) {
                            msgList.add(Settings.evIncreaseMessage
                                    .replace("%diff%", String.valueOf(speDiff))
                                    .replace("%stat%", "Spe")
                                    .replace("%newEVs%", String.valueOf(newEVs[5])));
                        }

                        StringBuilder totalEVsGained = new StringBuilder();

                        // Formats the message.
                        if (!msgList.isEmpty()) {
                            for (int i = 0; i < msgList.size(); i++) {
                                totalEVsGained.append(msgList.get(i));

                                if (i + 1 < msgList.size()) {
                                    totalEVsGained.append(" ");
                                }
                            }

                            String pokemonName = pokemon.getSpecies().getPokemonName();
                            String chatMessage = Settings.evGainMessage
                                    .replace("%pokemon%", pokemonName)
                                    .replace("%evGains%", totalEVsGained.toString());

                            // Sends the message.
                            if (player.isOnline()) {
                                MessageUtil.messagePlayer(player, chatMessage);
                            }
                        }
                    }
                }.runTaskLater(PixelAddons.instance, 5L);
            }
        }
    }

    /**
     * Handles modifying custom rates for Pokemon spawns.
     *
     * @param event SpawnEvent
     */
    @SuppressWarnings("UnsecureRandomNumberGeneration")
    @EventHandler
    public static void onCustomRateSpawn(@NotNull ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Returns if the event is null.
        if (forgeEvent == null) {
            return;
        }

        if (forgeEvent instanceof SpawnEvent) {
            // Gets the spawn event.
            SpawnEvent spawnEvent = (SpawnEvent) forgeEvent;
            if (!(spawnEvent.action instanceof SpawnActionPokemon)) {
                return;
            }

            // Gets the spawn action.
            SpawnActionPokemon spawnAction = (SpawnActionPokemon) spawnEvent.action;
            if (!(spawnAction.spawnInfo instanceof SpawnInfoPokemon)) {
                return;
            }

            // Gets the spawn location.
            SpawnLocation spawnLocation = spawnAction.spawnLocation;
            if (!(spawnLocation.cause instanceof EntityPlayerMP)) {
                return;
            }

            // Gets the player that spawned the Pokemon.
            Player player = Bukkit.getPlayer(spawnLocation.cause.getName());
            if (player == null || !player.isOnline()) {
                return;
            }

            // Gets the pixelmon.
            EntityPixelmon pixelmon = spawnAction.getOrCreateEntity();

            // Sets the custom boss rate for qualifying players.
            if (Settings.customBossRateEnabled && player.hasPermission(Settings.customBossRatePermission)
                    && new Random().nextInt(Settings.customBossRateOdds) == 0) {
                spawnAction.usingSpec.boss = (byte) (new Random().nextInt(7) + 1);
                spawnAction.usingSpec.apply(pixelmon);
            }

            // Sets the custom shiny rate for qualifying players.
            if (Settings.customShinyRateEnabled && player.hasPermission(Settings.customShinyRatePermission)
                    && new Random().nextInt(Settings.customShinyRateOdds) == 0) {
                spawnAction.usingSpec.shiny = true;
                spawnAction.usingSpec.apply(pixelmon);
            }

            // Sets the custom pokerus rate for qualifying players.
            if (Settings.customPokerusRateEnabled && player.hasPermission(Settings.customPokerusRatePermission)
                    && new Random().nextInt(Settings.customPokerusRateOdds) == 0) {
                spawnAction.usingSpec.pokerusType = (byte) (new Random().nextInt(5) + 1);
                spawnAction.usingSpec.apply(pixelmon);
            }
        }
    }

    /**
     * Handles Pokemon catch messages.
     *
     * @param event ForgeEvent
     */
    @SuppressWarnings("UnsecureRandomNumberGeneration")
    @EventHandler
    public static void onPokemonCatch(@NotNull ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Returns if the event is null.
        if (forgeEvent == null) {
            return;
        }

        // Checks if the event is either a regular capture or a raid capture.
        if (forgeEvent instanceof CaptureEvent.SuccessfulCapture
                || forgeEvent instanceof CaptureEvent.SuccessfulRaidCapture) {
            Player player;
            Pokemon pokemon;
            String pokemonName;

            // Differentiates the handling based on the event type.
            if (forgeEvent instanceof CaptureEvent.SuccessfulCapture) {
                CaptureEvent.SuccessfulCapture captureEvent = (CaptureEvent.SuccessfulCapture) forgeEvent;
                player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
                pokemon = captureEvent.getPokemon().getStoragePokemonData();
            } else {
                CaptureEvent.SuccessfulRaidCapture captureEvent = (CaptureEvent.SuccessfulRaidCapture) forgeEvent;
                player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
                pokemon = captureEvent.getRaidPokemon();
            }

            // Returns if the player is null or offline.
            if (player == null || !player.isOnline()) {
                return;
            }

            // Sets the hidden ability rate for qualifying players.
            if (Settings.customHiddenAbilityRateEnabled && player.hasPermission(Settings.customHiddenAbilityRatePermission)
                    && new Random().nextInt(Settings.customHiddenAbilityRateOdds) == 0) {
                pokemon.setAbilitySlot(2);
            }

            // Gets the Pokemon's name.
            pokemonName = pokemon.getSpecies().getPokemonName();

            // Formats the hover message.
            String chatMessage = Settings.catchMessage
                    .replace("%player%", player.getName())
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            new BukkitRunnable() {
                @Override
                public void run() {
                    MessageUtil.printStatsHoverMessage(player, pokemon, chatMessage);
                }
            }.runTaskLater(PixelAddons.instance, 10L);
        }

        // Handles Pokemon pickup messages.
        if (forgeEvent instanceof PickupEvent) {
            PickupEvent pickupEvent = (PickupEvent) forgeEvent;
            Player player = Bukkit.getPlayer(pickupEvent.player.player.getUniqueID());
            Pokemon pokemon = pickupEvent.pokemon.pokemon;
            ItemStack itemStack = pickupEvent.stack;

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Formats the item name.
            String itemName = itemStack.toString();
            itemName = itemName.replace("1x", "");
            itemName = itemName.replace("@0", "");
            itemName = itemName.replace("item.", "");
            itemName = itemName.replace("_", " ");
            itemName = MessageUtil.capitalize(itemName);

            // Get the correct article for the item name.
            String article = "a" + (((!itemName.isEmpty() && itemName.charAt(0) == 'A')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'E')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'I')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'O')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'U')) ? "n" : "");

            // Formats the message.
            String chatMessage = Settings.pickupMessage
                    .replace("%pokemon%", pokemon.getSpecies().getPokemonName())
                    .replace("%an%", article)
                    .replace("%color%", Settings.pickupColor)
                    .replace("%itemName%", itemName);

            // Prints the message.
            MessageUtil.messagePlayer(player, chatMessage);
        }

        // Handles egg hatch messages.
        if (forgeEvent instanceof EggHatchEvent.Post) {
            EggHatchEvent.Post eggHatchEvent = (EggHatchEvent.Post) forgeEvent;
            Player player = Bukkit.getPlayer(eggHatchEvent.getPokemon().getOwnerPlayer().getUniqueID());
            Pokemon pokemon = eggHatchEvent.getPokemon();
            String pokemonName = pokemon.getSpecies().getPokemonName();

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Formats the message.
            String chatMessage = Settings.eggHatchMessage
                    .replace("%player%", player.getName())
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            MessageUtil.printStatsHoverMessage(player, pokemon, chatMessage);
        }

        // Handles Pokemon receive messages.
        if (forgeEvent instanceof PixelmonReceivedEvent) {
            PixelmonReceivedEvent receivedEvent = (PixelmonReceivedEvent) forgeEvent;
            Player player = Bukkit.getPlayer(receivedEvent.player.getUniqueID());
            Pokemon pokemon = receivedEvent.pokemon;
            String pokemonName = pokemon.getSpecies().getPokemonName();
            ReceiveType receiveType = receivedEvent.receiveType;

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            String chatMessage = "";

            // Formats the message.
            switch (receiveType) {
                case Custom:
                    chatMessage = Settings.receivePokemonCustomMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Fossil:
                    // Get the correct article for the Pokemon name.
                    String article = "a" + (((!pokemonName.isEmpty() && pokemonName.charAt(0) == 'A')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'E')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'I')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'O')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'U')) ? "n" : "");

                    chatMessage = Settings.fossilRevivalMessage
                            .replace("%player%", player.getName())
                            .replace("%an%", article)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Starter:
                    chatMessage = Settings.chooseStarterMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Command:
                    chatMessage = Settings.receivePokemonCommandMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case SelectPokemon:
                    chatMessage = Settings.receivePokemonSelectMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Christmas:
                    chatMessage = Settings.receivePokemonChristmasMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                default:
                    break;
            }

            // Prints the hover message.
            if (!chatMessage.isEmpty()) {
                MessageUtil.printStatsHoverMessage(player, pokemon, chatMessage);
            }
        }
    }
}
