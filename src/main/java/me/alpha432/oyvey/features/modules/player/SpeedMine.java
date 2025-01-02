package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.EventListener;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.manager.InventoryManager;
import me.alpha432.oyvey.mixin.accessor.IInteractionManager;
import me.alpha432.oyvey.util.InteractionUtil;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.InvUtils;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SpeedMine extends Module {
    private static SpeedMine INSTANCE = new SpeedMine();
    public static SpeedMine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpeedMine();
        }
        return INSTANCE;
    }

    private BlockPos attackedBlock = null;
    private BlockPos secondAttackedBlock = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentTask = null;
    private long startTime = 0;
    private long breakingTime = 0;

    public enum SwapMode {
        Silent,
        Normal
    }

    public enum Mode {
        Packet,
        Damage
    }

    public Setting<SpeedMine.Mode> mode = this.register(new Setting<>("Mode", Mode.Packet));
    private final Setting<Boolean> grimMode = this.register(new Setting<>("Grim", true, v -> this.mode.getValue() == Mode.Packet));
    private final Setting<Float> factor = this.register(new Setting<>("Factor", 0.7f, 0.7f, 1.0f, v -> this.grimMode.getValue() && this.mode.getValue() == Mode.Packet));
    private final Setting<Boolean> swap = this.register(new Setting<>("Swap", true, v -> this.mode.getValue() == Mode.Packet));
    private final Setting<SpeedMine.SwapMode> swapMode = this.register(new Setting<>("SwapMode", SwapMode.Silent, v -> this.swap.getValue() && this.mode.getValue() == Mode.Packet));
    private final Setting<Float> damageSpeed = this.register(new Setting<>("DamageSpeed", 0.5f, 0f, 1f, v -> this.mode.getValue() == Mode.Damage));
    private final Setting<Boolean> doubleMine = this.register(new Setting<>("DoubleMine", true, v -> this.grimMode.getValue()));
    public SpeedMine() {
        super("SpeedMine", "Brrrr mine -jaxui", Category.PLAYER, true, false, false);
    }

    public void setTarget(BlockPos targetPos) {
        if (mc.world == null || mc.player == null) return;

        if (mc.world.getBlockState(targetPos).getBlock() != Blocks.AIR && mc.world.getBlockState(targetPos).getBlock() != Blocks.BEDROCK) {
            sendMinePackets(targetPos);
        }

        if (attackedBlock != null && mc.world.getBlockState(attackedBlock).getBlock() == Blocks.AIR) {
            attackedBlock = null;
        }
    }

    public void setSecondTarget(BlockPos targetPos2) {
        if (doubleMine.getValue()) {
            if (mc.world == null || mc.player == null || targetPos2.equals(attackedBlock)) return;

            if (mc.world.getBlockState(targetPos2).getBlock() != Blocks.AIR && mc.world.getBlockState(targetPos2).getBlock() != Blocks.BEDROCK) {
                sendMinePackets(targetPos2);
            }

            if (secondAttackedBlock != null && mc.world.getBlockState(secondAttackedBlock).getBlock() == Blocks.AIR) {
                secondAttackedBlock = null;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (Mode.Packet.equals(mode.getValue())) {
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                BlockPos targetPos = ((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos();
                setTarget(targetPos);

                // Find a secondary target (example logic: a nearby block)
                if (((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos() != targetPos) {
                    BlockPos secondTargetPos = ((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos();
                    setSecondTarget(secondTargetPos);
                }
            }
        }

        if (Mode.Damage.equals(mode.getValue())) {
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                BlockPos attackedBlock = ((net.minecraft.util.hit.BlockHitResult) mc.crosshairTarget).getBlockPos();

                if (((IInteractionManager) mc.interactionManager).getCurBlockDamageMP() < damageSpeed.getValue()) {
                    ((IInteractionManager) mc.interactionManager).setCurBlockDamageMP(damageSpeed.getValue());
                    mc.world.setBlockBreakingInfo(mc.player.getId(), attackedBlock, -1);
                }
            }
        }
    }

    private void sendMinePackets(BlockPos pos) {
        if (mc.player == null) return;
        if (mc.options.attackKey.isPressed()) {
            if (!pos.equals(attackedBlock) && !pos.equals(secondAttackedBlock)) {
                if (attackedBlock == null) {
                    attackedBlock = pos;
                } else {
                    secondAttackedBlock = pos;
                }

                if (currentTask != null && !currentTask.isDone()) {
                    currentTask.cancel(true);
                }
            }

            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));
            int returnSlot = mc.player.getInventory().selectedSlot;
            float breakingSpeed = (float) InteractionUtil.getBlockBreakingSpeed(mc.player.getInventory().selectedSlot, pos);

            if (grimMode.getValue()) {
                breakingSpeed = breakingSpeed / factor.getValue();
            }

            float blockResistance = mc.world.getBlockState(pos).getHardness(mc.world, pos);

            if (breakingSpeed <= 0 || blockResistance <= 0) {
                return;
            }

            breakingTime = (long) ((blockResistance / breakingSpeed) * 2600);
            startTime = System.currentTimeMillis();


            currentTask = scheduler.schedule(() -> {
                mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));
                mc.player.swingHand(Hand.MAIN_HAND);

                if (swapMode.getValue() == SwapMode.Normal) {
                    InvUtils.swap(InventoryManager.getBestTool(mc.world.getBlockState(pos)), true);
                    InvUtils.swap(returnSlot, false);
                }
                if (swapMode.getValue() == SwapMode.Silent) {
                    InventoryManager.setSlot(InventoryManager.getBestTool(mc.world.getBlockState(pos)));
                    if (InventoryManager.isDesynced()) {
                        InvUtils.swap(returnSlot, false);
                    }
                }
            }, breakingTime, TimeUnit.MILLISECONDS);

            if (mc.player.getInventory().selectedSlot != returnSlot) {
                InvUtils.swap(returnSlot, false);
            }
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();

        if (attackedBlock != null && mc.world.getBlockState(attackedBlock).getBlock() != Blocks.AIR) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int alpha = (int) Math.min(80, (elapsedTime * 80) / breakingTime);
            RenderUtil.drawBoxFilled(event.getMatrixStack(), attackedBlock, new Color(red, green, blue, alpha));
            RenderUtil.drawBox(event.getMatrixStack(), attackedBlock, new Color(red, green, blue, 255), 1f);
        }

        if (secondAttackedBlock != null && mc.world.getBlockState(secondAttackedBlock).getBlock() != Blocks.AIR) {
            if (doubleMine.getValue()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int alpha = (int) Math.min(80, (elapsedTime * 80) / breakingTime);
                RenderUtil.drawBoxFilled(event.getMatrixStack(), secondAttackedBlock, new Color(red, green, blue, alpha));
                RenderUtil.drawBox(event.getMatrixStack(), secondAttackedBlock, new Color(red, green, blue, 255), 1f);
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }
}
