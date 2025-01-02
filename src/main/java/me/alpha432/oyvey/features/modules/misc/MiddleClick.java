package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.InventoryManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class MiddleClick extends Module {
    private boolean pressed;
    public MiddleClick() {
        super("MiddleClick", "Doesn't pick block", Category.MISC, true, false, false);
    }

    public final Setting<Boolean> friend = register(new Setting("Friend", true));
    public final Setting<Boolean> pearl = register(new Setting("Pearl", true));
    public final Setting<Boolean> firework = register(new Setting("Firework", true));

    @Override public void onTick() {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 2) == 1) {
            if (!pressed) {
                Entity targetedEntity = mc.targetedEntity;
                if (mc.player.isFallFlying() && firework.getValue()) {
                    int rocketSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = mc.player.getInventory().getStack(i);
                        if (stack.getItem() instanceof FireworkRocketItem) {
                            rocketSlot = i;
                            break;
                        }
                    }
                    InventoryManager.setSlot(rocketSlot);
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    InventoryManager.syncToClient();
                } else if (targetedEntity instanceof PlayerEntity && friend.getValue()) {
                    String name = ((PlayerEntity) targetedEntity).getGameProfile().getName();

                    if (OyVey.friendManager.isFriend(name)) {
                        OyVey.friendManager.removeFriend(name);
                        Command.sendMessage(Formatting.RED + name + Formatting.RED + " has been unfriended.");
                    } else {
                        OyVey.friendManager.addFriend(name);
                        Command.sendMessage(Formatting.AQUA + name + Formatting.AQUA + " has been friended.");
                    }
                } else if (pearl.getValue()) {
                    int pearlSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        assert mc.player != null;
                        ItemStack stack = mc.player.getInventory().getStack(i);
                        if (stack.getItem() instanceof EnderPearlItem) {
                            pearlSlot = i;
                            break;
                        }
                    }
                    if (!(pearlSlot == -1 || mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL))) {
                        InventoryManager.setSlot(pearlSlot);
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        InventoryManager.syncToClient();
                    }
                }

                pressed = true;
            }
            //friend.getValue()

        } else {
            pressed = false;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
