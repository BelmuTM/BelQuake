package com.belmu.quakecraft.Utils;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class CooldownManager {

    public void setCooldown(Map<UUID, Double> cooldowns, Player player, double seconds){
        double delay = System.currentTimeMillis() + (seconds * 1000);
        cooldowns.put(player.getUniqueId(), delay);
    }

    public double getCooldown(Map<UUID, Double> cooldowns, Player player){
        return (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    public boolean checkCooldown(Map<UUID, Double> cooldowns, Player player){
        return !cooldowns.containsKey(player.getUniqueId()) || cooldowns.get(player.getUniqueId()) <= System.currentTimeMillis();
    }

}
