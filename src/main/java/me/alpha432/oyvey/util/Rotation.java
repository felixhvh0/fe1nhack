package me.alpha432.oyvey.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import static me.alpha432.oyvey.util.Util.mc;

public class Rotation {
    private final int priority;
    private float yaw;
    private float pitch;
    private boolean snap;

    public Rotation(int priority, float yaw, float pitch, boolean snap) {
        this.priority = priority;
        this.yaw = yaw;
        this.pitch = pitch;
        this.snap = snap;
    }

    public static float @NotNull [] calculateAngle(Vec3d to) {
        assert mc.player != null;
        return calculateAngle(mc.player.getEyePos(), to);
    }

    public static float @NotNull [] calculateAngle(@NotNull Vec3d from, @NotNull Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        float yD = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
        float pD = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))), -90f, 90f);
        return new float[]{yD, pD};
    }

    public Rotation(int priority, float yaw, float pitch) {
        this(priority, yaw, pitch, false);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setSnap(boolean snap) {
        this.snap = snap;
    }

    public boolean isSnap() {
        return this.snap;
    }
}