package com.belmu.quakecraft.Listeners;

import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Quake;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PlayerQuit implements Listener {

    public final Quake plugin;
    public PlayerQuit(Quake plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Map map = plugin.gameMap;

        Player player = e.getPlayer();
        int maxPlayers;

        if(map != null && map.getMaxPlayers(map.getName()) > 0) maxPlayers = map.getMaxPlayers(map.getName());
        else maxPlayers = Bukkit.getMaxPlayers();

        String playerCount = "§8(§7" + Bukkit.getOnlinePlayers().size() + "§8/§d" + maxPlayers + "§8)";
        String playerName;

        if(player.isOp()) playerName = "§8[§c✦§8] §c" + player.getName();
        else playerName = "§7" + player.getName();

        e.setQuitMessage(Quake.prefix + playerName + " §fleft the game " + playerCount);
    }

}
