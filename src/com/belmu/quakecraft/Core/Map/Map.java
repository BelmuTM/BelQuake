package com.belmu.quakecraft.Core.Map;

import com.belmu.quakecraft.Core.GameOptions;
import com.belmu.quakecraft.Quake;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Map {

    private String path = "Maps.";
    private String name;

    public final Quake plugin;
    public final FileConfiguration cfg;

    public Map(String mapName, Quake plugin) {
        this.name = mapName;
        this.plugin = plugin;

        cfg = plugin.mapManager.getConfig();

        createMap();
    }

    public void createMap() {
        cfg.set(path + name + ".saved", true);
        plugin.mapManager.saveConfig();
    }

    public void removeMap() {
        cfg.set(path + name, null);
        plugin.mapManager.saveConfig();
    }

    /**
     *
     * @param minPlayers Players required to start the game on this map. (0 = No Minimum)
     */
    public void setMinPlayers(int minPlayers) {
        cfg.set(path + name + ".min_players", minPlayers);
        plugin.mapManager.saveConfig();
    }

    /**
     *
     * @param maxPlayers Maximum amount of players who can play on this map. (0 = No Maximum)
     */
    public void setMaxPlayers(int maxPlayers) {
        cfg.set(path + name + ".max_players", maxPlayers);
        plugin.mapManager.saveConfig();
    }

    /**
     *
      * @param mainSpawn Where players will spawn during the waiting time.
     */
    public void setMainSpawn(Location mainSpawn) {
        String mainSpawnPath = path + name + ".main_spawn.";

        World world = mainSpawn.getWorld();
        double x = mainSpawn.getX();
        double y = mainSpawn.getY();
        double z = mainSpawn.getZ();

        float yaw = mainSpawn.getYaw();
        float pitch = mainSpawn.getPitch();

        cfg.set(mainSpawnPath + "world", world.getName());

        cfg.set(mainSpawnPath + "x", x);
        cfg.set(mainSpawnPath + "y", y);
        cfg.set(mainSpawnPath + "z", z);

        cfg.set(mainSpawnPath + "yaw", yaw);
        cfg.set(mainSpawnPath + "pitch", pitch);
        plugin.mapManager.saveConfig();
    }

    public String getName() { return name; }

    public int getMinPlayers(String mapName) {
        return cfg.getInt(path + mapName + "." + "min_players");
    }

    public int getMaxPlayers(String mapName) {
        return cfg.getInt(path + mapName + "." + "max_players");
    }
    public Location getMainSpawn(String mapName) {
        String spawnPath = path + mapName + "." + "main_spawn";

        if(cfg.get(spawnPath) != null) {
            World world = Bukkit.getWorld(cfg.get(spawnPath + ".world").toString());

            double x = cfg.getDouble(spawnPath + ".x");
            double y = cfg.getDouble(spawnPath + ".y");
            double z = cfg.getDouble(spawnPath + ".z");

            float yaw = ((Double) cfg.getDouble(spawnPath + ".yaw")).floatValue();
            float pitch = ((Double) cfg.getDouble(spawnPath + ".pitch")).floatValue();

            Location location = new Location(world, x, y, z);
            location.setYaw(yaw);
            location.setPitch(pitch);

            return location;
        }
        return null;
    }

    public ArrayList<SpawnPoint> getSpawnPoints() {
        ArrayList<SpawnPoint> spawnPoints = new ArrayList<>();

        String spawnPointsPath = path + name + ".spawn_points";
        ConfigurationSection cs = cfg.getConfigurationSection(spawnPointsPath);

        if(cs != null) {
            if (cs.getKeys(false) != null) {

                for (String spawnPoint : cs.getKeys(false)) {
                    String subPath = spawnPointsPath + "." + spawnPoint;

                    World world = Bukkit.getWorld(cfg.get(subPath + ".world").toString());

                    double x = cfg.getDouble(subPath + ".x");
                    double y = cfg.getDouble(subPath + ".y");
                    double z = cfg.getDouble(subPath + ".z");

                    float yaw = ((Double) cfg.getDouble(subPath + ".yaw")).floatValue();
                    float pitch = ((Double) cfg.getDouble(subPath + ".pitch")).floatValue();

                    Location location = new Location(world, x, y, z);
                    location.setYaw(yaw);
                    location.setPitch(pitch);

                    spawnPoints.add(new SpawnPoint(this, spawnPoint, location, plugin));
                }
            }
        }
        return spawnPoints;
    }

    public boolean isEnough() {
        if(getMinPlayers(name) == 0) return true;
        return Bukkit.getOnlinePlayers().size() >= getMinPlayers(name);
    }

    public boolean isFull() {
        if(getMaxPlayers(name) == 0) return false;
        return Bukkit.getOnlinePlayers().size() >= getMaxPlayers(name);
    }

    /**
     * Teleporting the player to spawnpoint with the least
     * amount of players around.
     */

    public void teleportPlayer(Player player) {
        if(getSpawnPoints().isEmpty()) return;

        player.teleport(randomSpawnPoint());

        SpawnProtection sp = new SpawnProtection(plugin);
        sp.invulnerability(player, GameOptions.invulnerabilityTime);
    }

    static int searchQuery = 6;

    public Location randomSpawnPoint() {

        // Gets all spawn points of a map.
        for(SpawnPoint spawnPoint : getSpawnPoints()) {

            Location location = spawnPoint.getLocation();
            World world = location.getWorld();

            Collection<Entity> nearEntities = world.getNearbyEntities(location, searchQuery, searchQuery, searchQuery);

            // Sets the minimum value to infinity, in order to slowly decrease during the loop.
            float minimum = Float.POSITIVE_INFINITY;
            int players = 0;
            if(!nearEntities.isEmpty()) {
                for (Entity player : nearEntities) {

                    // If the nearby entity is a player, adds 1 to the players amount.
                    if (player instanceof Player)
                        players += 1;
                }
            }

            // If the players amount is below the minimum, then set it to the minimum.
            // Then returns the spawn point.
            if(players == 0) return getSpawnPoints().get(new Random().nextInt(getSpawnPoints().size())).getLocation();

            if(players < minimum) {
                minimum = players;
                return location;
            }
        }
        return null;
    }

}
