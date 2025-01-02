package me.alpha432.oyvey.features.modules.render;

import java.awt.*;

import me.alpha432.oyvey.event.EventListener;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.DirectionUtil;
import me.alpha432.oyvey.util.DirectionUtil.EightWayDirections;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

//RX MODULE :3
public class PhaseESP extends Module {

    public PhaseESP() {super("PhaseESP", "Shows safe places to Phase", Category.RENDER, true, false, false);}
    public final Setting<Integer> boxAlpha = this.register(new Setting<>("BoxAlpha", 15, 0, 255));
    public final Setting<Integer> lineAlpha = this.register(new Setting<>("LineAlpha", 50, 0, 255));

    public final Setting<Double> lineWidth = this.register(new Setting<>("LineWidth", 2.0, 0.1, 4.0));
    public final Setting<Double> fadeDistance = this.register(new Setting<>("FadeDist", 0.5, 0.0, 1.0));
    public final Setting<Boolean> diagonal = this.register(new Setting<>("Diagonal", true));

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (mc.player != null && mc.world != null && mc.player.isOnGround()) {
            BlockPos playerPos = mc.player.getBlockPos();
            for (EightWayDirections direction : EightWayDirections.values()) {
                if (diagonal.getValue() || DirectionUtil.isCardinal(direction)) {
                    BlockPos blockPos = direction.offset(playerPos);
                    phaseESPRender(blockPos, event, direction);
                }
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    private void phaseESPRender(BlockPos blockPos, Render3DEvent event, EightWayDirections direction) {
        if (!mc.world.getBlockState(blockPos).isReplaceable()) {
            BlockState state = mc.world.getBlockState(blockPos.down());
            Color color;
            if (state.isReplaceable()) {
                color = new Color(255, 0, 0, boxAlpha.getValue());
            } else {
                if (state.getHardness(mc.world, blockPos.down()) < 0) {
                    color = new Color(0, 255, 0, boxAlpha.getValue());
                } else {
                    color = new Color(255, 255, 0, boxAlpha.getValue());
                }
            }

            BlockPos playerPos = mc.player.getBlockPos();
            Vec3d pos = mc.player.getPos();
            double dx = pos.getX() - (double)playerPos.getX();
            double dz = pos.getZ() - (double)playerPos.getZ();

            double far = fadeDistance.getValue();
            double near = 1.0 - fadeDistance.getValue();

            if (direction == EightWayDirections.EAST && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.WEST && dx <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.SOUTH && dz >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.NORTH && dz <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.NORTHEAST && dz <= near && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.NORTHWEST && dz <= near && dx <= near) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.SOUTHEAST && dz >= far && dx >= far) {
                BoxRender(blockPos, event, color);
            } else if (direction == EightWayDirections.SOUTHWEST && dz >= far && dx <= near) {
                BoxRender(blockPos, event, color);
            }
        }
    }

    private void BoxRender(BlockPos blockPos, Render3DEvent event, Color color) {
        Box render1 = VoxelShapes.fullCube().getBoundingBox();
        Box render = new Box((double)blockPos.getX() + render1.minX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.minZ, (double)blockPos.getX() + render1.maxX, (double)blockPos.getY() + render1.minY, (double)blockPos.getZ() + render1.maxZ);
        RenderUtil.drawBoxFilled(event.getMatrixStack(), render, color);
        Color lineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), lineAlpha.getValue());
        RenderUtil.drawBox(event.getMatrixStack(), render, lineColor, lineWidth.getValue());
    }
}
