package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class BoatFly extends Module {

    public BoatFly() {
        super("BoatFly","",Category.MOVEMENT,true,false,true);
    }

    public void onTick() {
        if(mc.player.getVehicle() instanceof BoatEntity boat) {
            Vec3d originalPos = boat.getPos();
            Vec3d moveVec = Vec3d.ZERO;
            if (mc.options.forwardKey.isPressed()) {
                moveVec = moveVec.add(Vec3d.fromPolar(0, mc.player.getYaw()).normalize());
               // boat.setPos(originalPos.x, originalPos.y, originalPos.z);
            }

            boat.setPos(moveVec.x, mc.player.getY(), moveVec.z);
        }
    }


    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
