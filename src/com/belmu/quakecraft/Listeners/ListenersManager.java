package com.belmu.quakecraft.Listeners;

import com.belmu.quakecraft.Core.Powerup.PowerupActivateEvent;
import com.belmu.quakecraft.Core.Railgun.Dash.DashEvent;
import com.belmu.quakecraft.Core.Railgun.Firing.ShootEvent;
import com.belmu.quakecraft.Quake;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class ListenersManager {

    public final Quake plugin;
    public ListenersManager(Quake plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {

        // Main Events
        reg(new CancelledEvents(plugin));
        reg(new PlayerChat());
        reg(new PlayerJoin(plugin));
        reg(new PlayerQuit(plugin));
        reg(new PowerupActivateEvent(plugin));

        // Core Events
        reg(new ShootEvent(plugin));
        reg(new DashEvent(plugin));
    }

    private void reg(Listener listener) {

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(listener, plugin);
    }
}
