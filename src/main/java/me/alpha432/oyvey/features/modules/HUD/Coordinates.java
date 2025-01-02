package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class Coordinates extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private static Coordinates instance;

    public Setting<Boolean> showOppositeCoords = this.register(new Setting<>("OppositeDimension", true));

    public Coordinates() {
        super("Coordinates", "", Category.HUD, true, false, false);
        instance = this;
    }

    public static Coordinates getInstance() {
        return instance;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null) return;

        TextRenderer textRenderer = mc.textRenderer;
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        RegistryKey<World> currentWorldKey = mc.world.getRegistryKey();
        String currentCoordsText = "XYZ: " + (int) mc.player.getX() + " " + (int) mc.player.getY() + " " + (int) mc.player.getZ();
        String oppositeCoordsText = "";

        if (currentWorldKey == World.OVERWORLD) {
            oppositeCoordsText = " [" + (int) (mc.player.getX() / 8) + " " + (int) (mc.player.getZ() / 8) + "]";
        } else if (currentWorldKey == World.NETHER) {
            oppositeCoordsText = " [" + (int) (mc.player.getX() * 8) + " " + (int) (mc.player.getZ() * 8) + "]";
        } else if (currentWorldKey == World.END) {
            oppositeCoordsText = " [" + (int) (mc.player.getX() * 8) + " " + (int) (mc.player.getZ() * 8) + "]";
        }

        String coordsText = currentCoordsText;
        if (showOppositeCoords.getValue()) {
            coordsText += oppositeCoordsText;
        }

        int coordsX = 5;
        int coordsY = mc.getWindow().getScaledHeight() -10;
        event.getContext().drawTextWithShadow(textRenderer, coordsText, coordsX, coordsY, argbColor);
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
