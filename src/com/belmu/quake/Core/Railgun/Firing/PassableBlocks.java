package com.belmu.quake.Core.Railgun.Firing;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.PressurePlate;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public enum PassableBlocks {

    AIR,
    STAINED_GLASS,
    STAINED_GLASS_PANE,
    GLASS,
    THIN_GLASS,
    RAILS,
    ACTIVATOR_RAIL,
    POWERED_RAIL,
    DETECTOR_RAIL,
    DAYLIGHT_DETECTOR,
    DAYLIGHT_DETECTOR_INVERTED,
    WEB,
    FLOWER_POT,
    MELON_STEM,
    PUMPKIN_STEM,
    NETHER_WARTS,
    LONG_GRASS,
    DEAD_BUSH,
    YELLOW_FLOWER,
    RED_ROSE,
    VINE,
    BROWN_MUSHROOM,
    RED_MUSHROOM,
    TORCH,
    REDSTONE_WIRE,
    REDSTONE_TORCH_OFF,
    REDSTONE_TORCH_ON,
    REDSTONE_COMPARATOR,
    REDSTONE_COMPARATOR_OFF,
    REDSTONE_COMPARATOR_ON,
    FIRE,
    SIGN,
    SIGN_POST,
    WALL_SIGN,
    LADDER,
    SNOW,
    LEVER,
    IRON_FENCE,
    STONE_BUTTON,
    WOOD_BUTTON,
    CARPET,
    WATER_LILY,
    COCOA,
    SUGAR_CANE_BLOCK,
    DOUBLE_PLANT,
    SAPLING,
    CARROT,
    POTATO,
    BANNER,
    STANDING_BANNER,
    WALL_BANNER,
    TRIPWIRE_HOOK,
    PORTAL,
    SKULL,
    WATER,
    STATIONARY_WATER,
    LAVA,
    STATIONARY_LAVA,
    ICE;

    public static boolean isFence(Material material) {
        return material.toString().toLowerCase().contains("fence");
    }

    public static boolean isSlab(Material material) {
        return material == Material.STEP || material == Material.WOOD_STEP;
    }

    public static boolean isLeaves(Material material) {
        return material == Material.LEAVES || material == Material.LEAVES_2;
    }

    public static boolean isPressurePlate(Material material) {
        return material == Material.STONE_PLATE || material == Material.WOOD_PLATE
                || material == Material.IRON_PLATE || material == Material.GOLD_PLATE;
    }

    public static boolean contains(Material material) {

        for(Object materials : PassableBlocks.values()) {
            Material foundMaterial = Material.matchMaterial(materials.toString());

            if(material == foundMaterial) return true;
        } return false;
    }
}
