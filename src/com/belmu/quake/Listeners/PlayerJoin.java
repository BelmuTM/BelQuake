package com.belmu.quake.Listeners;

import com.belmu.quake.Core.GameOptions;
import com.belmu.quake.Core.GameState;
import com.belmu.quake.Core.Map.Map;
import com.belmu.quake.Core.Packets.Scoreboard.Scoreboard;
import com.belmu.quake.Core.Packets.TabList.TabList;
import com.belmu.quake.Quake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        Scoreboard scoreboard = new Scoreboard(plugin);
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
            if(mainSpawn != null) player.teleport(mainSpawn);

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

}
