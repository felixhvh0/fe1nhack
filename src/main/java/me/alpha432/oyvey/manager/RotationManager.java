package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.util.InteractionUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Util;
import me.alpha432.oyvey.util.Rotation;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RotationManager implements Util {
    private float yaw;
    private float pitch;
    private Rotation rotation;
    private static final List<Rotation> requests = new ArrayList();
    public static boolean sendDisablerPacket = false;
    public static float lastActualYaw = 0f;
    public float rotationYaw = 0;
    public float rotationPitch = 0;
    public float lastYaw = 0;
    public float lastPitch = 0;


    public static void silentRotateToEntity(Entity e)
    {
        assert mc.player != null;
        float[] angle = Rotation.calculateAngle(e.getPos());
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                angle[0],
                angle[1],
                mc.player.isOnGround()
        ));
    }



    public void updateRotations() {
        this.yaw = mc.player.getYaw();
        this.pitch = mc.player.getPitch();
    }

    public void restoreRotations() {
        mc.player.setYaw(yaw);
        mc.player.headYaw = yaw;
        mc.player.setPitch(pitch);
    }

    public static void setPlayerRotations(float yaw, float pitch) {
        mc.player.setYaw(yaw);
        mc.player.headYaw = yaw;
        mc.player.setPitch(pitch);
    }

    public void setPlayerYaw(float yaw) {
        mc.player.setYaw(yaw);
        mc.player.headYaw = yaw;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(mc.player.getEyePos(), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(mc.player.getEyePos(), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public static float[] getRotationsTo(Vec3d src, Vec3d dest) {
        float yaw = (float) (Math.toDegrees(Math.atan2(dest.subtract(src).z,
                dest.subtract(src).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(dest.subtract(src).y,
                Math.hypot(dest.subtract(src).x, dest.subtract(src).z)));
        return new float[]
                {
                        MathHelper.wrapDegrees(yaw),
                        MathHelper.wrapDegrees(pitch)
                };
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(mc.player.getEyePos(), entity.getEyePos());
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public static Entity getCrosshairTarget(float yaw, float pitch, float distance, boolean ignoreWalls) {
        Entity targetedEntity = null;
        HitResult result = ignoreWalls ? null : rayTrace(distance, yaw, pitch);
        Vec3d vec3d = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        double distancePow2 = Math.pow(distance, 2);
        if (result != null) distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        Box box = mc.player.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(mc.player, vec3d, vec3d3, box, (entity) -> !entity.isSpectator() && entity.canHit(), distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (g < distancePow2 || result == null) {
                if (entity2 instanceof LivingEntity) {
                    targetedEntity = entity2;
                    return targetedEntity;
                }
            }
        }
        return targetedEntity;
    }

    public static @NotNull Vec3d getRotationVector(float yaw, float pitch) {
        return new Vec3d(MathHelper.sin(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F), -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F));
    }

    public static HitResult rayTrace(double dst, float yaw, float pitch) {
        Vec3d vec3d = mc.player.getCameraPosVec(1f);
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
    }

    public void setPlayerPitch(float pitch) {
        mc.player.setPitch(pitch);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public static void setRotationSilentSync(boolean grim) {
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        if (grim) {
            setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), yaw, pitch, mc.player.isOnGround()));
        } else {
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
        }

    }

    public static void setRotation(Rotation rotation) {
        if (rotation.getPriority() == Integer.MAX_VALUE) {
            rotation = rotation;
        }

        Rotation finalRotation = rotation;
        Rotation request = (Rotation)requests.stream().filter((r) -> {
            return finalRotation.getPriority() == r.getPriority();
        }).findFirst().orElse(null);
        if (request == null) {
            requests.add(rotation);
        } else {
            request.setYaw(rotation.getYaw());
            request.setPitch(rotation.getPitch());
        }

    }
    public static void setRotationSilent(float yaw, float pitch, boolean grim) {
        if (grim) {
            setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), yaw, pitch, mc.player.isOnGround()));
        } else {
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
        }

    }

}
