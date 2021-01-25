package com.belmu.quake.Core.Railgun.Dash;

import com.belmu.quake.Core.Packets.ActionBar;
import com.belmu.quake.Core.Railgun.Railgun;
import com.belmu.quake.Quake;
import com.belmu.quake.Utils.CooldownManager;
import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class DashEvent implements Listener {

    Map<UUID, Double> dashCooldowns = new HashMap<>();

    public final Quake plugin;
    public final CooldownManager cM;
    public DashEvent(Quake plugin) {
        this.plugin = plugin;
        this.cM = new CooldownManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();
        Action action = e.getAction();
        ItemStack it = e.getItem();

        if (it == null) return;

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {

            // Checks if the item is actually a railgun.
            if (it.hasItemMeta()) {
                if (it.getItemMeta().hasDisplayName()) {
                    String name = it.getItemMeta().getDisplayName();

                    if (Railgun.containsRailgun(name)) {
                        Railgun railgun = Railgun.getRailgunByName(name);
                        e.setCancelled(true);

                        if(railgun != null) {
                            double cooldown = railgun.getCooldownDash();
                            DecimalFormat df = new DecimalFormat("#0.0");

                            if(cM.checkCooldown(dashCooldowns, player)) {

                                cM.setCooldown(dashCooldowns, player, cooldown);
                                Dash.dashPlayer(player);

                                // Gets the percentage of time left and displays it in the player's action bar.
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        double timeLeft = cM.getCooldown(dashCooldowns, player);

                                        if(timeLeft > 0) {
                                            String progressBar = getProgressBar((int) timeLeft, (int) cooldown, 10, '■', ChatColor.RED, ChatColor.GREEN);
                                            String cooldownBar = "§8[" + progressBar + "§8] §6" + df.format(timeLeft) + " seconds";

                                            ActionBar.sendActionBar(player, cooldownBar);
                                        } else {
                                            ActionBar.sendActionBar(player, " ");
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 0L, 3L);
                            } else {

                                double timeLeft = cM.getCooldown(dashCooldowns, player);
                                player.sendMessage(Quake.prefix + "§7Your dash is in cooldown for §a" + df.format(timeLeft) + "s§7.");
                            }
                        }
                    }
                }
            }
        }
    }

    // I DIDN'T MAKE THIS METHOD. (https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/)
    public String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars)
                + Strings.repeat("" + completedColor + symbol, progressBars);
    }

}
