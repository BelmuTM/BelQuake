package com.belmu.quakecraft.Core.Railgun.Dash;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Dash {

    static double force = 1.85;
    static double velocityY = 0.08;

    public static void dashPlayer(Player player) {

        // Pushes the player forward using a vector.
        Location location = player.getLocation();
        Vector vec = location.getDirection().multiply(force).setY(velocityY);

        player.setVelocity(vec);

        // Dash sound
        for(Player online : Bukkit.getOnlinePlayers())
            online.playSound(location, Sound.BAT_TAKEOFF, 1.5f, 0.5f);
    }
}
