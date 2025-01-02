package me.alpha432.oyvey.manager;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.UpdateEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.util.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HoleManager extends Feature {
    private final int range = 8;
    private final List<Hole> holes = new ArrayList<>();
    private final BlockPos.Mutable pos = new BlockPos.Mutable();

    public HoleManager() {
        EVENT_BUS.register(this);
    }

    @Subscribe private void onTick(UpdateEvent event) {
        holes.clear();
        for (int x = -range; x < range; x++) {
            for (int y = -range; y < range; y++) {
                for (int z = -range; z < range; z++) {
                    pos.set(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
                    Hole hole = getHole(pos);
                    if (hole == null) continue;
                    holes.add(hole);
                }
            }
        }
    }

    @Nullable public Hole getHole(BlockPos pos) {
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR)
            return null;
        HoleType type = HoleType.BEDROCK;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            Block block = mc.world.getBlockState(pos.offset(direction)).getBlock();
            if (block == Blocks.OBSIDIAN) type = HoleType.UNSAFE;
            else if (block != Blocks.BEDROCK) return null;
        }
        return new Hole(pos, type);
    }

    private record Hole(BlockPos pos, HoleType holeType) {
    }

    private enum HoleType {
        BEDROCK,
        UNSAFE;
    }

    public static List<BlockPos> getSurroundEntities(Entity entity) {
        List<BlockPos> entities = new LinkedList();
        entities.add(entity.getBlockPos());
        Direction[] var3 = Direction.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Direction dir = var3[var5];
            if (dir.getAxis().isHorizontal()) {
                entities.addAll(PositionUtil.getAllInBox(entity.getBoundingBox(), entity.getBlockPos()));
            }
        }

        return entities;
    }

    public List<BlockPos> getSurroundEntities(BlockPos pos) {
        List<BlockPos> entities = new LinkedList();
        entities.add(pos);
        Direction[] var3 = Direction.values();
        int var4 = var3.length;
        for(int var5 = 0; var5 < var4; ++var5) {
            Direction dir = var3[var5];
            if (dir.getAxis().isHorizontal()) {
                BlockPos pos1 = pos.add(dir.getVector());
                List<Entity> box = mc.world.getOtherEntities(null, new Box(pos1)).stream().filter((e) -> {
                    return !this.isEntityBlockingSurround(e);
                }).toList();
                if (!box.isEmpty()) {
                    Iterator var9 = box.iterator();

                    while(var9.hasNext()) {
                        Entity entity = (Entity) var9.next();
                        entities.addAll(PositionUtil.getAllInBox(entity.getBoundingBox(), pos));
                    }
                }
            }
        }

        return entities;
    }
    public boolean isEntityBlockingSurround(Entity entity) {
        return entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof EndCrystalEntity;
    }

    public static List<BlockPos> getEntitySurroundNoSupport(Entity entity) {
        List<BlockPos> entities = getSurroundEntities(entity);
        List<BlockPos> blocks = new CopyOnWriteArrayList();
        Iterator var4 = entities.iterator();

        while(var4.hasNext()) {
            BlockPos epos = (BlockPos)var4.next();
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction dir2 = var6[var8];
                if (dir2.getAxis().isHorizontal()) {
                    BlockPos pos2 = epos.add(dir2.getVector());
                    if (!entities.contains(pos2) && !blocks.contains(pos2)) {
                        double dist = mc.player.squaredDistanceTo(pos2.toCenterPos());
                        if (!(dist > (4.0F * 4.0F))) {
                            blocks.add(pos2);
                        }
                    }
                }
            }
        }

        return blocks;
    }
}
