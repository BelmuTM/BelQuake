package com.belmu.quakecraft.Listeners;

import com.belmu.quakecraft.Core.GameOptions;
import com.belmu.quakecraft.Core.GameState;
import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Core.Packets.Scoreboard.GameScoreboard;
import com.belmu.quakecraft.Core.Packets.TabList.TabList;
import com.belmu.quakecraft.Quake;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PlayerJoin implements Listener {

    public final Quake plugin;
    public PlayerJoin(Quake plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Map map = plugin.gameMap;
        GameState game = plugin.gameState;

        GameScoreboard scoreboard = new GameScoreboard(plugin);
        TabList tabList = new TabList(plugin);

        Player player = e.getPlayer();
        String playerCount = "§8(§7" + Bukkit.getOnlinePlayers().size() + "§8/§d" + Bukkit.getMaxPlayers() + "§8)";

        String playerName;

        if(player.isOp()) playerName = "§8[§c✦§8] §c" + player.getName();
        else playerName = "§7" + player.getName();

        player.setPlayerListName(playerName);
        player.setDisplayName("§f" + player.getName());
        e.setJoinMessage(Quake.prefix + playerName + " §fjoined the game " + playerCount);

        player.getInventory().clear();
        if(player.hasPotionEffect(PotionEffectType.SPEED)) player.removePotionEffect(PotionEffectType.SPEED);
        if(player.getGameMode() != GameMode.ADVENTURE && !player.isOp()) player.setGameMode(GameMode.ADVENTURE);

        /**
         * Constantly sending packets to the player who joined.
         * Refreshing both the tablist and the scoreboard.
         */
        tabList.execute(player);
        scoreboard.createScoreboard(player, "§d§lQuakecraft");

        new BukkitRunnable() {

            @Override
            public void run() {
                scoreboard.update(player);
            }
        }.runTaskTimer(plugin, 17, 17);

        /**
         * Teleports player to main spawn on join.
         * Then checks if it can start a new game.
         */

        if(map != null) {

            Location mainSpawn = map.getMainSpawn(map.getName());
            if(mainSpawn != null) {
                player.teleport(mainSpawn);

                setMapTime(map);
            }
            checkGame(map, game);
        }
    }

    public void checkGame(Map map, GameState game) {
        boolean enough = map.isEnough();

        if(plugin.gameMap != null) {
            /**
             * @Important starting the game state if the map has enough players.
             */
            if(enough) {
                if(!game.running && !game.isStarting)
                    game.start(GameOptions.railgun, GameOptions.timeBeforeStart);
            }
        }
    }

    public void setMapTime(Map map) {

        /**
         * Sets the world time depending on the random "night" or "day" value picked before.
         */
        World mapWorld = map.getMainSpawn(map.getName()).getWorld();
        long day = 6000L;
        long night = 18000L;

        if(plugin.mapTime == 1 && mapWorld.getTime() != day)
            mapWorld.setTime(day);
        else
        if(mapWorld.getTime() != night) mapWorld.setTime(night);
    }

}
