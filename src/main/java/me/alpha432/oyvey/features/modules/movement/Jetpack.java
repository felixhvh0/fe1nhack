package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Jetpack extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Setting<Double> jumpHeight = register(new Setting<>("Speed", 1.0, 0.1, 5.0));

    public Jetpack() {
        super("Jetpack", "WEEEEEEEEEEEEEEEEEEEEEEEE", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        PlayerEntity player = mc.player;
        if (player == null) return;


        if (mc.options.jumpKey.isPressed()) {
            player.setVelocity(player.getVelocity().x, jumpHeight.getValue(), player.getVelocity().z);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }
}