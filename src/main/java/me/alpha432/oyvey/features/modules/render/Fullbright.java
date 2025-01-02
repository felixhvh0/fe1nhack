package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends Module {
    private static Fullbright INSTANCE = new Fullbright();
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.GAMMA));

    public static Fullbright getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Fullbright();
        }
        return INSTANCE;
    }

    public Fullbright() {
        super("Fullbright", "Makes the game fully bright.", Category.RENDER, true, false, false);
    }


    @Override
    public void onEnable() {
        if (mode.getValue() == Mode.GAMMA) {
        } else if (mode.getValue() == Mode.POTION) {
            applyPotionEffect(true);
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == Mode.GAMMA) {
        } else if (mode.getValue() == Mode.POTION) {
            applyPotionEffect(false);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        // compat
    }


    private void applyPotionEffect(boolean enable) {
        if (mc.player != null) {
            if (enable) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            } else {
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }

    public enum Mode {
        GAMMA,
        POTION
    }
}
