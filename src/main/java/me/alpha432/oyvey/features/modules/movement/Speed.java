package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.FakePlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {

    private final Setting<mode> Mode = this.register(new Setting<>("Mode", mode.Strafe));
    public Setting<Float> speed = this.register(new Setting<>("Speed", 0.25f, 0.1f, 2f));

    public Speed() {
        super("Speed", "", Category.MOVEMENT, true, false, true);
    }

    public enum mode {
        GrimCollide,
        Strafe,
        LowHop,
        Vanilla
    }

    public void onUpdate() {
        double dx = mc.player.getX() - mc.player.prevX;
        double dz = mc.player.getZ() - mc.player.prevZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (Mode.getValue() == mode.GrimCollide) {
            int collisions = 0;
            for (Entity entity : mc.world.getEntities()) {
                if (checkIsCollidingEntity(entity) && MathHelper.sqrt((float) mc.player.squaredDistanceTo(entity)) <= 1.5) {
                    collisions++;
                }
            }
            if (collisions > 0) {
                Vec3d velocity = mc.player.getVelocity();
                double factor = 0.08 * collisions;
                Vec2f strafe = handleStrafeMotion((float) factor);
                mc.player.setVelocity(velocity.x + strafe.x, velocity.y, velocity.z + strafe.y);
            }
        }

        if (Mode.getValue() == mode.Strafe) {
            if (mc.options.forwardKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.backKey.isPressed()) {
                Vec3d moveVec = Vec3d.ZERO;

                if (mc.options.forwardKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw()).normalize());
                }
                if (mc.options.backKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 180).normalize());
                }

                if (mc.options.rightKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 90).normalize());
                }
                if (mc.options.leftKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() - 90).normalize());
                }


                moveVec = moveVec.normalize().multiply(speed.getValue());
                mc.player.setVelocity(moveVec.x, mc.player.getVelocity().y, moveVec.z);
                //autojump
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }
            } else mc.player.setVelocity(0,mc.player.getVelocity().y,0);
        }

        if (Mode.getValue() == mode.LowHop) {
            if (mc.options.forwardKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.backKey.isPressed()) {
                Vec3d moveVec = Vec3d.ZERO;

                if (mc.options.forwardKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw()).normalize());
                }
                if (mc.options.backKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 180).normalize());
                }

                if (mc.options.rightKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 90).normalize());
                }
                if (mc.options.leftKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() - 90).normalize());
                }


                moveVec = moveVec.normalize().multiply(speed.getValue());
                mc.player.setVelocity(moveVec.x, mc.player.getVelocity().y-0.05, moveVec.z);
                //autojump
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }
            } else mc.player.setVelocity(0,mc.player.getVelocity().y,0);
        }


        if (Mode.getValue() == mode.Vanilla) {
            if (mc.options.forwardKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.backKey.isPressed()) {
                Vec3d moveVec = Vec3d.ZERO;

                if (mc.options.forwardKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw()).normalize());
                }
                if (mc.options.backKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 180).normalize());
                }

                if (mc.options.rightKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() + 90).normalize());
                }
                if (mc.options.leftKey.isPressed()) {
                    moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw() - 90).normalize());
                }


                moveVec = moveVec.normalize().multiply(speed.getValue());
                mc.player.setVelocity(moveVec.x, mc.player.getVelocity().y, moveVec.z);

            } else mc.player.setVelocity(0,mc.player.getVelocity().y,0);
        }

    }


    @Override
    public void onRender2D(Render2DEvent event) {

    }

    public boolean checkIsCollidingEntity(Entity entity) {
        return entity != null && entity != mc.player && entity instanceof LivingEntity
                && !(entity instanceof FakePlayerEntity) && !(entity instanceof ArmorStandEntity);
    }

    public Vec2f handleStrafeMotion(final float speed) {
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw);
        if (forward == 0.0f && strafe == 0.0f) {
            return Vec2f.ZERO;
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += forward > 0.0f ? -45 : 45;
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += forward > 0.0f ? 45 : -45;
                strafe = 0.0f;
            }
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        float rx = (float) Math.cos(Math.toRadians(yaw));
        float rz = (float) -Math.sin(Math.toRadians(yaw));
        return new Vec2f((forward * speed * rz) + (strafe * speed * rx),
                (forward * speed * rx) - (strafe * speed * rz));
    }

    private void sendMovementPackets(Vec3d velocity) {
        ClientPlayerEntity player = mc.player;
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                player.getX() + velocity.x,
                player.getY(),
                player.getZ() + velocity.z,
                player.getYaw(),
                player.getPitch(),
                player.isOnGround()
        ));
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                player.getYaw(),
                player.getPitch(),
                player.isOnGround()
        ));
    }
}
