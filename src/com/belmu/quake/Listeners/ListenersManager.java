package com.belmu.quake.Listeners;

import com.belmu.quake.Core.Railgun.Dash.DashEvent;
import com.belmu.quake.Core.Railgun.Firing.ShootEvent;
import com.belmu.quake.Quake;
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
        reg(new PlayerQuit());

        // Core Events
        reg(new ShootEvent(plugin));
        reg(new DashEvent(plugin));
    }

    private void reg(Listener listener) {

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(listener, plugin);
    }
}
