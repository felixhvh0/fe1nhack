package me.alpha432.oyvey.util;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RotationUtil {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static class InteractionUtility {
        private static final MinecraftClient mc = MinecraftClient.getInstance();

        public static float @NotNull [] calculateAngle(Vec3d to) {
            assert mc.player != null;
            return calculateAngle(getEyesPos(mc.player), to);
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

        private static Vec3d getEyesPos(ClientPlayerEntity player) {
            return player.getEyePos();
        }
    }

    /* SILENT ROTATIONS */
    public static void silentRotateToEntity(Entity e)
    {
        assert client.player != null;
        float[] angle = InteractionUtility.calculateAngle(e.getPos());
        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                angle[0],
                angle[1],
                client.player.isOnGround()
        ));
    }

    public static void silentRotateToPos(BlockPos pos)
    {
        assert client.player != null;
        float[] angle = InteractionUtility.calculateAngle(Vec3d.of(pos));
        Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                angle[0],
                angle[1],
                client.player.isOnGround()
        ));
    }
}