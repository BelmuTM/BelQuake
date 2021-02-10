package com.belmu.quakecraft.Core.Map;

import com.belmu.quakecraft.Quake;
import com.belmu.quakecraft.Utils.Countdown;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class SpawnProtection {

    static List<UUID> invulnerable = new ArrayList<>();

    public final Quake plugin;
    public SpawnProtection(Quake plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param time Defines the amount of time when the player will be invulnerable.
     */
    public void invulnerability(Player player, double time) {
        UUID uuid = player.getUniqueId();

        Countdown invulnerability = new Countdown(plugin,
            time,

            () -> {
                if(!invulnerable.contains(uuid)) invulnerable.add(uuid);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (int) time * 20, 2, true, false));
            },
            () -> {
                invulnerable.remove(uuid);
            },
            (t) -> {}
        );
        invulnerability.scheduleTimer();
    }

    public static boolean isInvulnerable(Player player) {
        return invulnerable.contains(player.getUniqueId());
    }
}
