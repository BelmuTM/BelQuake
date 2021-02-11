package com.belmu.quakecraft.Core.Railgun.Firing;

import com.belmu.quakecraft.Core.GameOptions;
import com.belmu.quakecraft.Core.GameState;
import com.belmu.quakecraft.Core.Map.Map;
import com.belmu.quakecraft.Core.Map.SpawnProtection;
import com.belmu.quakecraft.Core.Packets.ActionBar;
import com.belmu.quakecraft.Core.Packets.Effects;
import com.belmu.quakecraft.Core.Railgun.KillMessages;
import com.belmu.quakecraft.Core.Railgun.Railgun;
import com.belmu.quakecraft.Core.Stats.KillStreaks;
import com.belmu.quakecraft.Core.Stats.StatsConfig;
import com.belmu.quakecraft.Core.Stats.StreaksMessages;
import com.belmu.quakecraft.Quake;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockStep;
import net.minecraft.server.v1_8_R3.BlockStepAbstract;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.material.Step;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Shoot {

    public final Quake plugin;
    public final GameState state;
    public final StatsConfig config;
    public final Map map;

    public Shoot(Quake plugin) {
        this.plugin = plugin;
        this.state = plugin.gameState;
        this.config = plugin.statsConfig;
        this.map = plugin.gameMap;
    }

    static int railgunRange = 60;

    public void shootRailgun(Player player, Railgun railgun) {
        List<Entity> entitiesKilled = new ArrayList<>();

        Effects effects = new Effects(plugin);
        World world = player.getWorld();
        Location playerLoc = player.getEyeLocation();

        boolean headshot = false;

        /** @Sound
         * Plays the firing sound to the player if they are
         * invisible and to everyone if they aren't.
         */
        for(Player online : Bukkit.getOnlinePlayers())
            online.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.5f, 2f);

        Vector direction = playerLoc.getDirection();
        direction.multiply(railgunRange);
        Location destination = playerLoc.clone().add(direction);

        // Casts a ray where the player is looking at.
        direction.normalize();
        for(int i = 0; i < railgunRange; i++) {

            Location location = playerLoc.add(direction);
            Block block = location.getBlock();

            List<Entity> entitiesInSight = getEntitiesInSight(block, player, railgunRange);

            // Checks if the nearest entity is the last killed one.
            if(!entitiesInSight.isEmpty()) {
                for(Entity entity : world.getNearbyEntities(location, 1.65, 1.65, 1.65)) {

                    if(isValid(entity, player)) {
                        Location entityLoc = entity.getLocation();

                        if(entityLoc.distance(location) <= 1.5) {

                            if(entity == entitiesInSight.get(entitiesInSight.size() - 1)) {
                                destination = location;
                                break;
                            }
                        }
                    }
                }
            }
            if(collision(block, location)) break;

            // Animating railgun's beam + sound.
            if(destination != location && !collision(block, location)) effects.sendParticle(railgun.getEffect(), location, 0, 0, 0, 0, 1);

            /** @Kill
             * Gets all player entities in player's sight and kills them.
             */
            for(Entity entity : entitiesInSight) {
                if(entity instanceof Player) {
                    Player target = (Player) entity;

                    if(SpawnProtection.isInvulnerable(player)) return;
                    if(SpawnProtection.isInvulnerable(target)) return;
                    // Plays the death animation, adds the entity to the killed entities list then removes it.
                    if(!entitiesKilled.contains(target)) {

                        entitiesKilled.add(target);

                        // Total kills / deaths
                        config.addKills(player.getUniqueId(), 1);
                        config.addDeaths(target.getUniqueId(), 1);

                        checkWin(player); // Checks if player has enough kills to win.

                        // Killstreaks
                        KillStreaks.addStreak(player, 1);
                        if(KillStreaks.containsPlayer(target)) KillStreaks.setStreak(target, 0);

                        if(state.running) state.addKills(player.getUniqueId(), 1);
                    }

                    if(headshot(destination, entity)) {
                        headshot = true;
                        ActionBar.sendActionBar(player, "§e§lHEADSHOT");
                    }
                }
            }
        }
        /**
         * @Effects like sound, particles and messages + teleporting
         * killed players to a spawn point.
         */

        // Death messages, sounds,...
        postEffects(entitiesKilled, player, railgun);

        for(Entity killed : entitiesKilled) {
            Player target = (Player) killed;
            String hs = "";

            if(headshot) hs = " §7(§eHeadshot§7)";

            Bukkit.broadcastMessage(Quake.prefix + "§7" + player.getName() + " §fgibbed §7" + target.getName() + hs);
            map.teleportToSpawnPoint(target);
        }

        // Multiple kills messages. (e.g.: DOUBLE KILL, PENTA KILL,...)
        String msg = KillMessages.getKillMsgByAmount(entitiesKilled.size());
        if(msg != null) Bukkit.broadcastMessage(Quake.prefix + msg);

        if(!entitiesKilled.isEmpty()) {
            // Killstreaks messages.
            String killStreakMessage = StreaksMessages.getStreaksMsgByAmount(KillStreaks.getStreak(player));
            if(killStreakMessage != null)
                Bukkit.broadcastMessage(Quake.prefix + killStreakMessage.replaceAll("%player%", player.getName()));
        }
    }

    // Gets all entities in the player's sight.
    public static List<Entity> getEntitiesInSight(Block block, Player player, int range) {
        List<Entity> entitiesInSight = new ArrayList<>();

        List<Entity> entities = player.getNearbyEntities(range, range, range);
        Iterator<Entity> iterator = entities.iterator();

        while(iterator.hasNext()) {
            Entity next = iterator.next();
            if(!(next instanceof LivingEntity) || next == player)
                iterator.remove();
        }
        // Hit Detection
        Location low = block.getLocation();
        Location high = low.clone().add(0.85D, 0.85D, 0.85D);

        // Gets the block's bounding box depending on the min and max coordinates values.
        AxisAlignedBB blockBoundingBox = AxisAlignedBB.a(low.getX(), low.getY(), low.getZ(), high.getX(), high.getY(), high.getZ());

        // Check if the entities' bounding boxes intersect with the block's one.
        for(Entity entity : entities) {
            AxisAlignedBB entityBoundingBox = ((CraftEntity) entity).getHandle().getBoundingBox().grow(0.60D, 0.60D, 0.60D);

            if(entityBoundingBox.b(blockBoundingBox)) {

                if(isValid(entity, player))
                    entitiesInSight.add(entity);
            }
        }
        return entitiesInSight;
    }

    // If the material is full and solid, then it's a collision.
    public static boolean collision(Block block, Location location) {
        Material material = block.getType();

        if(!PassableBlocks.contains(material)) {

            if(PassableBlocks.isSlab(material))
                return location.getY() - block.getLocation().getY() > 0.5;
            if(PassableBlocks.isFence(material)) return false;
            return true;
        }
        return false;
    }

    // Checks if the location is around the entity's head.
    public static boolean headshot(Location location, Entity entity) {
        double y = location.getY();
        double entityY = entity.getLocation().getY();

        return y - entityY > 1.3325d;
    }

    public static boolean isValid(Entity entity, Player player) {
        return entity != player && entity instanceof Player;
    }
    public void postEffects(List<Entity> entitiesKilled, Player player, Railgun railgun) {

        if(!entitiesKilled.isEmpty()) {
            for (Entity entity : entitiesKilled) {
                if(entity instanceof Player) {
                    Player target = (Player) entity;

                    for (Player online : Bukkit.getOnlinePlayers())
                        online.playSound(player.getLocation(), railgun.getSoundFromKillSound(), 1.5f, railgun.getKillSound().getPitch());

                    Location eLoc = target.getLocation();
                    Location fireWorkLocation = new Location(eLoc.getWorld(), eLoc.getX(), eLoc.getY() + 0.95D, eLoc.getZ());

                    Effects effects = new Effects(plugin);
                    effects.spawnFireworks(fireWorkLocation, railgun.getFireworkEffect(), true);
                }
            }
        }
    }

    public void checkWin(Player player) {

        if(state.getTimer() > 0 && state.running) {

            if(player.isOnline() && state.gameKills.containsKey(player.getUniqueId())) {
                int kills = state.gameKills.get(player.getUniqueId());
                if(kills >= (GameOptions.toWin - 1)) state.winner = player;
            }
        }
    }

}
