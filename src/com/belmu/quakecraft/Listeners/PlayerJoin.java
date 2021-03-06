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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

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

        GameScoreboard scoreboard = new GameScoreboard(plugin);
        TabList tabList = new TabList(plugin);

        Player player = e.getPlayer();
        int maxPlayers;

        if(map != null && map.getMaxPlayers(map.getName()) > 0) maxPlayers = map.getMaxPlayers(map.getName());
        else maxPlayers = Bukkit.getMaxPlayers();

        String playerCount = "§8(§7" + Bukkit.getOnlinePlayers().size() + "§8/§d" + maxPlayers + "§8)";
        String playerName;

        LinkedHashMap<UUID, Integer> sortedKills = plugin.statsConfig.sortedKills();
        Entry<UUID, Integer> entry = sortedKills.entrySet().iterator().next();

        if(player.isOp()) playerName = "§8[§c✦§8] §c" + player.getName();
        else if(!sortedKills.isEmpty() && entry.getKey() == player.getUniqueId()) playerName = "§7[§6§l#1§r§7] §6" + player.getName();
        else playerName = "§7" + player.getName();

        player.setPlayerListName(playerName);
        player.setDisplayName("§f" + player.getName());
        e.setJoinMessage(Quake.prefix + playerName + " §fjoined the game " + playerCount);

        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.SPEED);
        if(player.getGameMode() != GameMode.ADVENTURE && !player.isOp()) player.setGameMode(GameMode.ADVENTURE);

        /**
         * Teleports player to main spawn on join.
         * Then checks if it can start a new game.
         */
        if(plugin.gameState == null) plugin.gameState = new GameState(plugin);

        if(map != null) {

            Location mainSpawn = map.getMainSpawn(map.getName());
            if(mainSpawn != null) {
                player.teleport(mainSpawn);

                setMapTime(map);
            }
            checkGame(map, plugin.gameState);
        }

        /**
         * Constantly sending packets to the player who joined.
         * Refreshing both the tablist and the scoreboard.
         */
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard teamScoreboard = scoreboardManager.getMainScoreboard();

        tabList.execute(player);
        scoreboard.createScoreboard(player, "§d§lQuakecraft", teamScoreboard);

        new BukkitRunnable() {

            @Override
            public void run() {
                scoreboard.update(player);
            }
        }.runTaskTimer(plugin, 17, 17);
        scoreboard.addToTeams(player, player.getName());
    }

    public void checkGame(Map map, GameState game) {
        boolean enough = map.isEnough();

        if(plugin.gameMap != null) {
            /**
             * @Important starting the game state if the map has enough players.
             */
            if(enough) {
                if(!game.running && !game.starting)
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
