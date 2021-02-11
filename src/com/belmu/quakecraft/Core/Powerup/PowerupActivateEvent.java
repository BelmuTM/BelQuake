package com.belmu.quakecraft.Core.Powerup;

import com.belmu.quakecraft.Quake;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PowerupActivateEvent implements Listener {

    public final Quake plugin;
    public PowerupActivateEvent(Quake plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        PowerupManager pm = new PowerupManager(plugin);
        Player player = e.getPlayer();
        Location playerLoc = player.getLocation();

        if(!pm.powerupTime.containsKey(player.getUniqueId())) {
            if(!pm.powerups.isEmpty()) {

                for(Powerup powerup : pm.powerups) {
                    Location powLoc = powerup.getLocation();

                    if(playerLoc.distance(powLoc) <= 1.05) pm.activatePowerup(powerup, player);
                }
            }
        }
    }

}
