package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.RotationManager;
import me.alpha432.oyvey.util.InteractionUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import me.alpha432.oyvey.manager.InteractionManager;

public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", "Bridge like sigma", Category.PLAYER, true, false, false);
    }

    private final Setting<Float> placeDelay = this.register(new Setting<>("PlaceDelay", 0.1f, 0.0f, 1.0f));
    private long lastPlace = 0;

    @Override
    public void onUpdate() {

        ClientPlayerEntity player = mc.player;
        BlockPos belowPlayer = mc.player.getBlockPos().down();

        if (mc.world.getBlockState(belowPlayer).getBlock() == Blocks.AIR) {
            if (System.currentTimeMillis() - lastPlace >= (placeDelay.getValue() * 1000)) {
                placeBlock(belowPlayer);
                lastPlace = System.currentTimeMillis();

            }
        }
    }

    private void placeBlock(BlockPos belowPlayer) {
        if (mc.interactionManager == null || mc.player == null) return;
        RotationManager.setRotationSilent(mc.player.getYaw()+180,85,true);
        InteractionUtil.place(belowPlayer,true, Hand.MAIN_HAND);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        //compatibility, dont remove
    }
}

