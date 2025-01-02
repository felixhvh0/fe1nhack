package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.EventHandler;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.mixin.IVec3d;
import me.alpha432.oyvey.mixin.accessor.EntityVelocityUpdateS2CPacketAccessor;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Velocity extends Module {
    private final Setting<Boolean> knockback = this.register(new Setting<>("Knockback",true));
    private final Setting<Float> knockbackHorizontal = this.register(new Setting<>("Horizontal", 0f,0f,1f));
    private final Setting<Float> knockbackVertical = this.register(new Setting<>("Vertical", 0f,0f,1f));


    public Velocity() {
        super("Velocity", "", Category.PLAYER, true, false, false);
    }
    @EventHandler
    public void onTick() {
        if (mc.options.jumpKey.isPressed() || mc.options.sneakKey.isPressed()) return;

        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && mc.player.getVelocity().y < 0) {
            ((IVec3d) mc.player.getVelocity()).setY(0);
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive2 event) {
        if (knockback.getValue() && event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet
                && packet.getEntityId() == mc.player.getId()) {
            double velX = (packet.getVelocityX() / 8000d - mc.player.getVelocity().x) * knockbackHorizontal.getValue();
            double velY = (packet.getVelocityY() / 8000d - mc.player.getVelocity().y) * knockbackVertical.getValue();
            double velZ = (packet.getVelocityZ() / 8000d - mc.player.getVelocity().z) * knockbackHorizontal.getValue();
            ((EntityVelocityUpdateS2CPacketAccessor) packet).setX((int) (velX * 8000 + mc.player.getVelocity().x * 8000));
            ((EntityVelocityUpdateS2CPacketAccessor) packet).setY((int) (velY * 8000 + mc.player.getVelocity().y * 8000));
            ((EntityVelocityUpdateS2CPacketAccessor) packet).setZ((int) (velZ * 8000 + mc.player.getVelocity().z * 8000));
        }
    }


    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
