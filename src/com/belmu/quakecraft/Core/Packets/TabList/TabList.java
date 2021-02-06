package com.belmu.quakecraft.Core.Packets.TabList;

import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class TabList {

    public final Quake plugin;
    public TabList(Quake plugin) {
        this.plugin = plugin;
    }

    int frames = 0;

    public void execute(Player player) {

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    Field a = packet.getClass().getDeclaredField("a");
                    a.setAccessible(true);
                    Field b = packet.getClass().getDeclaredField("b");
                    b.setAccessible(true);

                    for(TabEnum frame : TabEnum.values()) {

                        if(frames == frame.id) {
                            Object header = new ChatComponentText(frame.header);
                            Object footer = new ChatComponentText(frame.footer + " §7| §a" + ((CraftPlayer) player).getHandle().ping + "ms");

                            a.set(packet, header);
                            b.set(packet, footer);
                        }
                    }
                    frames += 1;
                    if(frames >= (TabEnum.values().length) - 1) frames = 0;

                    if(Bukkit.getOnlinePlayers().size() == 0) return;
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 0, 15);
    }

}
