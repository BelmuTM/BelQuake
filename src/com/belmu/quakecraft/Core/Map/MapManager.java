package com.belmu.quakecraft.Core.Map;

import com.belmu.quakecraft.Quake;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class MapManager {

    private static String file_name = "maps.yml";

    public final Quake plugin;
    public MapManager(Quake plugin) {
        this.plugin = plugin;
        initConfig();
    }

    public FileConfiguration getConfig() {
        return cfg;
    }

    static FileConfiguration cfg;
    static File file;

    public void initConfig(){
        File f = new File("plugins/" + plugin.pluginName);

        if(!f.exists()) f.mkdirs();
        file = new File(f, file_name);

        if(!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException ioe) { ioe.printStackTrace();}
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }
    public void saveConfig() { try { cfg.save(file); } catch (IOException ioe) { ioe.printStackTrace(); } }

    public List<Map> getMaps() {
        List<Map> maps = new ArrayList<>();

        FileConfiguration cfg = getConfig();
        ConfigurationSection cs = cfg.getConfigurationSection("Maps");

        if(cs != null) {
            if(cs.getKeys(false) != null) {

                for(String mapName : cs.getKeys(false)) {
                    Map map = new Map(mapName, plugin);

                    if(map.getMainSpawn(mapName) != null) map.setMainSpawn(map.getMainSpawn(mapName));
                    if(map.getMinPlayers(mapName) > 0) {
                        map.setMinPlayers(map.getMinPlayers(mapName));
                    }
                    if(map.getMaxPlayers(mapName) > 0) {
                        map.setMaxPlayers(map.getMaxPlayers(mapName));
                    }
                    if(map.getSpawnPoints() != null) {
                        for(SpawnPoint spawnPoint : map.getSpawnPoints()) {
                            new SpawnPoint(map, spawnPoint.getName(), spawnPoint.getLocation(), plugin);
                        }
                    }
                    maps.add(map);
                }
            }
        }
        return maps;
    }

    public Map getMapByName(String mapName) {
        for(Map map : getMaps()) {
            if(mapName.equals(map.getName())) return map;
        }
        return null;
    }

    public void chooseGameMap() {

        if(!getMaps().isEmpty()) {
            Random r = new Random();
            int upper = getMaps().size();

            Map map = getMaps().get(r.nextInt(upper));
            if(plugin.gameMap == null) plugin.gameMap = map;
        }
    }

    public SpawnPoint getSpawnPointByName(Map map, String name) {

        for(SpawnPoint spawnPoint : map.getSpawnPoints()) {
            if(name.equals(spawnPoint.getName())) return spawnPoint;
        }
        return null;
    }

}
