package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class Direction extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Direction() {
        super("Direction", "", Category.HUD, true, false, false);
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
        int yOffset = 20;
        if(!Coordinates.getInstance().isEnabled()) {
             yOffset = 10;
        }
        String directionText = getDirectionText();
        int dirY = mc.getWindow().getScaledHeight()-yOffset;
        event.getContext().drawTextWithShadow(textRenderer, directionText, 5, dirY, argbColor);
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }

    private String getDirectionText() {
        int yaw = Math.floorMod((int) mc.player.getYaw(), 360);
        if (yaw >= 337.5 || yaw < 22.5) return "South (+Z)";
        else if (yaw >= 22.5 && yaw < 67.5) return "SouthWest (-X, +Z)";
        else if (yaw >= 67.5 && yaw < 112.5) return "West (-X)";
        else if (yaw >= 112.5 && yaw < 157.5) return "NorthWest (-X, -Z)";
        else if (yaw >= 157.5 && yaw < 202.5) return "North (-Z)";
        else if (yaw >= 202.5 && yaw < 247.5) return "NorthEast (+X, -Z)";
        else if (yaw >= 247.5 && yaw < 292.5) return "East (+X)";
        else if (yaw >= 292.5 && yaw < 337.5) return "SouthEast (+X, +Z)";
        else return "Unknown";
    }
}
