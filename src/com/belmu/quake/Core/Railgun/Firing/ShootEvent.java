package com.belmu.quake.Core.Railgun.Firing;

import com.belmu.quake.Core.Railgun.Railgun;
import com.belmu.quake.Quake;
import com.belmu.quake.Utils.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class ShootEvent implements Listener {

    Map<UUID, Double> shootCooldowns = new HashMap<>();

    public final Quake plugin;
    public final CooldownManager cM;
    public ShootEvent(Quake plugin) {
        this.plugin = plugin;
        this.cM = new CooldownManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        Action action = e.getAction();
        ItemStack it = e.getItem();

        if (it == null) return;

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {

            // Checks if the item is actually a railgun.
            if (it.hasItemMeta()) {
                if (it.getItemMeta().hasDisplayName()) {
                    String name = it.getItemMeta().getDisplayName();

                    if (Railgun.containsRailgun(name)) {
                        Railgun railgun = Railgun.getRailgunByName(name);
                        e.setCancelled(true);

                        if(railgun != null) {
                            Double cooldown = railgun.getCooldown();

                            if(cM.checkCooldown(shootCooldowns, player)) {

                                cM.setCooldown(shootCooldowns, player, cooldown);

                                Shoot shoot = new Shoot(plugin);
                                shoot.shootRailgun(player, railgun);

                                // Sets player's xp to the cooldown time left scaled to a range between 0 -> 1.
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        double timeLeft = cM.getCooldown(shootCooldowns, player);
                                        double amount = timeLeft / cooldown;

                                        float floatAmount = ((Double) amount).floatValue();

                                        player.setLevel((int) timeLeft);
                                        player.setExp(floatAmount);

                                        if(amount <= 0) {
                                            player.setLevel(0);
                                            player.setExp(0);
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 0L, 3L);
                            }
                        }
                    }
                }
            }
        }
    }

}
