package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.ICancellable;
import net.minecraft.item.ItemStack;

public class DropItemsEvent implements ICancellable {
    private static final DropItemsEvent INSTANCE = new DropItemsEvent();

    public ItemStack itemStack;

    public static DropItemsEvent get(ItemStack itemStack) {
        INSTANCE.setCancelled(false);
        INSTANCE.itemStack = itemStack;
        return INSTANCE;
    }

    @Override
    public void setCancelled(boolean cancelled) {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}