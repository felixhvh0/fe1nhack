package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.RotationManager;
import me.alpha432.oyvey.util.Rotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {

    // Settings for KillAura
    private final Setting<Float> range = this.register(new Setting<>("Range", 4.0f, 1.0f, 6.0f));
    private final Setting<Integer> hitDelay = this.register(new Setting<>("HitDelay", 650, 0, 1000)); // Changed to integer to match ms delay
    private final Setting<Boolean> swordCheck = this.register(new Setting<>("SwordCheck", true));
    private final Setting<Boolean> players = this.register(new Setting<>("Players", true));
    private final Setting<Boolean> mobs = this.register(new Setting<>("Mobs", true));
    private final Setting<Boolean> aggressive = this.register(new Setting<>("Agressive", true, v -> this.mobs.getValue()));

    private long lastAttackTime;

    public KillAura() {
        super("KillAura", "Automatically attacks entities", Category.COMBAT, true, false, false);
        lastAttackTime = 0;
    }

    @Override
    public void onTick() {
        // Ensure the player is holding a sword if the swordCheck is enabled
        if (swordCheck.getValue() && !isHoldingSword()) {
            return; // Do nothing if the player is not holding a sword
        }

        // Iterate through entities and perform checks
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) {
                continue; // Don't target the player itself
            }

            // Check if the entity is within range
            if (entity.getPos().distanceTo(mc.player.getPos()) > range.getValue()) {
                continue; // Skip entities that are out of range
            }

            // Handle player or mob targeting
            if (entity instanceof PlayerEntity && players.getValue()) {
                attackEntity(entity);
            } else if (entity instanceof MobEntity && mobs.getValue()) {
                MobEntity mob = (MobEntity) entity;
                if (aggressive.getValue()) {
                    attackEntity(mob);
                }
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        // Render 2D overlays if needed (not implemented in this version)
    }

    private boolean isHoldingSword() {
        // Check if the player is holding a sword (Diamond Sword, Iron Sword, etc.)
        Item heldItem = mc.player.getMainHandStack().getItem();
        return heldItem == Items.DIAMOND_SWORD || heldItem == Items.IRON_SWORD || heldItem == Items.GOLDEN_SWORD ||
                heldItem == Items.STONE_SWORD || heldItem == Items.WOODEN_SWORD;
    }

    private void attackEntity(Entity entity) {
        float[] angle = Rotation.calculateAngle(entity.getPos());
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= hitDelay.getValue()) {
            RotationManager.setRotationSilent(
                    angle[0],
                    angle[1],
                    true
            );
            // Attack the entity
            mc.interactionManager.attackEntity(mc.player, entity);
            mc.player.swingHand(Hand.MAIN_HAND);  // Perform hand swing animation
            lastAttackTime = currentTime; // Update the last attack time
        }
    }
}
