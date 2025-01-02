package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;

import java.util.Random;

public class Trigger extends Module {
    public final Setting<Float> minRange = this.register(new Setting<>("Min Range", 2.7f, 0.0f, 4.0f));
    public final Setting<Float> maxRange = this.register(new Setting<>("Max Range", 3.0f, 0.0f, 4.0f));
    public final Setting<Integer> swingDelay = this.register(new Setting<>("Swing Delay", 15, 1, 20)); // delay in ticks

    private int swingTimer = 0;
    private float randRange;

    public Trigger() {
        super("Trigger", "Automatically attacks entities", Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        super.onEnable();
        randRange = (float) getRandomRange(minRange.getValue(), maxRange.getValue());
        swingTimer = 0; // Reset swing timer when enabled
    }

    public void onTick() {
        if (mc.targetedEntity != null) { // Ensure there is a targeted entity
            Entity target = mc.targetedEntity; // Get the targeted entity

            // Check if the swing timer has passed
            if (swingTimer >= swingDelay.getValue()) {
                // Attack the entity and swing the hand
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.interactionManager.attackEntity(mc.player, target);

                // Reset the swing timer
                swingTimer = 0;

                // Recalculate random range
                randRange = (float) getRandomRange(minRange.getValue(), maxRange.getValue());
            }
        }

        // Increment the swing timer every tick
        swingTimer++;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        // No rendering functionality required
    }

    public double getRandomRange(float min, float max) {
        Random random = new Random();
        float randomValue = min + (random.nextFloat() * (max - min));
        return Math.round(randomValue * 10.0f) / 10.0f;
    }
}
