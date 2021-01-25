package com.belmu.quake.Core.Map;

import com.belmu.quake.Quake;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class SpawnPoint {

    private static String path = "Maps.";

    private Map map;
    private String name;
    private Location location;

    public final Quake plugin;
    public SpawnPoint(Map map, String name, Location location, Quake plugin) {
        this.map = map;
        this.name = name;
        this.location = location;
        this.plugin = plugin;

        addSpawnPoint(name, location);
    }

    public void addSpawnPoint(String name, Location location) {
        FileConfiguration cfg = plugin.mapManager.getConfig();

        World world = location.getWorld();
        String spawnPointPath = path + map.getName() + ".spawn_points." + name + ".";

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        float yaw = location.getYaw();
        float pitch = location.getPitch();

        cfg.set(spawnPointPath + "world", world.getName());

        cfg.set(spawnPointPath + "x", x);
        cfg.set(spawnPointPath + "y", y);
        cfg.set(spawnPointPath + "z", z);

        cfg.set(spawnPointPath + "yaw", yaw);
        cfg.set(spawnPointPath + "pitch", pitch);
        plugin.mapManager.saveConfig();
    }

    public void removeSpawnPoint() {
        FileConfiguration cfg = plugin.mapManager.getConfig();
        String spawnPointPath = path + map.getName() + ".spawn_points." + name;

        cfg.set(spawnPointPath, null);
        plugin.mapManager.saveConfig();

        map = null;
        name = null;
        location = null;
    }

    public Map getMap() { return map; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
}
