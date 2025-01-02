package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;

public class Fov extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Setting for Field of View
    public Setting<Float> fovSetting = this.register(new Setting<>("Fov", 110.0f, 30.0f, 200.0f));  // Allow values past 110

    public Fov() {
        super("Fov", "Adjusts the client's field of view", Category.RENDER, true, false, false);
    }

    private int oldFov;
    @Override
    public void onEnable() {
        oldFov = mc.options.getFov().getValue();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        mc.options.getFov().setValue(fovSetting.getValue().intValue());
        System.out.println("Current FOV: " + mc.options.getFov().getValue());
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    @Override
    public void onDisable() {
        mc.options.getFov().setValue(oldFov);
    }
}
