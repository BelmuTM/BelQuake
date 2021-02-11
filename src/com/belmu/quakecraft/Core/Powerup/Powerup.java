package com.belmu.quakecraft.Core.Powerup;

import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Powerup {

    private Map map;
    private Location location;
    private PowerupType powerupType;

    public Powerup(Map map, Location location, PowerupType powerupType, Quake plugin) {
        this.map = map;
        this.location = location;

        this.powerupType = powerupType;

        PowerupManager powerupManager = new PowerupManager(plugin);
        powerupManager.powerups.add(this);
    }

    public Map getMap() { return map; }
    public Location getLocation() { return location; }

    public PowerupType getPowerupType() { return powerupType; }
    public EnumParticle getParticle() { return powerupType.getParticle(); }
    public Material getMaterial() { return powerupType.getMaterial(); }
    public Double getTime() { return powerupType.getTime(); }
}
