package me.alpha432.oyvey.features.modules.HUD;

import me.alpha432.oyvey.event.impl.Render2DEvent;  // For handling render events
import me.alpha432.oyvey.features.modules.Module;  // Base class for modules
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;  // To register settings
import net.minecraft.client.MinecraftClient;  // For accessing Minecraft's client instance
import net.minecraft.item.ItemStack;  // For dealing with item stacks in the inventory
import net.minecraft.item.Items;  // Minecraft's Items class (for Totem of Undying)
import net.minecraft.client.font.TextRenderer;  // For rendering text to the screen

public class TotemHud extends Module {

    public Setting<Boolean> whitecolor = this.register(new Setting<>("White", false));

    // Constructor to initialize the TotemHud module
    public TotemHud() {
        super("TotemHud", "", Category.HUD, true, false, true);
    }

    // Method that handles the 2D rendering of the Totem HUD
    public void onRender2D(Render2DEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();  // Get the Minecraft client instance
        int width = client.getWindow().getScaledWidth();  // Get the screen width
        int height = client.getWindow().getScaledHeight();  // Get the screen height

        // Count the total number of Totems of Undying in the player's inventory
        int totems = (int) client.player.getInventory().main.stream()
                .filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING)  // Check for Totem of Undying
                .mapToInt(ItemStack::getCount)  // Sum up the counts of Totems of Undying
                .sum();
        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += client.player.getOffHandStack().getCount();  // Add offhand totem count
        }

        // Render the Totem HUD if there are any Totems of Undying

            int x =  mc.getWindow().getScaledWidth()/2-10;  // X position of the totem icon, customizable
            int y = mc.getWindow().getScaledHeight()-50;  // Y position of the totem icon, customizable
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();
        int alpha = 255;
        int argbColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
            // Draw the totem icon (assumes `event.getContext().drawItem()` exists)
            event.getContext().drawItem(new ItemStack(Items.TOTEM_OF_UNDYING), x, y);

            // Draw the number of totems
            TextRenderer textRenderer = client.textRenderer;  // Get the font renderer
            String totemText = String.valueOf(totems);  // Convert totem count to a string
        if (whitecolor.getValue()) {
            event.getContext().drawTextWithShadow(textRenderer, totemText, x + 15, y + 9, 0xFFFFFF);
        } else {
            event.getContext().drawTextWithShadow(textRenderer, totemText, x + 15, y + 9, argbColor);
        }

    }

    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {

    }
}
