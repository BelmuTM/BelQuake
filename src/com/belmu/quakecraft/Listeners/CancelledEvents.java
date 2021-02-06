package com.belmu.quakecraft.Listeners;

import com.belmu.quakecraft.Core.GameState;
import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class CancelledEvents implements Listener {

    public final Quake plugin;
    public CancelledEvents(Quake plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Map map = plugin.gameMap;
        GameState game = plugin.gameState;

        if(map != null) {
            if(game != null) {

                if (map.isFull())
                    e.disallow(PlayerLoginEvent.Result.KICK_FULL, "§7§m                                " + "\n§dGame is full. §bSorry!\n" + "§7§m                                ");
                if (game.running)
                    e.disallow(PlayerLoginEvent.Result.KICK_FULL, "§7§m                                " + "\n§dGame has started. §bSorry!\n" + "§7§m                                ");
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();

        if(rain) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event) {
        boolean storm = event.toThunderState();

        if(storm) event.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        if(!e.getPlayer().isOp()) e.setCancelled(true);
    }

    private String[] cmds = {
            "?",
            "help",
            "version",
            "icanhasbukkit",
            "about",
            "ver",
            "tell",
            "me",
            "tps",
    };

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if(cmdCancelled(player, e.getMessage())) {
            e.setCancelled(true);
            player.sendMessage(Quake.prefix + "§cYou must be operator to do that!");

        } else if (!cmdCancelled(player, e.getMessage())) {

            if(e.getMessage().equalsIgnoreCase("/reload") || e.getMessage().equalsIgnoreCase("/rl")) {

                for(Player online : Bukkit.getOnlinePlayers())
                    online.kickPlayer("§7§m                              " + "\n§cServer is reloading...\n" + "§7§m                              ");
            }
        }
    }

    private boolean cmdCancelled(Player player, String message) {

        for(String cmd : cmds) {

            if (message.equalsIgnoreCase("/" + cmd) && !player.isOp()
                    || message.equalsIgnoreCase("/bukkit:" + cmd) && !player.isOp()
                    || message.equalsIgnoreCase("/minecraft:" + cmd) && !player.isOp()) {

                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e ) { if(isValid(e.getPlayer())) e.setCancelled(true); }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e ) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if(isValid(e.getPlayer())) {
            e.setCancelled(true);

            PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0, new BlockPosition(block.getX(), block.getY(), block.getZ()), 0);
            int dimension = ((CraftWorld) player.getWorld()).getHandle().dimension;
            ((CraftServer) player.getServer()).getHandle().sendPacketNearby(block.getX(), block.getY(), block.getZ(), 120, dimension, packet);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if(isValid(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if(isValid(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent e) {
        if(isValid(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(isValid(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        e.setTarget(null);
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if(player.getLocation().getY() <= 4)
            plugin.gameMap.teleportPlayer(player);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        GameState game = plugin.gameState;
        Player player = e.getPlayer();

        if(!player.isSneaking() && game.running) {
            player.sendMessage(Quake.prefix + "§cSneaking is disabled in this mode. Your nickname is still visible.");
        }
    }

    // I DIDN'T MAKE THIS METHOD. (https://www.spigotmc.org/threads/preventing-block-update-stackoverflow.360791/)

    private static int checkCounter = 0;

    private static long lastCancelTime = -1;
    private static Material lastCancelledMaterial;

    @SuppressWarnings("unused")
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (System.currentTimeMillis() < lastCancelTime && lastCancelledMaterial == event.getChangedType()) {
            event.setCancelled(true);

        } else {
            checkCounter++;

            if (checkCounter >= 500) {
                checkCounter = 0;

                int stackTraceLength = Thread.currentThread().getStackTrace().length;

                if (stackTraceLength > 400) {
                    lastCancelTime = System.currentTimeMillis() + 3;
                    lastCancelledMaterial = event.getChangedType();

                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isValid(Player player) {
        return !player.isOp() || player.getGameMode() != GameMode.CREATIVE;
    }

}
