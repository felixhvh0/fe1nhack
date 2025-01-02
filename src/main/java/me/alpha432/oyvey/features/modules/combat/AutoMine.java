package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.EventListener;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;

import java.awt.*;

public class AutoMine extends Module {
    public Setting<Float> range = this.register(new Setting<>("Range", 4.5f, 1f, 6f)); // Range setting
    Boolean mining = false;
    Boolean secondMining = false;
    BlockPos minePos = null;
    BlockPos secondMinePos = null;
    String moduleDescription = "";
    String moduleInfo = moduleDescription;

    @Override
    public String getDisplayInfo() {
        return moduleInfo;
    }

    public AutoMine() {
        super("AutoMine", "", Category.COMBAT, true, false, true);
    }

    public void onUpdate() {
        for (Entity entity : mc.world.getPlayers()) {
            if (!entity.equals(mc.player) && !OyVey.friendManager.isFriend(entity.getName().getString()) && isInRange(entity)) { // Self-check, friend-check, and range-check
                BlockPos[] positions = {
                        new BlockPos(entity.getBlockPos()),
                        new BlockPos(entity.getBlockPos().add(+1, 0, 0)),
                        new BlockPos(entity.getBlockPos().add(-1, 0, 0)),
                        new BlockPos(entity.getBlockPos().add(0, 0, +1)),
                        new BlockPos(entity.getBlockPos().add(0, 0, -1))
                };

                for (BlockPos pos : positions) {
                    assert mc.world != null;
                    if ((mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) && isInRange(pos)) { // Check block and range
                        moduleDescription = entity.getName().getString();
                        Command.sendRawMessage("moduledescription is " + moduleDescription);
                        if (!mining) {
                            setTarget(pos);
                            minePos = pos;
                        } else if (!secondMining && (secondMinePos == null || !secondMinePos.equals(pos))) {
                            setSecondTarget(pos);
                            secondMinePos = pos;
                        }
                    }
                }
            }
        }

        if (minePos != null && mc.world.getBlockState(minePos).isAir()) {
            minePos = null;
            mining = false;
        }

        if (secondMinePos != null && mc.world.getBlockState(secondMinePos).isAir()) {
            secondMinePos = null;
            secondMining = false;
        }
    }

    public void setTarget(BlockPos pos) {
        if (!mining) {
            mining = true;
           // Command.sendMessage("Target set to " + pos.getX() + "," + pos.getY() + "," + pos.getZ());

            mineBlock(pos);
        }
    }

    public void setSecondTarget(BlockPos pos) {
        if (!secondMining) {
            secondMining = true;
            Command.sendMessage("Second target set to " + pos.getX() + "," + pos.getY() + "," + pos.getZ());
            mineBlock(pos);
        }
    }

    public void mineBlock(BlockPos pos) {
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));
        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, net.minecraft.util.math.Direction.UP));

        // wait for the block breaking speed ammount, then swap

    }

    private boolean isInRange(Entity entity) {
        return mc.player.getPos().distanceTo(entity.getPos()) <= range.getValue();
    }

    private boolean isInRange(BlockPos pos) {
        return mc.player.getPos().distanceTo(new net.minecraft.util.math.Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) <= range.getValue();
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        //compat
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        int red = ClickGui.getInstance().red.getValue();
        int green = ClickGui.getInstance().green.getValue();
        int blue = ClickGui.getInstance().blue.getValue();

        if (minePos != null) {
            RenderUtil.drawBox(event.getMatrixStack(), minePos, new Color(red, green, blue, 255), 1f);
        }

        if (secondMinePos != null) {
            RenderUtil.drawBox(event.getMatrixStack(), secondMinePos, new Color(red, green, blue, 155), 1f);
        }
    }
}
