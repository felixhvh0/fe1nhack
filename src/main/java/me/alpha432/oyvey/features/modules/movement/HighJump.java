package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class HighJump extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Setting<Double> jumpHeight = register(new Setting<>("JumpHeight", 1.0, 0.1, 5.0));


    public HighJump() {
        super("HighJump", "Allows you to jump higher", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        PlayerEntity player = mc.player;

        if (player.isOnGround() && mc.options.jumpKey.isPressed()) {
            player.setVelocity(player.getVelocity().x, jumpHeight.getValue(), player.getVelocity().z);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    //compat
    }
}