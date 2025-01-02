package me.alpha432.oyvey.features.modules.render;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.DeathEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;

public class DeathEffects extends Module {

    public DeathEffects() {
        super("DeathEffects", "Effects when you death.", Category.RENDER, true, false, false);
    }

    public final Setting<Boolean> sound = this.register(new Setting<>("Sound", true));

    @Subscribe
    public void onDeath(DeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity player) {
            int i = 0;
            if (i == 0) {
                final LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
                bolt.updatePositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                mc.world.addEntity(bolt);
                ++i;
            }
            if (this.sound.getValue()) {
                DeathEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
