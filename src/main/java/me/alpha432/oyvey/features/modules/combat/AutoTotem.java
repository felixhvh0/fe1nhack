package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

import java.text.Normalizer;

public class AutoTotem extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean noTotemMessageSent = false;
    private final Setting<Boolean> useGappleMode = this.register(new Setting<>("OffhandGap", true, "Enable Gapple swapping mode."));
    private final Setting<Float> safehealth = this.register(new Setting<>("SafeHealth", 18f, 0f, 20f, "Health threshold to switch to Totem."));
    private final Setting<Boolean> debug = this.register(new Setting<>("Debug", false, "Print debug messages to chat."));
    public AutoTotem() {
        super("AutoTotem", "", Category.COMBAT, true, false, true);
    }

    @Override
    public void onUpdate() {
        if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            ClientPlayerEntity player = mc.player;
            if (player == null) return;
            ItemStack offhandItem = player.getEquippedStack(EquipmentSlot.OFFHAND);
            // Handle both swords in one logic branch
            if (mc.player.getMainHandStack().getItem() == Items.DIAMOND_SWORD || mc.player.getMainHandStack().getItem() == Items.NETHERITE_SWORD) {
                if (mc.options.useKey.isPressed() && useGappleMode.getValue() && mc.player.getHealth() >= safehealth.getValue() && mc.player.getMainHandStack().getItem() != Items.ENCHANTED_GOLDEN_APPLE) {
                    swapToItem(Items.ENCHANTED_GOLDEN_APPLE);
                    noTotemMessageSent = true;
                } else {
                    if (offhandItem.getItem() == Items.TOTEM_OF_UNDYING) {
                        noTotemMessageSent = false; // Reset no totem message sent
                        return;
                    }
                    if (!(mc.player.getInventory().offHand.contains(Items.TOTEM_OF_UNDYING))) {
                        swapToItem(Items.TOTEM_OF_UNDYING);
                    }
                }
            } else if (!(mc.player.getInventory().offHand.contains(Items.TOTEM_OF_UNDYING))) {
                swapToItem(Items.TOTEM_OF_UNDYING);
            }
        }
    }
    @Override
    public void onRender2D(Render2DEvent event) {
    }
    private void swapToItem(net.minecraft.item.Item item) {
        int itemSlot = findItemSlot(item);
        if (itemSlot != -1) {
            quickSwapToOffhand(itemSlot);
            if (debug.getValue()) {
                Command.sendRawMessage("[AutoTotem] " + Formatting.GRAY + "Offhand has " + Formatting.GOLD + item.getName().getString());
            }
            noTotemMessageSent = false;
        } else if (!noTotemMessageSent) {
            if (debug.getValue()) {
                Command.sendRawMessage("[AutoTotem] " + Formatting.GRAY + "you aint got a  " + Formatting.GOLD + item.getName().getString() + Formatting.GRAY + " lol");
            }
            noTotemMessageSent = true;
        }
    }
    private int findItemSlot(net.minecraft.item.Item item) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i < 9 ? i + 36 : i;
            }
        }
        return -1;
    }
    private void quickSwapToOffhand(int slot) {
        if (slot != -1) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
        }
    }
}
