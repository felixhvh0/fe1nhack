package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.List;
import java.util.stream.Collectors;

public class Arraylist extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();



    public Arraylist() {
        super("Arraylist", "Displays enabled modules", Category.HUD, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        TextRenderer textRenderer = mc.textRenderer;

        // Get the X and Y positions from the settings
        float x = 2f;
        float y = 2f;

        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Sort the enabled modules by the visual width of their names in descending order
        List<Module> sortedModules = Module.enabledModules.stream()
                .filter(module -> module.drawn.getValue()) // Only include drawn modules
                .sorted((module1, module2) -> {
                    int width1 = textRenderer.getWidth(module1.getDisplayName());
                    int width2 = textRenderer.getWidth(module2.getDisplayName());
                    return Integer.compare(width2, width1); // Sort by visual text width, descending
                })
                .collect(Collectors.toList());

        // Get the screen width to align text right to left
        int screenWidth = mc.getWindow().getScaledWidth();

        // Loop through the sorted list and render the module names from right to left
        for (Module module : sortedModules) {
            String text = module.getDisplayName();
            int textWidth = textRenderer.getWidth(text);

            // Calculate X position for right-to-left rendering
            float rightAlignedX = screenWidth - x - textWidth;

            // Draw the text for each module at the calculated position
            event.getContext().drawTextWithShadow(textRenderer, text, (int) rightAlignedX, (int) y, argbColor);
            y += mc.textRenderer.fontHeight + 2; // Increase the vertical position for the next module
        }
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}