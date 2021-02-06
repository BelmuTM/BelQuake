package com.belmu.quakecraft.Core;

import com.belmu.quakecraft.Core.Railgun.HoeType;
import com.belmu.quakecraft.Core.Railgun.KillSound;
import com.belmu.quakecraft.Core.Railgun.Railgun;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

/**
 * @author Belmu (https://github.com/BelmuTM/)
 */
public class GameOptions {

    public static int toWin = 25;

    public static double timeBeforeStart = 1 * 60;
    public static double timer = 10 * 60;
    public static double beforeKickAll = 18;

    public static FireworkEffect fireworkEffect = FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.LIME).withColor(Color.YELLOW).withColor(Color.FUCHSIA).flicker(true).build();
    public static Railgun railgun = new Railgun(HoeType.IRON_HOE, "§dRailgun", 1D, 4.0D,
            EnumParticle.VILLAGER_HAPPY, KillSound.LEVEL_UP, fireworkEffect);

    public static double invulnerabilityTime = 2.3D;
}
