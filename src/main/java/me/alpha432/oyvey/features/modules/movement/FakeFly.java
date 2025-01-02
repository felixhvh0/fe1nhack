package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.EventListener;
import me.alpha432.oyvey.event.impl.TickEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.FindItemResult;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.manager.InventoryManager;
import me.alpha432.oyvey.util.InvUtils;
import me.alpha432.oyvey.event.impl.PlayerMoveEvent;
import me.alpha432.oyvey.mixin.IVec3d;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class FakeFly extends Module {

    public FakeFly() {
        super("FakeFly", "", Category.MOVEMENT, true, false, false);
    }

    public enum Mode {
        Chestplate,
        ELYTRA
    }

    private final Setting<FakeFly.Mode> mode = this.register(new Setting<>("Mode", Mode.Chestplate));
    public Setting<Float> fireworkDelay = this.register(new Setting<>("FireworkDelay", 2.1f, 0f, 5f));
    public Setting<Float> horizontalSpeed = this.register(new Setting<>("HorizontalSpeed", 50f, 0f, 100f));
    public Setting<Float> verticalSpeed = this.register(new Setting<>("VerticalSpeed", 30f, 0f, 100f));
    public Setting<Float> accelTime = this.register(new Setting<>("AccelTime", 0.2f, 0.001f, 2f));

    private int fireworkTicksLeft = 0;
    private boolean needsFirework = false;
    private Vec3d currentVelocity = Vec3d.ZERO;
    private InventorySlotSwap slotSwap = null;

    @Override
    public void onEnable() {
        needsFirework = true;
        currentVelocity = mc.player.getVelocity();
        mc.player.jump();
        mc.player.setOnGround(false);
    }

    @Override
    public void onDisable() {
        equipChestplate(slotSwap);
        mc.player.setSneaking(false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    @EventHandler
    public void onUpdate() {
        boolean isUsingFirework = getIsUsingFirework();
        if (!isUsingFirework && !InvUtils.find(Items.FIREWORK_ROCKET).found()) {
            return;
        }

        Vec3d desiredVelocity = new Vec3d(0, 0, 0);

        double yaw = Math.toRadians(mc.player.getYaw());
        double pitch = Math.toRadians(mc.player.getPitch());
        Vec3d direction = new Vec3d(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch)).normalize();

        if (mc.options.forwardKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(direction.multiply(horizontalSpeed.getValue() / 20, 0, horizontalSpeed.getValue() / 20));
        }
        if (mc.options.backKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(direction.multiply(0-horizontalSpeed.getValue() / 20, 0, 0-horizontalSpeed.getValue() / 20));
        }
        if (mc.options.leftKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(direction.multiply(horizontalSpeed.getValue() / 20, 0, horizontalSpeed.getValue() / 20)
                    .rotateY(90));
        }
        if (mc.options.rightKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(direction.multiply(horizontalSpeed.getValue() / 20, 0, horizontalSpeed.getValue() / 20)
                    .rotateY(-90));
        }
        if (mc.options.jumpKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(0, verticalSpeed.getValue() / 20, 0);
        }
        if (mc.options.sneakKey.isPressed()) {
            desiredVelocity = desiredVelocity.add(0, -verticalSpeed.getValue() / 20, 0);
        }



        double actualAccelTime = accelTime.getValue();
        if (!isUsingFirework) {
            desiredVelocity = Vec3d.ZERO;
            actualAccelTime = 2.0;
        }

        // Smooth velocity interpolation
        currentVelocity = currentVelocity.add(desiredVelocity.subtract(currentVelocity).multiply(1.0 / (actualAccelTime * 20)));

        // Ensure velocity doesn't exceed the limit
        if (currentVelocity.horizontalLengthSquared() > Math.pow(horizontalSpeed.getValue() / 20, 2)) {
            currentVelocity = currentVelocity.normalize().multiply(horizontalSpeed.getValue() / 20);
        }

        mc.player.setVelocity(currentVelocity);

        if (fireworkTicksLeft < ((int) (fireworkDelay.getValue() * 20.0) - 3) && fireworkTicksLeft > 3 && !isUsingFirework) {
            fireworkTicksLeft = 0;
        }

        slotSwap = equipElytra();
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

        if (fireworkTicksLeft <= 0) {
            needsFirework = true;
        }

        if (needsFirework) {
            useFirework();
            needsFirework = false;
        }

        if (fireworkTicksLeft >= 0) {
            fireworkTicksLeft--;
        }

        if (mode.getValue() == Mode.Chestplate) {
            equipChestplate(slotSwap);
            slotSwap = null;
        }
    }

    @EventListener
    public void onUpdate(PlayerMoveEvent event) {
        if (!isEnabled()) {
            return;
        }

        if (!getIsUsingFirework() && !InvUtils.find(Items.FIREWORK_ROCKET).found()) {
            return;
        }

        Vec3d newMovement = currentVelocity;
        ((IVec3d) event.movement).set(newMovement.x, newMovement.y, newMovement.z);
    }

    private boolean getIsUsingFirework() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof FireworkRocketEntity firework) {
                if (firework.getOwner() != null && firework.getOwner().equals(mc.player)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void equipChestplate(InventorySlotSwap slotSwap) {
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.DIAMOND_CHESTPLATE) || mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.NETHERITE_CHESTPLATE)) {
            return;
        }

        FindItemResult result = InvUtils.findInHotbar(Items.NETHERITE_CHESTPLATE);
        if (!result.found()) {
            result = InvUtils.findInHotbar(Items.DIAMOND_CHESTPLATE);
        }

        if (result.found()) {
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, result.slot(), SlotActionType.SWAP, mc.player);

            if (slotSwap != null) {
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slotSwap.inventorySlot, result.slot(), SlotActionType.SWAP, mc.player);
            }
            return;
        }

        result = InvUtils.find(Items.NETHERITE_CHESTPLATE);
        if (!result.found()) {
            result = InvUtils.find(Items.DIAMOND_CHESTPLATE);
        }

        if (result.found()) {
            InvUtils.move().from(result.slot()).toArmor(2);
        }
    }

    public InventorySlotSwap equipElytra() {
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
            return null;
        }

        FindItemResult result = InvUtils.findInHotbar(Items.ELYTRA);

        if (result.found()) {
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 6, result.slot(), SlotActionType.SWAP, mc.player);
            return null;
        }

        result = InvUtils.find(Items.ELYTRA);

        if (!result.found()) {
            return null;
        }

        FindItemResult hotbarSlot = InvUtils.findInHotbar(x -> x.getItem() != Items.TOTEM_OF_UNDYING);

        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, hotbarSlot.slot(), 6, SlotActionType.SWAP, mc.player);

        InvUtils.move().from(result.slot()).toArmor(2);

        return new InventorySlotSwap(hotbarSlot.slot(), result.slot());
    }

    public void useFirework() {
        FindItemResult fireworkSlot = InvUtils.findInHotbar(Items.FIREWORK_ROCKET);

        if (!fireworkSlot.found()) {
            fireworkSlot = InvUtils.find(Items.FIREWORK_ROCKET);
            if (fireworkSlot.found()) {
                InvUtils.move().from(fireworkSlot.slot()).toHotbar(0);
                fireworkSlot = InvUtils.findInHotbar(Items.FIREWORK_ROCKET);
            }
        }

        if (fireworkSlot.found()) {
            mc.player.getInventory().selectedSlot = fireworkSlot.slot();
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            fireworkTicksLeft = (int) (fireworkDelay.getValue() * 20.0);
        }
    }



    public class InventorySlotSwap {
        int hotbarSlot;
        int inventorySlot;

        public InventorySlotSwap(int hotbarSlot, int inventorySlot) {
            this.hotbarSlot = hotbarSlot;
            this.inventorySlot = inventorySlot;
        }
    }
}
