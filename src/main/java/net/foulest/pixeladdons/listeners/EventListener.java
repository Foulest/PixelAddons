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
import net.foulest.pixeladdons.util.MessageUtil;
import net.foulest.pixeladdons.util.Settings;
import net.foulest.pixeladdons.util.StatsUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Foulest
 * @project PixelAddons
 */
public class EventListener implements Listener {

    public static void printHoverMessage(Player player, Pokemon pokemon, String chatMessage) {
        List<String> statsList = StatsUtil.getStats(player, pokemon);

        for (Player online : Bukkit.getOnlinePlayers()) {
            TextComponent message = new TextComponent(MessageUtil.colorize(chatMessage));
            TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
            TextComponent hoverMessage = new TextComponent(new ComponentBuilder("").create());

            for (String line : statsList) {
                hoverMessage.addExtra(new TextComponent(MessageUtil.colorize(line)));

                if (!statsList.get(statsList.size() - 1).equals(line)) {
                    hoverMessage.addExtra(newLine);
                }
            }

            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{hoverMessage}));
            online.spigot().sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            for (String line : Settings.commandsOnJoin) {
                if (line.equals("")) {
                    break;
                }

                line = line.replace("%player%", player.getName());

                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line);
                } catch (Exception ex) {
                    MessageUtil.log("&cAn error occurred when trying to run command: '" + line + "'");
                }
            }
        }
    }

    /**
     * Disables throwing knife crashing.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity().getName().contains("dungeontactics:throwing_knife")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    /**
     * Disables throwing knife crashing.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType().name().contains("DUNGEONTACTICS_THROWING_KNIFE")) {
            event.setCancelled(true);
        }
    }

    /**
     * Disables throwing knife crashing.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand() == null) {
            return;
        }

        if (event.getItemInHand().getType().name().contains("DUNGEONTACTICS_THROWING_KNIFE")) {
            event.setCancelled(true);
        }
    }

    /**
     * Disables throwing knife crashing.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getName().contains("dungeontactics:throwingknife")) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    /**
     * Disables throwing knife crashing.
     * Removes fade leaf flowers.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        for (Entity entity : chunk.getEntities()) {
            if (entity.getName().contains("dungeontactics:throwingknife")) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onEVGain(ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        if (forgeEvent instanceof EVsGainedEvent) {
            EVsGainedEvent eVsGainedEvent = (EVsGainedEvent) forgeEvent;

            if (eVsGainedEvent.pokemon.getOwnerPlayer() != null
                    && Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID()) != null) {
                Player player = Bukkit.getPlayer(eVsGainedEvent.pokemon.getOwnerPlayer().getUniqueID());
                EVStore evStore = eVsGainedEvent.evStore;
                PlayerPartyStorage party = Pixelmon.storageManager.getParty(player.getUniqueId());
                int[] oldEVs = evStore.getArray();

                if (!player.isOnline()) {
                    return;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Pokemon pokemon = party.get(party.getSlot(eVsGainedEvent.pokemon.getUUID()));

                        if (pokemon == null) {
                            return;
                        }

                        int[] newEVs = pokemon.getEVs().getArray();
                        int hpDiff = newEVs[0] - oldEVs[0];
                        int atkDiff = newEVs[1] - oldEVs[1];
                        int defDiff = newEVs[2] - oldEVs[2];
                        int spaDiff = newEVs[3] - oldEVs[3];
                        int spdDiff = newEVs[4] - oldEVs[4];
                        int speDiff = newEVs[5] - oldEVs[5];
                        List<String> msgList = new ArrayList<>();

                        if (hpDiff > 0) {
                            msgList.add("&a+" + hpDiff + " &aHP &7(" + newEVs[0] + ")");
                        }

                        if (atkDiff > 0) {
                            msgList.add("&a+" + atkDiff + " Atk &7(" + newEVs[1] + ")");
                        }

                        if (defDiff > 0) {
                            msgList.add("&a+" + defDiff + " Def &7(" + newEVs[2] + ")");
                        }

                        if (spaDiff > 0) {
                            msgList.add("&a+" + spaDiff + " SpA &7(" + newEVs[3] + ")");
                        }

                        if (spdDiff > 0) {
                            msgList.add("&a+" + spdDiff + " SpD &7(" + newEVs[4] + ")");
                        }

                        if (speDiff > 0) {
                            msgList.add("&a+" + speDiff + " Spe &7(" + newEVs[5] + ")");
                        }

                        StringBuilder temp = new StringBuilder();

                        if (!msgList.isEmpty()) {
                            for (int i = 0; i < msgList.size(); i++) {
                                if (msgList.size() > 1 && i + 1 < msgList.size()) {
                                    temp.append(msgList.get(i)).append(" ");
                                } else {
                                    temp.append(msgList.get(i));
                                }
                            }

                            if (player.isOnline()) {
                                MessageUtil.messagePlayer(player, "&fYour &a" + pokemon.getSpecies().getPokemonName()
                                        + " &fgained " + temp + " &fEVs!");
                            }
                        }
                    }
                }.runTaskLater(PixelAddons.getInstance(), 5L);
            }
        }
    }

    @EventHandler
    public void onPokemonCatch(ForgeEvent event) {
        Event forgeEvent = event.getForgeEvent();

        if (forgeEvent instanceof CaptureEvent.SuccessfulCapture) {
            CaptureEvent.SuccessfulCapture captureEvent = (CaptureEvent.SuccessfulCapture) forgeEvent;
            Player player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
            Pokemon pokemon = captureEvent.getPokemon().getStoragePokemonData();

            if (!player.isOnline()) {
                return;
            }

            StringBuilder chatMessage = new StringBuilder("&r" + player.getDisplayName() + " caught a wild ");

            if (pokemon.isShiny()) {
                chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
            } else if (pokemon.isLegendary()) {
                chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
            } else {
                chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    printHoverMessage(player, pokemon, chatMessage.toString());
                }
            }.runTaskLater(PixelAddons.getInstance(), 10L);
        }

        if (forgeEvent instanceof CaptureEvent.SuccessfulRaidCapture) {
            try {
                CaptureEvent.SuccessfulRaidCapture captureEvent = (CaptureEvent.SuccessfulRaidCapture) forgeEvent;
                Player player = Bukkit.getPlayer(captureEvent.player.getUniqueID());
                Pokemon pokemon = captureEvent.getRaidPokemon();

                if (!player.isOnline()) {
                    return;
                }

                StringBuilder chatMessage = new StringBuilder("&r" + player.getDisplayName() + " caught a wild ");

                if (pokemon.isShiny()) {
                    chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                } else if (pokemon.isLegendary()) {
                    chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                } else {
                    chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        printHoverMessage(player, pokemon, chatMessage.toString());
                    }
                }.runTaskLater(PixelAddons.getInstance(), 10L);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (forgeEvent instanceof PickupEvent) {
            PickupEvent pickupEvent = (PickupEvent) forgeEvent;
            Player player = Bukkit.getPlayer(pickupEvent.player.player.getUniqueID());
            Pokemon pokemon = pickupEvent.pokemon.pokemon;
            ItemStack itemStack = pickupEvent.stack;

            if (!player.isOnline()) {
                return;
            }

            String itemName = itemStack.toString();
            itemName = itemName.replace("1x", "");
            itemName = itemName.replace("@0", "");
            itemName = itemName.replace("item.", "");
            itemName = itemName.replace("_", " ");
            itemName = WordUtils.capitalize(itemName);

            MessageUtil.messagePlayer(player, "&rYour &e" + pokemon.getSpecies().getPokemonName()
                    + " &fpicked up a &e[" + itemName + "&e]");
        }

        if (forgeEvent instanceof EggHatchEvent.Post) {
            EggHatchEvent.Post eggHatchEvent = (EggHatchEvent.Post) forgeEvent;
            Player player = Bukkit.getPlayer(eggHatchEvent.getPokemon().getOwnerPlayer().getUniqueID());
            Pokemon pokemon = eggHatchEvent.getPokemon();

            if (!player.isOnline()) {
                return;
            }

            StringBuilder chatMessage = new StringBuilder("&r" + player.getDisplayName() + "'s egg hatched into ");

            if (pokemon.isShiny()) {
                chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
            } else if (pokemon.isLegendary()) {
                chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
            } else {
                chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
            }

            printHoverMessage(player, pokemon, chatMessage.toString());
        }

        if (forgeEvent instanceof PixelmonReceivedEvent) {
            PixelmonReceivedEvent receivedEvent = (PixelmonReceivedEvent) forgeEvent;
            Player player = Bukkit.getPlayer(receivedEvent.player.getUniqueID());
            Pokemon pokemon = receivedEvent.pokemon;
            ReceiveType receiveType = receivedEvent.receiveType;

            if (!player.isOnline()) {
                return;
            }

            StringBuilder chatMessage = new StringBuilder();

            switch (receiveType) {
                case Custom:
                    chatMessage = new StringBuilder("&7(Custom) &r" + player.getDisplayName() + " received a wild ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }
                    break;

                case Trade:
                case Evolution:
                case PokeBall:
                    break;

                case Fossil:
                    chatMessage = new StringBuilder("&r" + player.getDisplayName() + " revived a ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }

                    chatMessage.append(" &rfrom a fossil!");
                    break;

                case Starter:
                    chatMessage = new StringBuilder("&r" + player.getDisplayName() + " chose ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }

                    chatMessage.append(" &ras their starter!");
                    break;

                case Command:
                    chatMessage = new StringBuilder("&7(Command) &r" + player.getDisplayName() + " received a wild ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }
                    break;

                case SelectPokemon:
                    chatMessage = new StringBuilder("&7(SelectPokemon) &r" + player.getDisplayName() + " received a wild ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }
                    break;

                case Christmas:
                    chatMessage = new StringBuilder("&r" + player.getDisplayName() + " received a wild ");

                    if (pokemon.isShiny()) {
                        chatMessage.append("&6[").append(pokemon.getDisplayName()).append("]");
                    } else if (pokemon.isLegendary()) {
                        chatMessage.append("&d[").append(pokemon.getDisplayName()).append("]");
                    } else {
                        chatMessage.append("&a[").append(pokemon.getDisplayName()).append("]");
                    }

                    chatMessage.append(" &rfor christmas!");
                    break;
            }

            if (!chatMessage.toString().equals("")) {
                printHoverMessage(player, pokemon, chatMessage.toString());
            }
        }
    }
}
