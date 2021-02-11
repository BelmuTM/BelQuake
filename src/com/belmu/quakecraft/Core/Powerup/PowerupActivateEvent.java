package com.belmu.quakecraft.Core.Powerup;

import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
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
        PowerupManager pm = new PowerupManager(plugin);

        Player player = e.getPlayer();
        AxisAlignedBB playerBoundingBox = ((CraftPlayer) player).getHandle().getBoundingBox();

        if(!pm.powerupTime.containsKey(player.getUniqueId())) {

            for(Powerup powerup : pm.powerups) {

                Location low = powerup.getLocation().getBlock().getLocation();
                Location high = low.clone().add(0.2D, 2.45D, 0.2D);

                AxisAlignedBB powerupBoundingBox = AxisAlignedBB.a(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ());

                // Checks if the player's bounding box and the powerup's one intersect.
                if(playerBoundingBox.b(powerupBoundingBox)) pm.activatePowerup(powerup, player);
            }
        }
    }

    public Powerup nearestPowerup(Player player) {
        PowerupManager pm = new PowerupManager(plugin);
        Powerup nearestPowerup = null;

        if(!pm.powerups.isEmpty()) {
            for(Powerup powerup : pm.powerups) {
                double distance = powerup.getLocation().distance(player.getLocation());
                float minimum = Float.POSITIVE_INFINITY;

                if(distance < minimum) nearestPowerup = powerup;
            }
        }
        return nearestPowerup;
    }

}
