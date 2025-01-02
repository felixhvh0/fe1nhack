package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class BoatExcute extends Module {

    public BoatExcute() {
        super("BoatExecute","",Category.MOVEMENT,true,false,true);
    }

    @Override
    public void onEnable() {
        if (!(mc.player.getVehicle() instanceof BoatEntity boat)) return;
        Vec3d originalPos = boat.getPos();
        boat.setPos(originalPos.x, originalPos.y+0.05, originalPos.z);
        VehicleMoveC2SPacket groundPacket = new VehicleMoveC2SPacket(boat);
        boat.setPos(originalPos.x, originalPos.y+20, originalPos.z);
        VehicleMoveC2SPacket skyPacket = new VehicleMoveC2SPacket(boat);
        boat.setPos(originalPos.x, originalPos.y, originalPos.z);
            mc.getNetworkHandler().sendPacket(skyPacket);
            mc.getNetworkHandler().sendPacket(groundPacket);
            Command.sendMessage("executed player >:3");
        mc.getNetworkHandler().sendPacket(new VehicleMoveC2SPacket(boat));
        this.toggle();
    }


    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
