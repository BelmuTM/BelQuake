package com.belmu.quakecraft.Core.Powerup;

import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
        PowerupManager pm = plugin.powerupManager;
        Player player = e.getPlayer();

        if(!pm.powerupTime.containsKey(player.getUniqueId())) {
            if(!pm.powerups.isEmpty()) {
                Powerup powerup = nearestPowerup(player);

                AxisAlignedBB playerBoundBox = ((CraftPlayer) player).getHandle().getBoundingBox();

                Location low = powerup.getLocation();
                Location high = low.clone().add(0.2D, 0.245D, 0.2D);

                // Creating a new bound box that corresponds to the powerup's one.
                AxisAlignedBB powerupBoundBox = AxisAlignedBB.a(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ());

                /// If both player and powerup bound boxes intersect / collide.
                if(playerBoundBox.b(powerupBoundBox))
                    pm.activatePowerup(powerup, player);
            }
        }
    }

    public Powerup nearestPowerup(Player player) {
        PowerupManager pm = plugin.powerupManager;
        Powerup nearestPowerup = null;

        for(Powerup powerup : pm.powerups) {
            double distance = powerup.getLocation().distanceSquared(player.getLocation());
            float minimum = Float.POSITIVE_INFINITY;

            if(distance < minimum) nearestPowerup = powerup;
        }
        return nearestPowerup;
    }

}
