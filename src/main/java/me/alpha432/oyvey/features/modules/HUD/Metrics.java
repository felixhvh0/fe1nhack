package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class Metrics extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Metrics() {
        super("Metrics", "", Category.HUD, true, false, false);
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

        int fps = Integer.parseInt(mc.fpsDebugString.split(" ", 2)[0]);
        int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
        double posX = mc.player.getX();
        double posZ = mc.player.getZ();
        double lastPosX = mc.player.prevX;
        double lastPosZ = mc.player.prevZ;
        double speed = Math.sqrt((posX - lastPosX) * (posX - lastPosX) + (posZ - lastPosZ) * (posZ - lastPosZ)) * 20;

        float metricsX = mc.getWindow().getScaledWidth()-2;
        float metricsY = mc.getWindow().getScaledHeight()-30;

        String speedText = String.format("Speed: %.2f b/s", speed);
        String pingText = "Ping: " + ping + "ms";
        String fpsText = fps+" FPS";

        List<SimpleEntry<String, Integer>> metricsList = new ArrayList<>();
        metricsList.add(new SimpleEntry<>(speedText, speedText.length()));
        metricsList.add(new SimpleEntry<>(pingText, pingText.length()));
        metricsList.add(new SimpleEntry<>(fpsText, fpsText.length()));

        metricsList.sort(Comparator.comparingInt(SimpleEntry::getValue));

        int textHeight = 10;
        int yOffset = 0;
        for (SimpleEntry<String, Integer> metric : metricsList) {
            String metricText = metric.getKey();
            event.getContext().drawTextWithShadow(
                    textRenderer,
                    Text.literal(metricText),
                    Math.round(metricsX - textRenderer.getWidth(metricText)),
                    Math.round(metricsY+ yOffset),
                    argbColor
            );
            yOffset += textHeight;
        }
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
