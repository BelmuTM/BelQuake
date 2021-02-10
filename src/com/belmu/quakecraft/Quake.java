package com.belmu.quakecraft;

import com.belmu.quakecraft.Commands.QuakeCmd;
import com.belmu.quakecraft.Core.GameState;
import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Core.Map.MapManager;
import com.belmu.quakecraft.Core.Stats.StatsConfig;
import com.belmu.quakecraft.Listeners.ListenersManager;
import com.belmu.quakecraft.Utils.Properties;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Quake extends JavaPlugin implements Listener {

    /**
     * @README Do not ever try to claim my work as your own, fool (╯°□°）>.
     *
     * </p> *What is Quakecraft ?* </p>
     * Quakecraft is a fast-paced First Person Shooter inspired by the game Quake and re-imagined for
     * Minecraft by the Hypixel Network. In it you use a Railgun to kill your opponents in one shot.
     * You can play against a swarm of enemies, or in a team competition! This plugin aims to freshen
     * the original idea up by updating it to a more stylized version.
     *
     * @Havefun
     */

    public String pluginName = "BelQuake";
    public static String prefix = "§8[§dQuake§8] » §7";

    public MapManager mapManager;
    public int mapTime;
    public StatsConfig statsConfig;

    public GameState gameState;
    public Map gameMap;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);
        mapManager = new MapManager(this);
        statsConfig = new StatsConfig(this);
        gameState = new GameState(this);

        ListenersManager lm = new ListenersManager(this);
        lm.registerListeners();

        getCommand("quake").setExecutor(new QuakeCmd(this));

        setProperties();
        Bukkit.getWorld("world").setGameRuleValue("doDaylightCycle", "false");

        mapTime = mapManager.chooseGameMap();

        mapManager.saveConfig();
        gameState.running = false;
    }

    @Override
    public void onDisable() {
        gameMap = null;
    }

    /**
     * Modifies the server.properties file's values to match the game's needs.
     */
    public void setProperties() {
        Properties.setServerProperty(Properties.ServerProperty.ANNOUNCE_PLAYER_ACHIEVEMENTS, false);
        Properties.setServerProperty(Properties.ServerProperty.SPAWN_ANIMALS, false);
        Properties.setServerProperty(Properties.ServerProperty.SPAWN_MONSTERS, false);
        Properties.setServerProperty(Properties.ServerProperty.PVP, false);
    }
}
