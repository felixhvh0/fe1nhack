package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.commands.Command;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class Weather extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Settings
    public Setting<Boolean> clear = this.register(new Setting<>("ClearWeather", true));
    public Setting<Boolean> timechanger = this.register(new Setting<>("TimeChanger", false));
    public Setting<Float> timeOfDay = this.register(new Setting<>("TimeOfDay", 1000f, 0f, 24000f, v -> timechanger.getValue()));
    public Setting<Boolean> snow = this.register(new Setting<>("❅", true));
    public Setting<Float> snowAmount = this.register(new Setting<>("SnowAmount", 240f, 1f, 2500f, v -> snow.getValue())); // Snow particle density
    public Setting<Float> fallspeed = this.register(new Setting<>("SnowfallSpeed", 0.2f, 0.1f, 2f, v -> snow.getValue())); // Snow particle density

    public Weather() {
        super("Weather", "Sets the client's weather to clear and time of day", Category.RENDER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;

        // Add snowflakes if the snow setting is enabled
        if (snow.getValue()) {
            // Get render distance and use it for snow distribution
            int renderDistance = mc.options.getViewDistance().getValue(); // Get the current render distance
            double particlesToSpawn = snowAmount.getValue(); // Get the snow particle amount from settings

            // Generate snowflakes based on render distance and particle density
            for (int i = 0; i < particlesToSpawn; i++) {
                // Randomly generate snowflake positions based on render distance
                double x = mc.player.getX() + (mc.world.random.nextDouble() - 0.5) * renderDistance * 16; // x-coordinate within render distance
                double y = mc.player.getY() + 3.5 + mc.world.random.nextDouble() * 15; // y-coordinate slightly above player
                double z = mc.player.getZ() + (mc.world.random.nextDouble() - 0.5) * renderDistance * 16; // z-coordinate within render distance

                // Add snowflake particle
                mc.world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, -0.01, 0-fallspeed.getValue(), 0); // Snow particle
            }
        }

        // Set weather to clear if the clear weather setting is enabled
        if (clear.getValue()) {
            mc.world.setRainGradient(0f); // Set rain to no rain
            mc.world.setThunderGradient(0f); // Set thunder to no thunder
        }

        // Set time of day (note: only works in single-player or with cheats enabled)
        if (timechanger.getValue()) {
            long time = timeOfDay.getValue().longValue();
            mc.world.setTimeOfDay(time);
        }}

    public void onDisable() {
            mc.world.setRainGradient(100f); // Set rain back
            mc.world.setThunderGradient(100f); // Set thunder back
}
    @Override
    public void onRender2D(Render2DEvent event) {

    }
}

