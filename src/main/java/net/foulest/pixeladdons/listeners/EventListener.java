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
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.enums.ReceiveType;
import com.pixelmonmod.pixelmon.api.events.*;
import com.pixelmonmod.pixelmon.api.events.pokemon.EVsGainedEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.spawning.SpawnLocation;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnActionPokemon;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
            for (@NotNull String line : Settings.commandsOnJoin) {
                if (line.isEmpty()) {
                    break;
                }

                // Replaces %player% with the player's name.
                String playerName = player.getName();
                @NotNull String replace = line.replace("%player%", playerName);

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
     * Cancels block explosion damage for tamed entities.
     *
     * @param event EntityDamageEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onOtherDamageTamedEntity(@NotNull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Tameable)) {
            return;
        }

        Tameable tameable = (Tameable) event.getEntity();

        // Cancels block explosion damage for tamed entities.
        if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            if (tameable.isTamed()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels damage for tamed entities.
     *
     * @param event EntityDamageEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onPlayerDamageTamedEntity(@NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Tameable)) {
            return;
        }

        Tameable tameable = (Tameable) event.getEntity();

        // Cancels damage for tamed entities from their owners.
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (tameable.isTamed() && tameable.getOwner() != null && tameable.getOwner().equals(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles item right-click events.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onRightClickItem(@NotNull PlayerInteractEvent event) {
        // Ignores the event if the player isn't right-clicking.
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        org.bukkit.inventory.ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        Material material = itemStack.getType();

        if (material == null) {
            return;
        }

        @NotNull String materialName = material.name().toLowerCase(Locale.ROOT);

        // Cancels the event if the item is disabled.
        for (String line : Settings.disabledItems) {
            if (materialName.contains(line)) {
                MessageUtil.messagePlayer(player, "&cThis item's functionality has been disabled.");
                event.setCancelled(true);
                player.updateInventory();
                return;
            }
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
            @NotNull EVsGainedEvent eVsGainedEvent = (EVsGainedEvent) forgeEvent;
            EntityPlayerMP ownerPlayer = eVsGainedEvent.pokemon.getOwnerPlayer();

            // Returns if the owner player is null.
            if (ownerPlayer == null) {
                return;
            }

            UUID ownerPlayerUUID = ownerPlayer.getUniqueID();

            // Returns if the owner is null.
            if (Bukkit.getPlayer(ownerPlayerUUID) == null) {
                return;
            }

            Player player = Bukkit.getPlayer(ownerPlayerUUID);

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            EVStore evStore = eVsGainedEvent.evStore;
            int[] oldEVs = evStore.getArray();

            UUID playerUUID = player.getUniqueId();
            PlayerPartyStorage party = Pixelmon.storageManager.getParty(playerUUID);

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Handles EV gain messages.
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID pokemonUUID = eVsGainedEvent.pokemon.getUUID();
                    int partySlot = party.getSlot(pokemonUUID);
                    @Nullable Pokemon pokemon = party.get(partySlot);

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
                    @NotNull List<String> msgList = new ArrayList<>();

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

                    @NotNull StringBuilder totalEVsGained = new StringBuilder();

                    // Formats the message.
                    if (!msgList.isEmpty()) {
                        for (int i = 0; i < msgList.size(); i++) {
                            totalEVsGained.append(msgList.get(i));

                            if (i + 1 < msgList.size()) {
                                totalEVsGained.append(" ");
                            }
                        }

                        String pokemonName = pokemon.getSpecies().getPokemonName();
                        @NotNull String chatMessage = Settings.evGainMessage
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
            @NotNull SpawnEvent spawnEvent = (SpawnEvent) forgeEvent;
            if (!(spawnEvent.action instanceof SpawnActionPokemon)) {
                return;
            }

            // Gets the spawn action.
            @NotNull SpawnActionPokemon spawnAction = (SpawnActionPokemon) spawnEvent.action;
            if (!(spawnAction.spawnInfo instanceof SpawnInfoPokemon)) {
                return;
            }

            // Gets the spawn location.
            SpawnLocation spawnLocation = spawnAction.spawnLocation;
            if (!(spawnLocation.cause instanceof EntityPlayerMP)) {
                return;
            }

            // Gets the player that spawned the Pokemon.
            String playerName = spawnLocation.cause.getName();
            Player player = Bukkit.getPlayer(playerName);
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

            // Sets the custom Pokerus rate for qualifying players.
            if (Settings.customPokerusRateEnabled && player.hasPermission(Settings.customPokerusRatePermission)
                    && new Random().nextInt(Settings.customPokerusRateOdds) == 0) {
                spawnAction.usingSpec.pokerusType = (byte) (new Random().nextInt(5) + 1);
                spawnAction.usingSpec.apply(pixelmon);
            }
        }
    }

    /**
     * Handles economy changes.
     *
     * @param event ForgeEvent
     */
    @SuppressWarnings("UnsecureRandomNumberGeneration")
    @EventHandler
    public static void onEconomyChange(@NotNull ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Returns if the event is null.
        if (forgeEvent == null) {
            return;
        }

        // EconomyEvent.PreTransactionEvent = Before the transaction happens; can cancel; cancelling won't stop
        // the Shopkeeper.Sell event, so the item will still be sold but the player won't receive the money.

        // EconomyEvent.PostTransactionEvent = After the transaction happens; can't cancel; can only cap the new balance.

        // Shopkeeper.Sell = When a player sells an item to a shopkeeper; can cancel; cancelling will stop the
        // transaction, but there's no way to see how much the item costs, so can only cancel if the player has
        // reached the maximum balance.

        // Solution: cap the balance with Post; cancel the Sell event if the balance is capped.

        // Problem: players don't receive full amount if they sell more than the maximum balance, and
        // there's no way of cancelling the transaction if the player hasn't reached the maximum balance.

        // Example: Player has $24,999 and sells an item for $1,000. They only receive $1 because the maximum balance
        // would be reached at $25,000. This creates a problem because the player would expect to receive $1,000, but
        // they only receive $1. The only way to fix this would be to cancel the transaction if the player hasn't
        // reached the maximum balance, but there's no way to see how much the item costs, so the transaction can't be
        // cancelled. The only way to fix this would be to cap the balance with Post, but then the player wouldn't
        // receive the full amount if they sell more than the maximum balance.

        // Caps the transaction amount to the maximum balance.
        if (forgeEvent instanceof EconomyEvent.PostTransactionEvent) {
            EconomyEvent.@NotNull PostTransactionEvent economyEvent = (EconomyEvent.PostTransactionEvent) forgeEvent;
            EntityPlayerMP player = economyEvent.player;

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            UUID uniqueID = player.getUniqueID();

            // Caps the balance to the maximum balance.
            if (economyEvent.newBalance > Settings.maxBalance) {
                if (Pixelmon.moneyManager.getBankAccount(uniqueID).isPresent()) {
                    @NotNull IPixelmonBankAccount bankAccount = Pixelmon.moneyManager.getBankAccount(uniqueID).get();
                    bankAccount.setMoney(Settings.maxBalance);
                    MessageUtil.messagePlayer(Bukkit.getPlayer(uniqueID), "&aYou have reached the maximum balance.");
                }
            }
        }

        // Handles selling items to shopkeepers.
        if (forgeEvent instanceof ShopkeeperEvent.Sell) {
            ShopkeeperEvent.@NotNull Sell sellEvent = (ShopkeeperEvent.Sell) forgeEvent;
            EntityPlayerMP player = sellEvent.getPlayer();

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            UUID uniqueID = player.getUniqueID();

            if (Pixelmon.moneyManager.getBankAccount(uniqueID).isPresent()) {
                @NotNull IPixelmonBankAccount bankAccount = Pixelmon.moneyManager.getBankAccount(uniqueID).get();

                // Cancels transactions that would exceed the maximum balance.
                if (bankAccount.getMoney() >= Settings.maxBalance) {
                    sellEvent.setCanceled(true);
                    MessageUtil.messagePlayer(Bukkit.getPlayer(uniqueID), "&cThis transaction would exceed the maximum balance.");
                }
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
                CaptureEvent.@NotNull SuccessfulCapture captureEvent = (CaptureEvent.SuccessfulCapture) forgeEvent;
                UUID uniqueID = captureEvent.player.getUniqueID();
                player = Bukkit.getPlayer(uniqueID);
                pokemon = captureEvent.getPokemon().getStoragePokemonData();
            } else {
                CaptureEvent.@NotNull SuccessfulRaidCapture captureEvent = (CaptureEvent.SuccessfulRaidCapture) forgeEvent;
                UUID uniqueID = captureEvent.player.getUniqueID();
                player = Bukkit.getPlayer(uniqueID);
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

            pokemonName = pokemon.getSpecies().getPokemonName();
            String playerName = player.getName();

            // Formats the hover message.
            @NotNull String chatMessage = Settings.catchMessage
                    .replace("%player%", playerName)
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
            @NotNull PickupEvent pickupEvent = (PickupEvent) forgeEvent;
            UUID uniqueID = pickupEvent.player.player.getUniqueID();
            Player player = Bukkit.getPlayer(uniqueID);
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
            @NotNull String itemName = itemStack.toString();
            itemName = itemName.replace("1x", "");
            itemName = itemName.replace("@0", "");
            itemName = itemName.replace("item.", "");
            itemName = itemName.replace("_", " ");
            itemName = MessageUtil.capitalize(itemName);

            // Get the correct article for the item name.
            @NotNull String article = "a" + (((!itemName.isEmpty() && itemName.charAt(0) == 'A')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'E')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'I')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'O')
                    || (!itemName.isEmpty() && itemName.charAt(0) == 'U')) ? "n" : "");

            EnumSpecies species = pokemon.getSpecies();
            String pokemonName = species.getPokemonName();

            // Formats the message.
            @NotNull String chatMessage = Settings.pickupMessage
                    .replace("%pokemon%", pokemonName)
                    .replace("%an%", article)
                    .replace("%color%", Settings.pickupColor)
                    .replace("%itemName%", itemName);

            // Prints the message.
            MessageUtil.messagePlayer(player, chatMessage);
        }

        // Handles egg hatch messages.
        if (forgeEvent instanceof EggHatchEvent.Post) {
            EggHatchEvent.@NotNull Post eggHatchEvent = (EggHatchEvent.Post) forgeEvent;
            Pokemon pokemon = eggHatchEvent.getPokemon();
            EntityPlayerMP ownerPlayer = pokemon.getOwnerPlayer();
            UUID ownerPlayerUUID = ownerPlayer.getUniqueID();
            Player player = Bukkit.getPlayer(ownerPlayerUUID);
            String pokemonName = pokemon.getSpecies().getPokemonName();

            // Returns if the player is null.
            if (player == null) {
                return;
            }

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            String playerName = player.getName();

            // Formats the message.
            @NotNull String chatMessage = Settings.eggHatchMessage
                    .replace("%player%", playerName)
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            MessageUtil.printStatsHoverMessage(player, pokemon, chatMessage);
        }

        // Handles Pokemon receive messages.
        if (forgeEvent instanceof PixelmonReceivedEvent) {
            @NotNull PixelmonReceivedEvent receivedEvent = (PixelmonReceivedEvent) forgeEvent;
            UUID uniqueID = receivedEvent.player.getUniqueID();
            Player player = Bukkit.getPlayer(uniqueID);
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

            @NotNull String chatMessage = "";
            String playerName = player.getName();

            // Formats the message.
            switch (receiveType) {
                case Custom:
                    chatMessage = Settings.receivePokemonCustomMessage
                            .replace("%player%", playerName)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Fossil:
                    // Get the correct article for the Pokemon name.
                    @NotNull String article = "a" + (((!pokemonName.isEmpty() && pokemonName.charAt(0) == 'A')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'E')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'I')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'O')
                            || (!pokemonName.isEmpty() && pokemonName.charAt(0) == 'U')) ? "n" : "");

                    chatMessage = Settings.fossilRevivalMessage
                            .replace("%player%", playerName)
                            .replace("%an%", article)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Starter:
                    chatMessage = Settings.chooseStarterMessage
                            .replace("%player%", playerName)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Command:
                    chatMessage = Settings.receivePokemonCommandMessage
                            .replace("%player%", playerName)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case SelectPokemon:
                    chatMessage = Settings.receivePokemonSelectMessage
                            .replace("%player%", playerName)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Christmas:
                    chatMessage = Settings.receivePokemonChristmasMessage
                            .replace("%player%", playerName)
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
