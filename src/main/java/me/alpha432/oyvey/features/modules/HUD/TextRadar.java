package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.FriendManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class TextRadar extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final FriendManager friendManager = new FriendManager();

    public TextRadar() {
        super("TextRadar", "", Category.HUD, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (mc.world == null || mc.player == null) return;
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
        TextRenderer textRenderer = mc.textRenderer;
        int screenWidth = mc.getWindow().getWidth();
        int screenHeight = mc.getWindow().getHeight();

        // Radar settings
        float radarX = 2;
        float radarY = mc.getWindow().getScaledHeight() / 6;
        int yOffset = 0;

        // Draw the "TextRadar" label
        String radarLabel = "TextRadar:";
        event.getContext().drawTextWithShadow(textRenderer, Text.literal(radarLabel), Math.round(radarX), Math.round(radarY), argbColor);

        // Increase the Y offset to position player names below the label
        yOffset += 15;

        // Iterate through all players in the world
        for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
            if (entity == mc.player) continue; // Skip drawing the player's own name

            String playerName = entity.getName().getString();
            int color;

            // Check if the player is a friend and set color accordingly
            if (friendManager.isFriend(playerName)) {
                color = argbColor; // Aqua color for friends
            } else {
            }

            // Draw player name in radar with appropriate color
            event.getContext().drawTextWithShadow(textRenderer, Text.literal(playerName), Math.round(radarX), Math.round(radarY + yOffset), argbColor);
                color = argbColor; // White color for other players);
            yOffset += 12; // Increase Y offset for next player name

            // Prevent excessive crowding
            if (yOffset > screenHeight - 100) {
                break; // Limit the number of players shown
            }
        }
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
