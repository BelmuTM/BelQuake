package com.belmu.quakecraft.Core;

import com.belmu.quakecraft.Core.Railgun.HoeType;
import com.belmu.quakecraft.Core.Railgun.KillSound;
import com.belmu.quakecraft.Core.Railgun.Railgun;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class GameOptions {

    public static int toWin = 25;

    public static double timeBeforeStart = 1 * 60;
    public static double timer = 10 * 60;
    public static double beforeKickAll = 18;

    public static PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false);

    public static FireworkEffect fireworkEffect = FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.LIME).withColor(Color.YELLOW).withColor(Color.FUCHSIA).flicker(true).build();
    public static Railgun railgun = new Railgun(HoeType.IRON_HOE, "§dRailgun", 1.1D, 4.0D,
            EnumParticle.FIREWORKS_SPARK, KillSound.BLAZE_DEATH, fireworkEffect);

    public static int railgunSlot = 0;

    public static double invulnerabilityTime = 2.20D;
}
