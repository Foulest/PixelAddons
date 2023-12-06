package net.foulest.pixeladdons.listeners;

import catserver.api.bukkit.event.ForgeEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.enums.ReceiveType;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.events.PickupEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonReceivedEvent;
import com.pixelmonmod.pixelmon.api.events.pokemon.EVsGainedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.foulest.pixeladdons.PixelAddons;
import net.foulest.pixeladdons.cmds.RerollCmd;
import net.foulest.pixeladdons.data.PlayerDataManager;
import net.foulest.pixeladdons.util.FormatUtil;
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static net.foulest.pixeladdons.util.MessageUtil.printStatsHoverMessage;
import static net.foulest.pixeladdons.util.Settings.*;

/**
 * @author Foulest
 * @project PixelAddons
 */
public class EventListener implements Listener {

    /**
     * Handles player data loading and first-join commands.
     *
     * @param event PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager.getPlayerData(player);

        // Handles first-join commands.
        if (!player.hasPlayedBefore()) {
            for (String line : commandsOnJoin) {
                if (line.isEmpty()) {
                    break;
                }

                // Replaces %player% with the player's name.
                line = line.replace("%player%", player.getName());

                // Runs the command as console.
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line);
            }
        }
    }

    /**
     * Handles player data unloading and re-roll voting.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataManager.removePlayerData(player);

        // Removes the player from the re-roll list if they are on it.
        if (rerollCommandEnabled) {
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
    public void onEVGain(ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Handles EV gain messages.
        if (forgeEvent instanceof EVsGainedEvent) {
            EVsGainedEvent eVsGainedEvent = (EVsGainedEvent) forgeEvent;

            // Checks if the Pokemon has an owner.
            if (eVsGainedEvent.pokemon.getOwnerPlayer() != null
                    && Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID()) != null) {
                Player player = Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID());
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
                            msgList.add(evIncreaseMessage
                                    .replace("%diff%", String.valueOf(hpDiff))
                                    .replace("%stat%", "HP")
                                    .replace("%newEVs%", String.valueOf(newEVs[0])));
                        }

                        if (atkDiff > 0) {
                            msgList.add(evIncreaseMessage
                                    .replace("%diff%", String.valueOf(atkDiff))
                                    .replace("%stat%", "Atk")
                                    .replace("%newEVs%", String.valueOf(newEVs[1])));
                        }

                        if (defDiff > 0) {
                            msgList.add(evIncreaseMessage
                                    .replace("%diff%", String.valueOf(defDiff))
                                    .replace("%stat%", "Def")
                                    .replace("%newEVs%", String.valueOf(newEVs[2])));
                        }

                        if (spaDiff > 0) {
                            msgList.add(evIncreaseMessage
                                    .replace("%diff%", String.valueOf(spaDiff))
                                    .replace("%stat%", "SpA")
                                    .replace("%newEVs%", String.valueOf(newEVs[3])));
                        }

                        if (spdDiff > 0) {
                            msgList.add(evIncreaseMessage
                                    .replace("%diff%", String.valueOf(spdDiff))
                                    .replace("%stat%", "SpD")
                                    .replace("%newEVs%", String.valueOf(newEVs[4])));
                        }

                        if (speDiff > 0) {
                            msgList.add(evIncreaseMessage
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
                            String chatMessage = evGainMessage
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
     * Handles Pokemon catch messages.
     *
     * @param event ForgeEvent
     */
    @EventHandler
    public void onPokemonCatch(ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        // Handles Pokemon catch messages.
        if (forgeEvent instanceof CaptureEvent.SuccessfulCapture) {
            CaptureEvent.SuccessfulCapture captureEvent = (CaptureEvent.SuccessfulCapture) forgeEvent;
            Player player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
            Pokemon pokemon = captureEvent.getPokemon().getStoragePokemonData();
            String pokemonName = pokemon.getSpecies().getPokemonName();

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Formats the message.
            String chatMessage = catchMessage
                    .replace("%player%", player.getName())
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            new BukkitRunnable() {
                @Override
                public void run() {
                    printStatsHoverMessage(player, pokemon, chatMessage);
                }
            }.runTaskLater(PixelAddons.instance, 10L);
        }

        // Handles raid Pokemon catch messages.
        if (forgeEvent instanceof CaptureEvent.SuccessfulRaidCapture) {
            CaptureEvent.SuccessfulRaidCapture captureEvent = (CaptureEvent.SuccessfulRaidCapture) forgeEvent;
            Player player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
            Pokemon pokemon = captureEvent.getRaidPokemon();
            String pokemonName = pokemon.getSpecies().getPokemonName();

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Formats the message.
            String chatMessage = catchMessage
                    .replace("%player%", player.getName())
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            new BukkitRunnable() {
                @Override
                public void run() {
                    printStatsHoverMessage(player, pokemon, chatMessage);
                }
            }.runTaskLater(PixelAddons.instance, 10L);
        }

        // Handles Pokemon pickup messages.
        if (forgeEvent instanceof PickupEvent) {
            PickupEvent pickupEvent = (PickupEvent) forgeEvent;
            Player player = Bukkit.getPlayer(pickupEvent.player.player.getUniqueID());
            Pokemon pokemon = pickupEvent.pokemon.pokemon;
            ItemStack itemStack = pickupEvent.stack;

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
            itemName = WordUtils.capitalize(itemName);

            // Get the correct article for the item name.
            String article = "a" + ((itemName.startsWith("A") || itemName.startsWith("E") || itemName.startsWith("I")
                    || itemName.startsWith("O") || itemName.startsWith("U")) ? "n" : "");

            // Formats the message.
            String chatMessage = pickupMessage
                    .replace("%pokemon%", pokemon.getSpecies().getPokemonName())
                    .replace("%an%", article)
                    .replace("%color%", pickupColor)
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

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            // Formats the message.
            String chatMessage = eggHatchMessage
                    .replace("%player%", player.getName())
                    .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                    .replace("%pokemon%", pokemonName);

            // Prints the hover message.
            printStatsHoverMessage(player, pokemon, chatMessage);
        }

        // Handles Pokemon receive messages.
        if (forgeEvent instanceof PixelmonReceivedEvent) {
            PixelmonReceivedEvent receivedEvent = (PixelmonReceivedEvent) forgeEvent;
            Player player = Bukkit.getPlayer(receivedEvent.player.getUniqueID());
            Pokemon pokemon = receivedEvent.pokemon;
            String pokemonName = pokemon.getSpecies().getPokemonName();
            ReceiveType receiveType = receivedEvent.receiveType;

            // Returns if the player is offline.
            if (!player.isOnline()) {
                return;
            }

            String chatMessage = "";

            // Formats the message.
            switch (receiveType) {
                case Custom:
                    chatMessage = pokemonReceiveCustomMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Fossil:
                    // Get the correct article for the Pokemon name.
                    String article = "a" + ((pokemonName.startsWith("A") || pokemonName.startsWith("E")
                            || pokemonName.startsWith("I") || pokemonName.startsWith("O")
                            || pokemonName.startsWith("U")) ? "n" : "");

                    chatMessage = fossilRevivalMessage
                            .replace("%player%", player.getName())
                            .replace("%an%", article)
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Starter:
                    chatMessage = chooseStarterMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Command:
                    chatMessage = pokemonReceiveCommandMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case SelectPokemon:
                    chatMessage = pokemonReceiveSelectMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                case Christmas:
                    chatMessage = pokemonReceiveChristmasMessage
                            .replace("%player%", player.getName())
                            .replace("%color%", FormatUtil.getDisplayColor(pokemon))
                            .replace("%pokemon%", pokemonName);
                    break;

                default:
                    break;
            }

            // Prints the hover message.
            if (!chatMessage.isEmpty()) {
                printStatsHoverMessage(player, pokemon, chatMessage);
            }
        }
    }
}
