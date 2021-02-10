package com.belmu.quakecraft.Core.Railgun;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public enum KillSound {

    BLAZE_DEATH(2f),
    BAT_DEATH(1f),
    HORSE_DEATH(1.5f),
    ENDERMAN_DEATH(1.5f),
    IRONGOLEM_DEATH(1.5f),
    PIG_DEATH(1f),
    COW_HURT(1.75f),
    CREEPER_DEATH(1.5f),
    ANVIL_LAND(1.0f),
    GHAST_DEATH(1.5f),
    DRAGON_GROWL(1.5f),
    VILLAGER_YES(1f),
    WITHER_IDLE(2f),
    LEVEL_UP(1.0f),
    ZOMBIE_WOODBREAK(1.25f);

    private float pitch;
    KillSound(float pitch) {
        this.pitch = pitch;
    }

    public float getPitch() { return pitch; }
}
