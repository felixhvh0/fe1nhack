package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.AbstractMap.SimpleEntry;


public class HudModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Settings for toggling different HUD elements
    public Setting<Boolean> arraylist = this.register(new Setting<>("ArrayList", false));
    public Setting<Boolean> showWatermark = this.register(new Setting<>("Watermark", true));
    public Setting<HudModule.watermarkMode> mode = this.register(new Setting("Mode", watermarkMode.fe1nh4ck));
    public Setting<Boolean> version = this.register(new Setting<>("Version", true));
    public Setting<Boolean> showUID = this.register(new Setting<>("UID", true));
    public Setting<Boolean> radar = this.register(new Setting<>("TextRadar", true));
    public Setting<Float> radarY = this.register(new Setting<>("RadarY", 100f, 2f, 750f));  // Slider for radar Y position
    public Setting<Boolean> showWelcomer = this.register(new Setting<>("Welcomer", true));
    public Setting<String> customWelcomer = this.register(new Setting<>("WelcomerPrefix", "welcome to fe!nh4ck,"));
    public Setting<Float> welcomerX = this.register(new Setting<>("WelcomerX", 550f, 2f, 1400f));
    public Setting<Boolean> showCoordinates = this.register(new Setting<>("Coordinates", true)); // New setting to toggle coordinates
    public Setting<Boolean> showOppositeCoords = this.register(new Setting<>("OppositeDimension", true)); // New setting to toggle opposite dimension coords
    public Setting<Float> coordsY = this.register(new Setting<>("CoordsY", 675f, 2f, 750f));
    public Setting<Boolean> direction = this.register(new Setting<>("Direction", true));
    public Setting<Boolean> metrics = this.register(new Setting<>("Metrics", true)); // New setting for metrics display
    public Setting<Float> metricsX = this.register(new Setting<>("MetricsX", 1275f, 2f, 1400f));
    public Setting<Float> metricsY = this.register(new Setting<>("MetricsY", 675f, 2f, 750f));

    public HudModule() {
        super("Hud", "Displays HUD elements", Category.CLIENT, true, true, false);
    }

    public enum watermarkMode {
        fe1nh4ck,
        jaxhax,
        KenCarson,
        TuPacHack,
        IRONMIKE,
        HawkTuahHack
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null) return;

        // Get text renderer
        TextRenderer textRenderer = mc.textRenderer;

        // Screen dimensions
        int screenWidth = mc.getWindow().getWidth();
        int screenHeight = mc.getWindow().getHeight();

        // Calculate text height (using a fixed height for simplicity)
        int textHeight = 10; // Approximate height for the standard font

        // Color settings (example color values, modify as needed)
        int red = ClickGui.getInstance().red.getValue(), green = ClickGui.getInstance().green.getValue(), blue = ClickGui.getInstance().blue.getValue(), alpha = 255; // Import global color
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Welcomer text (fixed at 600px from the left)
        if (showWelcomer.getValue()) {
            String playerName = mc.player.getName().getString();
            String welcomeMessage = customWelcomer.getValue() + " " + playerName + " :^)";
            event.getContext().drawTextWithShadow(
                    textRenderer,
                    Text.literal(welcomeMessage), // Convert String to Text
                    Math.round(welcomerX.getValue()), 1, // Round the X position and cast to int
                    argbColor
            );
        }

        // Watermark text (top-left corner)
        if (showWatermark.getValue()) {
            int watermarkX = 2;
            int watermarkY = 2;

            if (version.getValue()) {
                String watermarkText = mode.getValue() + " " + OyVey.VERSION;
                event.getContext().drawTextWithShadow(textRenderer, watermarkText, watermarkX, watermarkY, argbColor);

                // UID text (3px below watermark, closer gap)
                if (showUID.getValue()) {
                    String uidText = "UID 0";
                    int uidY = watermarkY + textHeight;  // Reduced gap (3px below watermark)
                    event.getContext().drawTextWithShadow(textRenderer, uidText, watermarkX, uidY, argbColor);
                }
            } else {
                String watermarkText = mode.getValue().toString();
                event.getContext().drawTextWithShadow(textRenderer, watermarkText, watermarkX, watermarkY, argbColor);

                if (showUID.getValue()) {
                    String uidText = "UID 0";
                    int uidY = watermarkY + textHeight;  // Reduced gap (3px below watermark)
                    event.getContext().drawTextWithShadow(textRenderer, uidText, watermarkX, uidY, argbColor);
                }
            }
        }

        // Text Radar
        if (radar.getValue()) {
            // Initialize radar text position
            int radarX = 2;
            int radarYPosition = Math.round(radarY.getValue());  // Get the Y position from the radarY setting

            // Draw the "TextRadar:" label at the radar position
            String radarLabel = "TextRadar";
            event.getContext().drawTextWithShadow(textRenderer, Text.literal(radarLabel), radarX, radarYPosition, argbColor);

            // Increase Y position to start drawing player names below the label
            int offsetY = radarYPosition + 15;  // Start drawing player names just below the label

            // Iterate through all players in the world
            for (AbstractClientPlayerEntity entity : mc.world.getPlayers()) {
                if (entity == mc.player) continue; // Skip drawing the player's own name

                // Get the player's name
                String playerName = entity.getName().getString();

                // Draw the player's name on the radar
                event.getContext().drawTextWithShadow(textRenderer, Text.literal(playerName), radarX, offsetY, argbColor);

                // Increase Y position for the next player
                offsetY += 12;  // Add some vertical space between the player names

                // Prevent excessive crowding by limiting how many names are drawn
                if (offsetY > radarYPosition + 100) break;  // You can adjust this limit as needed
            }
        }

        // Coordinates display logic (only if showCoordinates is enabled)
        if (showCoordinates.getValue()) {
            // Get the current dimension
            RegistryKey<World> currentWorldKey = mc.world.getRegistryKey();

            // Get the current coordinates (current dimension)
            String currentCoordsText = "XYZ: " + (int) mc.player.getX() + " " + (int) mc.player.getY() + " " + (int) mc.player.getZ();

            // Calculate the opposite dimension coordinates (excluding Y-coordinate)
            String oppositeCoordsText = "";

            if (currentWorldKey == World.OVERWORLD) {
                // Opposite is the Nether, coordinates are divided by 8 (excluding Y)
                oppositeCoordsText = "  [" + (int) (mc.player.getX() / 8) + " " + (int) (mc.player.getZ() / 8) + "]";
            } else if (currentWorldKey == World.NETHER) {
                // Opposite is the Overworld, coordinates are multiplied by 8 (excluding Y)
                oppositeCoordsText = " [" + (int) (mc.player.getX() * 8) + " " + (int) (mc.player.getZ() * 8) + "]";
            } else if (currentWorldKey == World.END) {
                // Opposite is the Overworld, coordinates are multiplied by 8 (excluding Y)
                oppositeCoordsText = " [" + (int) (mc.player.getX() * 8) + " " + (int) (mc.player.getZ() * 8) + "]";
            }

            // Combine the coordinates text
            String coordsText = currentCoordsText;
            // Display opposite dimension coordinates if showOppositeCoords is enabled
            if (showOppositeCoords.getValue()) {
                coordsText += oppositeCoordsText;
            }
            int cordsY = Math.round(coordsY.getValue());
            // Calculate the X position for the coordinates text
            int coordsWidth = textRenderer.getWidth(coordsText);
            int coordsX = 5;  // 5px from the left edge
            event.getContext().drawTextWithShadow(textRenderer, coordsText, coordsX, cordsY, argbColor);
        }

        // Direction text (5px from the left edge, 10px above coordinates)
        if (direction.getValue()) {
            String directionText = getDirectionText();
            int dirY = Math.round(coordsY.getValue()) - 10;
            event.getContext().drawTextWithShadow(textRenderer, directionText, 5, dirY, argbColor);
        }

        // Metrics display (bottom-right corner, rendering from right to left, sorted by string length)
        if (metrics.getValue()) {
            // Retrieve metrics values
            int fps = Integer.parseInt(mc.fpsDebugString.split(" ", 2)[0]);
            int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
            double posX = mc.player.getX();
            double posZ = mc.player.getZ();
            double lastPosX = mc.player.prevX;
            double lastPosZ = mc.player.prevZ;
            double speed = Math.sqrt((posX - lastPosX) * (posX - lastPosX) + (posZ - lastPosZ) * (posZ - lastPosZ)) * 20;

            // Position settings for metrics display
            float metricsX = this.metricsX.getValue();
            float metricsY = this.metricsY.getValue();

            // Create metric strings
            String speedText = String.format("Speed: %.2f b/s", speed);
            String pingText = "Ping: " + ping + "ms";
            String fpsText = "FPS: " + fps;

            // Calculate text lengths
            int speedLength = speedText.length();
            int pingLength = pingText.length();
            int fpsLength = fpsText.length();

            // Prepare the metrics as a list of SimpleEntry (text, length)
            List<SimpleEntry<String, Integer>> metricsList = new ArrayList<>();
            metricsList.add(new SimpleEntry<>(speedText, speedLength));
            metricsList.add(new SimpleEntry<>(pingText, pingLength));
            metricsList.add(new SimpleEntry<>(fpsText, fpsLength));

            // Sort the metrics by string length (ascending order)
            metricsList.sort(Comparator.comparingInt(SimpleEntry::getValue));

            // Render each metric based on the sorted order
            int yOffset = 0; // Start from the top
            for (SimpleEntry<String, Integer> metric : metricsList) {
                String metricText = metric.getKey();

                // Render the text with the proper alignment and vertical spacing
                event.getContext().drawTextWithShadow(
                        textRenderer,
                        Text.literal(metricText),
                        Math.round(metricsX - textRenderer.getWidth(metricText)), // Right-align the text
                        Math.round(metricsY + yOffset), // Vertical positioning
                        argbColor
                );

                // Increment yOffset to space out the metrics
                yOffset += textHeight;
            }
        }



    }

    // Method to get the direction text based on the player's yaw
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

    // Additional onRender2D method for any extra customization
    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {
        // Additional rendering logic (if needed)
    }
}
