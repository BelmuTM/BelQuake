package com.belmu.quakecraft.Core.Powerup;

import com.belmu.quakecraft.Core.GameOptions;
import com.belmu.quakecraft.Core.Packets.Effects;
import com.belmu.quakecraft.Core.Railgun.Firing.Shoot;
import com.belmu.quakecraft.Core.Railgun.Railgun;
import com.belmu.quakecraft.Quake;
import com.belmu.quakecraft.Utils.Countdown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class PowerupManager {

    public int respawnTime = 1 * 60;

    public final Quake plugin;
    public PowerupManager(Quake plugin) {
        this.plugin = plugin;
    }

    public List<Powerup> powerups = new ArrayList<>();
    public Map<Powerup, Item> powerupsItems = new HashMap<>();

    public void spawnPowerup(Powerup powerup) {
        Location location = powerup.getLocation();
        World world = location.getWorld();

        Location itemLocation = new Location(world, location.getX(), location.getY() + 1D, location.getZ());

        Item item = world.dropItem(itemLocation, new ItemStack(powerup.getMaterial()));
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setVelocity(new Vector(0, 0, 0));

        if(!powerups.contains(powerup)) powerups.add(powerup);

        Location particlesLocation = new Location(world, itemLocation.getX(), itemLocation.getY() - 0.20D, itemLocation.getZ());
        new BukkitRunnable() {

            @Override
            public void run() {
                if(!powerups.contains(powerup)) this.cancel();

                Effects effects = new Effects(plugin);
                effects.sendParticle(powerup.getParticle(), particlesLocation, 0, 0, 0, 0.0485f, 5);
            }
        }.runTaskTimer(plugin, 12, 12);
    }

    public void despawnPowerup(Powerup powerup) {
        powerups.remove(powerup);
        powerupsItems.get(powerup).remove();
    }

    public void despawnPowerups() {

        if(!powerupsItems.isEmpty()) for(Item item : powerupsItems.values()) item.remove();
        if(!powerups.isEmpty()) {
            for (Powerup powerup : powerups) {
                powerups.remove(powerup);
                powerupsItems.remove(powerup);
            }
        }
    }

    public Map<UUID, Double> powerupTime = new HashMap<>();
    public void activatePowerup(Powerup powerup, Player player) {
        String powerupName = powerup.getPowerupType().toString().replaceAll("_", " ");

        Countdown respawn = new Countdown(plugin,
                70,

                () -> {
                    Bukkit.broadcastMessage(Quake.prefix + "§e" + player.getName() + " §ahas activated §6§l" + powerupName + "§r§a!");

                    Countdown powTime = new Countdown(plugin,
                            powerup.getTime(),

                            () -> {
                                powerupTime.put(player.getUniqueId(), powerup.getTime()); },
                            () -> {
                                powerupTime.remove(player.getUniqueId()); },
                            (t) -> {}
                    );
                    powTime.scheduleTimer();

                    despawnPowerup(powerup);
                    effect(powerup, player);
                },
                () -> {

                    spawnPowerup(powerup);
                },
                (t) -> {
                }
        );
        respawn.scheduleTimer();
    }

    public void effect(Powerup powerup, Player player) {
        PowerupType type = powerup.getPowerupType();

        if(type == PowerupType.RAPID_FIRE) {
            Railgun railgun = Railgun.getPlayerRailgun(player);

            new BukkitRunnable() {

                @Override
                public void run() {
                    if(!player.isOnline()) this.cancel();
                    if(!powerupTime.containsKey(player.getUniqueId())) this.cancel();

                    if(railgun == null || railgun.getItemStack() == null) return;

                    if(player.getItemInHand() == railgun.getItemStack()) {
                        Shoot shoot = new Shoot(plugin);
                        shoot.shootRailgun(player, railgun);
                    }
                }
            }.runTaskTimer(plugin, 12, 12);
        }

        else if(type == PowerupType.SPEED) {
            Countdown speed = new Countdown(plugin,
                    powerup.getTime(),

                    () -> {
                        player.removePotionEffect(PotionEffectType.SPEED);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (powerup.getTime() * 20), 2, false, false));
                    },
                    () -> {
                        player.removePotionEffect(PotionEffectType.SPEED);
                        player.addPotionEffect(GameOptions.potionEffect);
                    },
                    (t) -> {
                    }
            );
            speed.scheduleTimer();
        }
    }
}
