package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoCrystal extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Setting<Float> range = this.register(new Setting<>("Range", 5.0f, 1.0f, 10.0f));
    private final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));  // New rotate setting
    private final boolean debugMode = false; // Set to false for release
    private final Set<BlockPos> blacklistedPositions = new HashSet<>();

    public AutoCrystal() {
        super("JaxuiCrystal", "Places and breaks crystals on bedrock/obsidian near the target", Category.COMBAT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null) return;

        PlayerEntity target = getTarget();
        if (target == null) {
            log("No target found within range.");
            return;
        }

        BlockPos placePos = findBestPlacePosition(target);
        while (placePos != null && !placeCrystal(placePos)) {
            blacklistedPositions.add(placePos);
            placePos = findBestPlacePosition(target);
        }

        if (placePos != null) {
            log("Attempting to place crystal at " + placePos);
            breakCrystalAt(placePos.up());
        } else {
            log("No suitable position found for placing crystal.");
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
    }

    @Override
    public void onDisable() {
        blacklistedPositions.clear();
    }

    private PlayerEntity getTarget() {
        return mc.world.getPlayers().stream()
                .filter(player -> player != mc.player && !player.isRemoved())
                .filter(player -> mc.player.distanceTo(player) < range.getValue())
                .min((player1, player2) -> Double.compare(mc.player.distanceTo(player1), mc.player.distanceTo(player2)))
                .orElse(null);
    }

    private BlockPos findBestPlacePosition(PlayerEntity target) {
        BlockPos targetPos = target.getBlockPos();
        List<BlockPos> positions = Stream.of(
                targetPos.north(),
                targetPos.south(),
                targetPos.east(),
                targetPos.west(),
                targetPos.north().down(),
                targetPos.south().down(),
                targetPos.east().down(),
                targetPos.west().down(),
                targetPos.down()
        ).collect(Collectors.toList());

        return positions.stream()
                .filter(pos -> isValidTargetBlock(pos) && target.getPos().distanceTo(Vec3d.ofCenter(pos)) >= 1)
                .filter(pos -> !blacklistedPositions.contains(pos))
                .max((pos1, pos2) -> Float.compare(calculateDamage(pos1, target), calculateDamage(pos2, target)))
                .orElse(null);
    }

    private boolean isValidTargetBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    private float calculateDamage(BlockPos pos, PlayerEntity target) {
        // Simplified damage calculation
        double dist = Math.sqrt(pos.getSquaredDistance(target.getPos()));
        double exposure = 1.0 - (dist / 12.0);
        return (float) ((exposure * exposure + exposure) * 7.0 * 12.0 + 1.0);
    }

    private boolean placeCrystal(BlockPos pos) {
        if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {
            // Swap to end crystal if not holding one
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() == Items.END_CRYSTAL) {
                    mc.player.getInventory().selectedSlot = i;
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));  // Ensure slot update is sent
                    log("Swapping to end crystal in slot " + i);
                    break;
                }
            }
        }

        if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
            if (rotate.getValue()) {
                facePosition(pos);
            }
            BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false);
            try {
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 1));
                log("Placed crystal at " + pos);
                return true;
            } catch (Exception e) {
                log("Failed to place crystal: " + e.getMessage());
                blacklistedPositions.add(pos);
                return false;
            }
        } else {
            log("No end crystal in hand to place.");
            blacklistedPositions.add(pos);
            return false;
        }
    }

    private void breakCrystalAt(BlockPos pos) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity && entity.getBlockPos().equals(pos)) {
                if (rotate.getValue()) {
                    faceEntity(entity);
                }
                mc.interactionManager.attackEntity(mc.player, entity);
                mc.player.swingHand(Hand.MAIN_HAND);
                log("Breaking crystal at " + pos);
                return;
            }
        }
    }

    private void facePosition(BlockPos pos) {
        Vec3d eyesPos = mc.player.getEyePos();
        Vec3d targetPos = Vec3d.ofCenter(pos);
        double diffX = targetPos.x - eyesPos.x;
        double diffY = targetPos.y - eyesPos.y;
        double diffZ = targetPos.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    private void faceEntity(Entity entity) {
        Vec3d eyesPos = mc.player.getEyePos();
        Vec3d targetPos = entity.getPos();
        double diffX = targetPos.x - eyesPos.x;
        double diffY = targetPos.y + entity.getHeight() / 2.0 - eyesPos.y;
        double diffZ = targetPos.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    private void log(String message) {
        if (debugMode) {
            Command.sendMessage(message);
        }
    }
}
