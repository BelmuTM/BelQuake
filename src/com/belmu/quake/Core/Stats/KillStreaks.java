package com.belmu.quake.Core.Stats;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillStreaks {

    public static Map<UUID, Integer> killStreaks = new HashMap<>();

    public static void setStreak(Player player, int amount) {
        killStreaks.put(player.getUniqueId(), amount);
    }

    public static int getStreak(Player player) {
        if(containsPlayer(player)) {
            return killStreaks.get(player.getUniqueId());
        } return 0;
    }

    public static void addStreak(Player player, int amount) {
        int streak = getStreak(player);
        setStreak(player, streak + amount);
    }

    public static boolean containsPlayer(Player player) {
        return killStreaks.containsKey(player.getUniqueId());
    }

}
