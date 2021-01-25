package com.belmu.quake.Core.Packets;

import com.belmu.quake.Quake;
import com.belmu.quake.Utils.Fireworks;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Effects {

    public final Quake plugin;
    public Effects(Quake plugin) {
        this.plugin = plugin;
    }

    public void spawnFireworks(Location location, FireworkEffect fireworkEffect, boolean instant){

        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(fireworkEffect);

        fw.setFireworkMeta(meta);
        if(instant) ((CraftFirework) fw).getHandle().expectedLifespan = 1;
    }

    public void tempFireworks(Player player, int stopAfter) {

        new BukkitRunnable() {
            int stop = stopAfter;

            @Override
            public void run() {
                stop--;

                if(player.getLocation() == null) this.cancel();
                if(stop <= 0) this.cancel();

                Fireworks.random(player.getLocation());
            }
        }.runTaskTimer(plugin, 8, 8);
    }

    public void sendParticle(EnumParticle type, Location location, float xOffset, float yOffset, float zOffset, float speed, int count) {
        float x = (float) location.getX();
        float y = (float) location.getY();
        float z = (float) location.getZ();

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(type, true, x, y, z, xOffset, yOffset, zOffset, speed, count, null);
        for(Player online : Bukkit.getOnlinePlayers())
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
    }

}
