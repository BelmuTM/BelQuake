package com.belmu.quake.Listeners;

import com.belmu.quake.Quake;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String playerCount = "§8(§7" + (Bukkit.getOnlinePlayers().size() - 1) + "§8/§d" + Bukkit.getMaxPlayers() + "§8)";

        String playerName;

        if(player.isOp()) playerName = "§8[§c✦§8] §c" + player.getName();
        else playerName = "§7" + player.getName();

        e.setQuitMessage(Quake.prefix + playerName + " §fleft the game " + playerCount);
    }

}
