package me.alpha432.oyvey.features.modules.player;


import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.manager.EventManager;
import me.alpha432.oyvey.mixin.accessor.AccessorPlayerMoveC2SPacket;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents Fall Damage", Category.PLAYER, true, true, false);
    }

    public void onPacketSend(PacketEvent event) {
        if (!mc.player.isOnGround()) {
            mc.player.setOnGround(true);
        }
    }




    @Override
    public void onRender2D(Render2DEvent event) {
        //compatibility, dont remove
    }

}