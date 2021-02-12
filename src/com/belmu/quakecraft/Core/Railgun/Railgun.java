package com.belmu.quakecraft.Core.Railgun;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
    private double cooldown;
    private double cooldownDash;

    private EnumParticle effect;
    private KillSound killSound;
    private FireworkEffect fireworkEffect;

    public Railgun(HoeType type, String name, double cooldown, double cooldownDash,
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
    public double getCooldown() { return cooldown; }
    public double getCooldownDash() { return cooldownDash; }

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

    public static Railgun getPlayerRailgun(Player player) {

        for(ItemStack item : player.getInventory().getContents()) {
            for(Railgun railgun : railguns)
                if(isSimilar(item, railgun.getItemStack())) return railgun;
        }
        return null;
    }

    /* I DID NOT MAKE THIS METHOD. (https://www.spigotmc.org/threads/comparing-item-stacks.247785/?__cf_chl_jschl_tk__=75f635299a7776408f7fae334436d85c2225627f-1613118799-0-ATNvHeX2yMu5zCvw6yRHRWIjfR2_hSjo1uK3zSyV2s9VqZSqq6qPueKl3ElRjzJSUNjTmNDZ6b9GPc6UsCTOQAspM2fIem31SP6052PwdHHrQEFX2CZG-afri6v8himaErHY3FkXvt2U_8vnRx5FWHjkh9spel2SLX6Wwg04VL0th43TLExW_fnVlqht_DdMmttL5MTbj04vWGdOuUytDkKTV7feE0ptvjGqbZXt5zh3VzJ-t4bVnC59-e8uzljCyEEpBTYneOnIlPHE6-a7KraWcJOsoJTN_73FUoc10FBA2xXT6trpWFZBXVCda3A7fzH2cnX681jQD3ZBNTzzKq1bp7qx6dEyLbFRCifH70OJ)
     * I modified it.
     */
    public static boolean isSimilar(ItemStack first,ItemStack second){
        boolean similar = false;

        if(first == null || second == null)
            return similar;

        boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
        boolean sameItemMeta = true;

        if(sameHasItemMeta)
            sameItemMeta = Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta());

        if(sameHasItemMeta && sameItemMeta) similar = true;

        return similar;
    }

}
