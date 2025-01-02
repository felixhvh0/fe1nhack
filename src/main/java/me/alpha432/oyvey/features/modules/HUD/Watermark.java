package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Watermark extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Setting<Boolean> showWatermark = this.register(new Setting<>("Watermark", true));
    public Setting<Watermark.watermarkMode> mode = this.register(new Setting("Mode", watermarkMode.fe1nh4ck));
    public Setting<Boolean> version = this.register(new Setting<>("Version", true));
    public Setting<Boolean> showUID = this.register(new Setting<>("UID", true));

    public Watermark() {
        super("Watermark", "", Category.HUD, true, false, false);
    }

    public enum watermarkMode {
        fe1nh4ck, jaxhax, KenCarson, TuPacHack, IRONMIKE, HawkTuahHack, NSOClient
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String playerName = mc.player.getName().getString();
        if (mc.player == null || !showWatermark.getValue()) return;
        String uid = "0";

        if (playerName.equals("clientpvp")) {
            uid = "❄";
        }
        if (playerName.equals("jaxui")) {
            uid = "☆ 1";
        }
        if (playerName.equals("_Onyx")) {
            uid = "5";
        }
        if (playerName.equals("Yaga419")) {
            uid = "420";
        }
        if (playerName.equals("rxxyv")) {
            uid = "☆ 2";
        }
        if (playerName.equals("Steffie678")) {
            uid = "☆ 3";
        }
        if (playerName.equals("vP0G")) {
            uid = "2";
        }
        if (playerName.equals("LonelyLodge")) {
            uid = "2";
        }
        if (playerName.equals("Lord_Vic")) {
            uid = "4";
        }
        if (playerName.equals("DigitalMiscreant")) {
            uid = "Δ";
        }



        TextRenderer textRenderer = mc.textRenderer;
        int watermarkX = 2;
        int watermarkY = 2;
        int textHeight = 10;
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        String watermarkText = version.getValue() ? mode.getValue() + " beta (" + OyVey.VERSION +")" : mode.getValue().toString();
        event.getContext().drawTextWithShadow(textRenderer, watermarkText, watermarkX, watermarkY, argbColor);

        if (showUID.getValue()) {

            String uidText = "UID "+uid;
            int uidY = watermarkY + textHeight;
            event.getContext().drawTextWithShadow(textRenderer, uidText, watermarkX, uidY, argbColor);
        }
    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
