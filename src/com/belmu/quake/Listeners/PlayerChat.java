package com.belmu.quake.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PlayerChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();
        String msg = e.getMessage();

        if (msg.contains("<3"))
            e.setMessage(msg.replaceAll("<3", "§c❤§r"));

        e.setFormat(player.getPlayerListName() + " §8» §f" + e.getMessage());
    }

}
