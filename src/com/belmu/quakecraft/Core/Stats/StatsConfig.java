package com.belmu.quakecraft.Core.Stats;

import com.belmu.quakecraft.Quake;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class StatsConfig {

    private String file_name = "player_stats.yml";
    private String path = "Players.";

    public final Quake plugin;
    public StatsConfig(Quake plugin) {
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

    public List<UUID> getPlayers() {
        List<UUID> players = new ArrayList<>();
        ConfigurationSection cs = cfg.getConfigurationSection("Players");

        if(cs != null) {
            if (cs.getKeys(false) != null) {

                for (String playerUUID : cs.getKeys(false)) {
                    players.add(UUID.fromString(playerUUID));
                }
            }
        }
        return players;
    }

    public boolean containsPlayer(UUID uuid) {
        return getPlayers().contains(uuid);
    }

    public void setKills(UUID uuid, int amount) {
        cfg.set(path + uuid + ".kills", amount);
        saveConfig();
    }
    public void setDeaths(UUID uuid, int amount) {
        cfg.set(path + uuid + ".deaths", amount);
        saveConfig();
    }
    public void setWins(UUID uuid, int amount) {
        cfg.set(path + uuid + ".wins", amount);
        saveConfig();
    }

    public int getKills(UUID uuid) {
        if(containsPlayer(uuid)) {
            return cfg.getInt(path + uuid + ".kills");
        } return 0;
    }
    public int getDeaths(UUID uuid) {
        if(containsPlayer(uuid)) {
            return cfg.getInt(path + uuid + ".deaths");
        } return 0;
    }
    public int getWins(UUID uuid) {
        if(containsPlayer(uuid)) {
            return cfg.getInt(path + uuid + ".wins");
        } return 0;
    }

    public void addKills(UUID uuid, int amount) {
        int kills = getKills(uuid);
        setKills(uuid, kills + amount);
    }
    public void addDeaths(UUID uuid, int amount) {
        int deaths = getDeaths(uuid);
        setDeaths(uuid, deaths + amount);
    }
    public void addWins(UUID uuid, int amount) {
        int wins = getWins(uuid);
        setWins(uuid, wins + amount);
    }

    public LinkedHashMap<UUID, Integer> sortedKills() {
        LinkedHashMap<UUID, Integer> sorted = new LinkedHashMap<>();
        Map<UUID, Integer> kills = new HashMap<>();

        for(UUID uuid : getPlayers()) {
            kills.put(uuid, getKills(uuid));
        }

        kills.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));

        return sorted;
    }

}
