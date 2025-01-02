package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.RotationManager;
import me.alpha432.oyvey.util.Rotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

public class PearlPhase extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int previousSlot;

    public PearlPhase() {
        super("PearlPhase", "", Category.MOVEMENT, true, false, false);
    }

    public Setting<Boolean> swing = this.register(new Setting<>("Swing",true));
    public Setting<Integer> pitch = this.register(new Setting<>("Pitch", 86, 70, 90));

    @Override
    public void onEnable() {
        previousSlot = mc.player.getInventory().selectedSlot;



        int pearlSlot = getEnderPearlSlot();
        if (pearlSlot != -1) {
            // Swap to the Ender Pearl slot
            mc.player.getInventory().selectedSlot = pearlSlot;
            RotationManager.setRotationSilent(mc.player.getYaw(),pitch.getValue(),true);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            if (swing.getValue()) {
                mc.player.swingHand(Hand.MAIN_HAND);
            }
            mc.player.getInventory().selectedSlot = previousSlot;
            // Return pitch later
            //mc.player.setPitch(returnpitch);
            this.disable();
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        // No rendering needed for this module
    }

    // Helper method to find the Ender Pearl slot
    private int getEnderPearlSlot() {
        for (int i = 0; i < mc.player.getInventory().main.size(); i++) {
            if (mc.player.getInventory().main.get(i).getItem() == Items.ENDER_PEARL) {
                return i;
            }
        }
        return -1; // Return -1 if Ender Pearl is not in the inventory
    }
}
