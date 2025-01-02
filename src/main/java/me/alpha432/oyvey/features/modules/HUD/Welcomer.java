package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class Welcomer extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Setting<String> customWelcomer = this.register(new Setting<>("WelcomerPrefix", "welcome to fe1nh4ck,"));


    public Welcomer() {
        super("Welcomer", "", Category.HUD, true, false, false);
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

        String playerName = mc.player.getName().getString();
        String welcomeMessage = customWelcomer.getValue() + " " + playerName + " :^)";
        int x = mc.getWindow().getScaledWidth() / 2 - (textRenderer.getWidth(welcomeMessage)/2) ;
        event.getContext().drawTextWithShadow(textRenderer, Text.literal(welcomeMessage),  x, 2, argbColor);
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
