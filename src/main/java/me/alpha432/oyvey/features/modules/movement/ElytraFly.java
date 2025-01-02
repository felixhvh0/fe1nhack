package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ElytraFly extends Module {

    public ElytraFly() {
        super("ElytraFly", "", Category.MOVEMENT, true, false, false);
    }

    public enum ElytraFlightModes {
        BOUNCE,
        CONTROL
    }

    public Setting<ElytraFlightModes> flightMode = this.register(new Setting<>("FlightMode", ElytraFlightModes.CONTROL));
    public Setting<Float> pitch = this.register(new Setting<>("Pitch", 70.0f, 60f, 90f));
    public Setting<Boolean> stopInWater = this.register(new Setting<>("StopInWater", true));
    public Setting<Float> controlspeed = this.register(new Setting<>("ControlSpeed", 1.0f, 0.25f, 10f, v -> flightMode.getValue().equals(ElytraFlightModes.CONTROL)));
    public Setting<Float> ySpeed = this.register(new Setting<>("Y Speed", 1.0f, 0.25f, 3f, v -> flightMode.getValue().equals(ElytraFlightModes.CONTROL)));

    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (flightMode.getValue() == ElytraFlightModes.BOUNCE) {
            if (!mc.player.isFallFlying() && mc.player.isOnGround()) {
                mc.player.jump();
                mc.player.startFallFlying();
            }

            if (mc.player.isFallFlying()) {
                handleBounceMode();
            } else {
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                }
            }
        }

        if (flightMode.getValue() == ElytraFlightModes.CONTROL) {
            if (mc.player.isFallFlying()) {
                Vec3d moveVec = Vec3d.ZERO;

                Float y = 0f;

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

                if (mc.options.jumpKey.isPressed()) {
                    y = ySpeed.getValue();
                }

                if (mc.options.sneakKey.isPressed()) {
                    y = -ySpeed.getValue();
                }


                moveVec = moveVec.normalize().multiply(controlspeed.getValue());
                mc.player.setVelocity(moveVec.x,y, moveVec.z);
            }
        }

    }

    private void handleBounceMode() {
        // Adjust flight pitch and velocity
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), pitch.getValue(), false));

        // Handle collision with water
        if (mc.player.isTouchingWater() && stopInWater.getValue()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }
}
