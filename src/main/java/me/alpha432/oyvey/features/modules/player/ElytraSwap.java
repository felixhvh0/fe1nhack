package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.manager.InventoryManager;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ElytraSwap extends Module {

    public ElytraSwap() {
        super("ChestSwap", "Swaps Chestplate and Elytra", Category.PLAYER, true, false, true);
    }

    private boolean inHotbar;

    @Override public void onEnable() {
        Item chestPiece;
        if (!(mc.player.getInventory().getArmorStack(2).getItem() instanceof ElytraItem)) {
            chestPiece = Items.ELYTRA;
        } else {chestPiece = Items.NETHERITE_CHESTPLATE;}

        int elytraSlot = -1;
        for(int i = 0; i <= 44; i++) {
            assert mc.player != null;
            Item item = mc.player.getInventory().getStack(i).getItem();
            if (item == chestPiece || (chestPiece == Items.NETHERITE_CHESTPLATE && item == Items.DIAMOND_CHESTPLATE)) {
                if (i < 9) {
                    inHotbar = true;
                }
                else {
                    inHotbar = false;
                }
                elytraSlot = i;
            }
        }
        if (inHotbar) {
            InventoryManager.setSlot(elytraSlot);
            //final SequencedPacketCreator o = id -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, id, mc.player.getYaw(), mc.player.getPitch());
            //NetworkManager.sendSequencedPacket(o);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            InventoryManager.syncToClient();
        }
        else {
            ItemStack elytraStack = mc.player.getInventory().getArmorStack(2);

            InventoryManager.pickupSlot(elytraSlot);
            boolean rt = !elytraStack.isEmpty();
            InventoryManager.pickupSlot(6);
            if (rt) {
                InventoryManager.pickupSlot(elytraSlot);
            }
        }
        toggle();
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

}

