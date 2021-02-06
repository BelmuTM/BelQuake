package com.belmu.quakecraft.Core;

import org.bukkit.Sound;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public enum GameSound {

    CLICK(Sound.CLICK, 1.0f),
    PLING(Sound.NOTE_PLING, 0.35f),
    LEVEL_UP(Sound.LEVEL_UP, 1f);

    private Sound sound;
    private float pitch;
    GameSound(Sound sound, float pitch) {
        this.sound = sound;
        this.pitch = pitch;
    }

    public Sound getSound() { return sound; }
    public float getPitch() { return pitch; }
}
