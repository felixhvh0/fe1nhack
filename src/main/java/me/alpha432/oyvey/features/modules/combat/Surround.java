package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.manager.FindItemResult;
import me.alpha432.oyvey.util.InvUtils;
import me.alpha432.oyvey.util.RotationUtil;
import me.alpha432.oyvey.features.modules.Module;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean isPlacing = false;

    public Surround() {
        super("Surround", "", Category.COMBAT, true, false, true);
    }

    public void onTick(MinecraftClient minecraftClient) {
        if (!isPlacing) {
            placeObsidianBlocks();
        }
    }

    private void placeObsidianBlocks() {
        if (mc.player == null) return;

        // Find obsidian in the player's inventory
        me.alpha432.oyvey.manager.FindItemResult result = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (result.slot() == -1) return; // No obsidian found

        // Loop through the directions to place obsidian blocks around the player
        BlockPos[] positions = {
                new BlockPos((int) (mc.player.getX() + 1), (int)mc.player.getY(), (int)mc.player.getZ()), // Right
                new BlockPos((int) (mc.player.getX() - 1), (int)mc.player.getY(), (int)mc.player.getZ()), // Left
                new BlockPos((int)mc.player.getX(), (int)mc.player.getY(),  (int)mc.player.getZ() + 1), // Front
                new BlockPos((int)mc.player.getX(),(int) mc.player.getY(), (int) mc.player.getZ() - 1), // Back
                new BlockPos( (int)mc.player.getX() + 1,  (int)mc.player.getY(),  (int)mc.player.getZ() + 1), // Front-right
                new BlockPos( (int)mc.player.getX() - 1, (int)mc.player.getY(), (int)mc.player.getZ() + 1), // Front-left
                new BlockPos((int)mc.player.getX() + 1,(int) mc.player.getY(), (int)mc.player.getZ() - 1), // Back-right
                new BlockPos((int)mc.player.getX() - 1, (int)mc.player.getY(), (int)mc.player.getZ() - 1), // Back-left
        };

        for (BlockPos pos : positions) {
            assert mc.world != null;
            if (mc.world.getBlockState(pos).isAir()) {
                placeBlock(pos);
            }
        }
    }
    private void placeBlock(BlockPos pos) {
        if (mc.player == null) return;

        // Find the best slot for placing obsidian
        FindItemResult result = InvUtils.findInHotbar(Items.OBSIDIAN);
        if (result.slot() == -1) return; // No obsidian found

        // Rotate to the block position
        Vec3d blockCenter = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        float[] angles = RotationUtil.InteractionUtility.calculateAngle(mc.player.getEyePos(), blockCenter);
        mc.player.setYaw(angles[0]);
        mc.player.setPitch(angles[1]);

        // Create a BlockHitResult
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.UP, pos, false);

        // Place the block (use Hand.MAIN_HAND or Hand.OFF_HAND depending on the slot)
        assert mc.interactionManager != null;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);  // You can change Hand.MAIN_HAND to Hand.OFF_HAND as needed
        isPlacing = true;
    }



    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
