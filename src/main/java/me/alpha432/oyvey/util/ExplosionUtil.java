package me.alpha432.oyvey.util;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.BlockView;

import java.util.Iterator;
import java.util.function.BiFunction;

import static me.alpha432.oyvey.util.Util.mc;

public class ExplosionUtil {

    public ExplosionUtil() {
    }

    public static double getDamageTo(Entity entity, Vec3d explosion) {
        return getDamageTo(entity, explosion, false);
    }

    public static double getDamageTo(Entity entity, Vec3d explosion, boolean ignoreTerrain) {
        return getDamageTo(entity, explosion, ignoreTerrain, 12.0F);
    }

    public static double getDamageTo(Entity entity, Vec3d explosion, boolean ignoreTerrain, float power) {
        double d = Math.sqrt(entity.squaredDistanceTo(explosion));
        double ab = getExposure(explosion, entity, ignoreTerrain);
        double w = d / (double)power;
        double ac = (1.0 - w) * ab;
        double dmg = ((float)((int)((ac * ac + ac) / 2.0 * 7.0 * 12.0 + 1.0)));
        dmg = getReduction(entity, mc.world.getDamageSources().explosion((Explosion)null), dmg);
        return Math.max(0.0, dmg);
    }

    private static double getReduction(Entity entity, DamageSource damageSource, double damage) {
        if (damageSource.isScaledWithDifficulty()) {
            switch (mc.world.getDifficulty()) {
                case EASY:
                    damage = Math.min(damage / 2.0 + 1.0, damage);
                    break;
                case HARD:
                    damage *= 1.5;
            }
        }


        return Math.max(damage, 0.0);
    }
    private static float getArmor(LivingEntity entity) {
        return (float)Math.floor(getAttributeValue(entity, (EntityAttribute) EntityAttributes.GENERIC_ARMOR));
    }
    public static double getAttributeValue(LivingEntity entity, EntityAttribute attribute) {
        return getAttributeInstance(entity, attribute).getValue();
    }

    public static EntityAttributeInstance getAttributeInstance(LivingEntity entity, EntityAttribute attribute) {
        double baseValue = getDefaultForEntity(entity).getBaseValue((RegistryEntry<EntityAttribute>) attribute);
        EntityAttributeInstance attributeInstance = new EntityAttributeInstance((RegistryEntry<EntityAttribute>) attribute, (o1) -> {
        });
        attributeInstance.setBaseValue(baseValue);
        EquipmentSlot[] var5 = EquipmentSlot.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            EquipmentSlot equipmentSlot = var5[var7];
            ItemStack stack = entity.getEquippedStack(equipmentSlot);
            Multimap<EntityAttribute, EntityAttributeModifier> modifiers = (Multimap<EntityAttribute, EntityAttributeModifier>) stack.getComponents();
            Iterator var11 = modifiers.get(attribute).iterator();

            while(var11.hasNext()) {
                EntityAttributeModifier modifier = (EntityAttributeModifier)var11.next();
                attributeInstance.addTemporaryModifier(modifier);
            }
        }

        return attributeInstance;
    }
    private static <T extends LivingEntity> DefaultAttributeContainer getDefaultForEntity(T entity) {
        return DefaultAttributeRegistry.get((EntityType<? extends LivingEntity>) entity.getType());
    }

    private static float getExposure(Vec3d source, Entity entity, boolean ignoreTerrain) {
        Box box = entity.getBoundingBox();
        return getExposure(source, box, ignoreTerrain);
    }

    private static float getExposure(Vec3d source, Box box, boolean ignoreTerrain) {
        RaycastFactory raycastFactory = getRaycastFactory(ignoreTerrain);
        double xDiff = box.maxX - box.minX;
        double yDiff = box.maxY - box.minY;
        double zDiff = box.maxZ - box.minZ;
        double xStep = 1.0 / (xDiff * 2.0 + 1.0);
        double yStep = 1.0 / (yDiff * 2.0 + 1.0);
        double zStep = 1.0 / (zDiff * 2.0 + 1.0);
        if (xStep > 0.0 && yStep > 0.0 && zStep > 0.0) {
            int misses = 0;
            int hits = 0;
            double xOffset = (1.0 - Math.floor(1.0 / xStep) * xStep) * 0.5;
            double zOffset = (1.0 - Math.floor(1.0 / zStep) * zStep) * 0.5;
            xStep *= xDiff;
            yStep *= yDiff;
            zStep *= zDiff;
            double startX = box.minX + xOffset;
            double startY = box.minY;
            double startZ = box.minZ + zOffset;
            double endX = box.maxX + xOffset;
            double endY = box.maxY;
            double endZ = box.maxZ + zOffset;

            for(double x = startX; x <= endX; x += xStep) {
                for(double y = startY; y <= endY; y += yStep) {
                    for(double z = startZ; z <= endZ; z += zStep) {
                        Vec3d position = new Vec3d(x, y, z);
                        if (raycast(new ExposureRaycastContext(position, source), raycastFactory) == null) {
                            ++misses;
                        }

                        ++hits;
                    }
                }
            }

            return (float)misses / (float)hits;
        } else {
            return 0.0F;
        }
    }
    private static RaycastFactory getRaycastFactory(boolean ignoreTerrain) {
        return ignoreTerrain ? (context, blockPos) -> {
            BlockState blockState = mc.world.getBlockState(blockPos);
            return blockState.getBlock().getBlastResistance() < 600.0F ? null : blockState.getCollisionShape(mc.world, blockPos).raycast(context.start(), context.end(), blockPos);
        } : (context, blockPos) -> {
            BlockState blockState = mc.world.getBlockState(blockPos);
            return blockState.getCollisionShape(mc.world, blockPos).raycast(context.start(), context.end(), blockPos);
        };
    }

    private static BlockHitResult raycast(ExposureRaycastContext context, RaycastFactory raycastFactory) {
        return (BlockHitResult)BlockView.raycast(context.start, context.end, context, raycastFactory, (ctx) -> {
            return null;
        });
    }
    public static record ExposureRaycastContext(Vec3d start, Vec3d end) {
        public ExposureRaycastContext(Vec3d start, Vec3d end) {
            this.start = start;
            this.end = end;
        }

        public Vec3d start() {
            return this.start;
        }

        public Vec3d end() {
            return this.end;
        }
    }

    @FunctionalInterface
    public interface RaycastFactory extends BiFunction<ExposureRaycastContext, BlockPos, BlockHitResult> {
    }


}
