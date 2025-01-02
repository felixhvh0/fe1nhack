package me.alpha432.oyvey.manager;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;


public class InventoryManager implements Util {

    static MinecraftClient mc = MinecraftClient.getInstance();


    private static int slot;

    private static final Set<Packet<?>> PACKET_CACHE = new HashSet<>();

    public static void setSlot(final int barSlot) {
        if (slot != barSlot && PlayerInventory.isValidHotbarIndex(barSlot)) {
            setSlotForced(barSlot);
        }
    }

    public static FindItemResult find(Predicate<ItemStack> isGood) {
        if (mc.player == null) return new FindItemResult(0, 0);
        return find(isGood, 0, mc.player.getInventory().size());
    }

    public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
        if (mc.player == null) return new FindItemResult(0, 0);

        int slot = -1, count = 0;

        for (int i = start; i <= end; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (isGood.test(stack)) {
                if (slot == -1) slot = i;
                count += stack.getCount();
            }
        }

        return new FindItemResult(slot, count);
    }

    public static void syncToClient() {
        if (isDesynced()) {
            setSlotForced(mc.player.getInventory().selectedSlot);
        }
    }

    public static boolean isDesynced()
    {
        return mc.player.getInventory().selectedSlot != slot;
    }

    public static void setSlotForced(final int barSlot) {

        final Packet<?> p = new UpdateSelectedSlotC2SPacket(barSlot);
        if (mc.getNetworkHandler() != null) {
            PACKET_CACHE.add(p);
            mc.getNetworkHandler().sendPacket(p);
        }
    }

    private static void click(int slot, int button, SlotActionType type) {
        ScreenHandler screenHandler = mc.player.currentScreenHandler;
        DefaultedList<Slot> defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot1 : defaultedList) {
            list.add(slot1.getStack().copy());
        }
        screenHandler.onSlotClick(slot, button, type, mc.player);
        Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = list.get(j);
            if (ItemStack.areEqual(itemStack, itemStack2 = defaultedList.get(j).getStack())) continue;
            int2ObjectMap.put(j, itemStack2.copy());
        }
        mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), int2ObjectMap));
    }

    public static void pickupSlot(final int slot) {
        click(slot, 0, SlotActionType.PICKUP);
    }

    public static int getBestTool(BlockState state) {
        int slot = getBestToolNoFallback(state);
        return slot != -1 ? slot : mc.player.getInventory().selectedSlot;
    }

    public static int getBestToolNoFallback(BlockState state) {
        int slot = -1;
        float bestTool = 0.0F;

        for(int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ToolItem) {
                float speed = stack.getMiningSpeedMultiplier(state);
                int efficiency = EnchantmentHelper.getLevel(mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get(), stack);
                if (efficiency > 0) {
                    speed += (float)(efficiency * efficiency) + 1.0F;
                }

                if (speed > bestTool) {
                    bestTool = speed;
                    slot = i;
                }
            }
        }

        return slot;
    }


}
