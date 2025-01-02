package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class InteractionManager implements Util {
    public static Direction getPlaceDirectionGrim(BlockPos blockPos) {
        Set<Direction> directions = getPlaceDirectionsGrim(mc.player.getPos(), blockPos);
        return directions.stream().findAny().orElse(Direction.UP);
    }

    public static Set<Direction> getPlaceDirectionsGrim(Vec3d eyePos, BlockPos blockPos) {
        return getPlaceDirectionsGrim(eyePos.x, eyePos.y, eyePos.z, blockPos);
    }

    public static Set<Direction> getPlaceDirectionsGrim(double x, double y, double z, BlockPos pos) {
        Set<Direction> dirs = new HashSet<>(6);
        Box combined = getCombinedBox(pos);
        Box eyePositions = (new Box(x, y + 0.4, z, x, y + 1.62, z)).expand(2.0E-4);
        if (eyePositions.minZ <= combined.minZ) {
            dirs.add(Direction.NORTH);
        }

        if (eyePositions.maxZ >= combined.maxZ) {
            dirs.add(Direction.SOUTH);
        }

        if (eyePositions.maxX >= combined.maxX) {
            dirs.add(Direction.EAST);
        }

        if (eyePositions.minX <= combined.minX) {
            dirs.add(Direction.WEST);
        }

        if (eyePositions.maxY >= combined.maxY) {
            dirs.add(Direction.UP);
        }

        if (eyePositions.minY <= combined.minY) {
            dirs.add(Direction.DOWN);
        }

        return dirs;
    }

    private static Box getCombinedBox(BlockPos pos) {
        VoxelShape shape = mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).offset(pos.getX(), pos.getY(), pos.getZ());
        Box combined = new Box(pos);

        double maxZ;
        double minX;
        double minY;
        double minZ;
        double maxX;
        double maxY;
        for(Iterator<Box> var4 = shape.getBoundingBoxes().iterator(); var4.hasNext(); combined = new Box(minX, minY, minZ, maxX, maxY, maxZ)) {
            Box box = (Box)var4.next();
            minX = Math.max(box.minX, combined.minX);
            minY = Math.max(box.minY, combined.minY);
            minZ = Math.max(box.minZ, combined.minZ);
            maxX = Math.min(box.maxX, combined.maxX);
            maxY = Math.min(box.maxY, combined.maxY);
            maxZ = Math.min(box.maxZ, combined.maxZ);
        }

        return combined;
    }

    public static float calcBlockBreakingDelta(BlockState state, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = canHarvest(state) ? 30 : 100;
            return getBlockBreakingSpeed(state) / f / (float)i;
        }
    }

    private static boolean canHarvest(BlockState state) {
        if (state.isToolRequired()) {
            int tool = InventoryManager.getBestTool(state);
            return mc.player.getInventory().getStack(tool).isSuitableFor(state);
        } else {
            return true;
        }
    }

    private static float getBlockBreakingSpeed(BlockState block) {
        int tool = InventoryManager.getBestTool(block);
        float f = mc.player.getInventory().getStack(tool).getMiningSpeedMultiplier(block);
        if (f > 1.0F) {
            ItemStack stack = mc.player.getInventory().getStack(tool);
            int i = EnchantmentHelper.getLevel((RegistryEntry<Enchantment>) Enchantments.EFFICIENCY, stack);
            if (i > 0 && !stack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (StatusEffectUtil.hasHaste(mc.player)) {
            f *= 1.0F + (float)(StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2F;
        }

        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float var10000;
            switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    var10000 = 0.3F;
                    break;
                case 1:
                    var10000 = 0.09F;
                    break;
                case 2:
                    var10000 = 0.0027F;
                    break;
                default:
                    var10000 = 8.1E-4F;
            }

            float g = var10000;
            f *= g;
        }

        if (mc.player.isSubmergedIn(FluidTags.WATER)) {
            f /= 5.0F;
        }

        if (!mc.player.isOnGround()) {
            f /= 5.0F;
        }

        return f;
    }
}
