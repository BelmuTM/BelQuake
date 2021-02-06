package com.belmu.quakecraft.Core.Railgun;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class Railgun {

    public static List<Railgun> railguns = new ArrayList<>();

    private HoeType type;
    private String name;
    private Double cooldown;
    private Double cooldownDash;

    private EnumParticle effect;
    private KillSound killSound;
    private FireworkEffect fireworkEffect;

    public Railgun(HoeType type, String name, Double cooldown, Double cooldownDash,
                   EnumParticle effect, KillSound killSound, FireworkEffect fireworkEffect) {
        this.type = type;
        this.name = name;
        this.cooldown = cooldown;
        this.cooldownDash = cooldownDash;

        this.effect = effect;
        this.killSound = killSound;
        this.fireworkEffect = fireworkEffect;

        railguns.add(this);
    }

    public HoeType getType() { return type; }
    public String getName() { return name; }
    public Double getCooldown() { return cooldown; }
    public Double getCooldownDash() { return cooldownDash; }

    public EnumParticle getEffect() { return effect; }
    public KillSound getKillSound() { return killSound; }
    public Sound getSoundFromKillSound() {
        return Sound.valueOf(killSound.toString());
    }
    public FireworkEffect getFireworkEffect() { return fireworkEffect; }

    public void setType(HoeType type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setCooldown(Double cooldown) { this.cooldown = cooldown; }
    public void setCooldownDash(Double cooldownDash) { this.cooldownDash = cooldownDash; }

    public void setEffect(EnumParticle effect) { this.effect = effect; }
    public void setKillSound(KillSound killSound) { this.killSound = killSound; }
    public void setFireworkEffect(FireworkEffect fireworkEffect) { this.fireworkEffect = fireworkEffect; }

    public ItemStack getItemStack() {

        Material hoeType = Material.matchMaterial(type.toString());
        ItemStack railgun = new ItemStack(hoeType, 1);
        ItemMeta railgunMeta = railgun.getItemMeta();

        railgunMeta.setDisplayName("§r" + name);
        railgunMeta.setLore(Arrays.asList("§7Shoot: §a" + getCooldown() + "s", "§7Dash: §6" + getCooldownDash() + "s"));
        railgunMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        railgun.setItemMeta(railgunMeta);
        return railgun;
    }

    public static Railgun getRailgunByName(String name) {

        for(Railgun railgun : railguns) {
            if(ChatColor.stripColor(railgun.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) return railgun;
        }
        return null;
    }

    public static boolean containsRailgun(String name) {

        for(Railgun railgun : railguns) {
            if(ChatColor.stripColor(railgun.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) return true;
        }
        return false;
    }
}
