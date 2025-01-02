package me.alpha432.oyvey.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PositionUtil {
    public PositionUtil() {
    }

    public static List<BlockPos> getAllInBox(Box box, BlockPos pos) {
        List<BlockPos> intersections = new ArrayList();

        for(int x = (int)Math.floor(box.minX); (double)x < Math.ceil(box.maxX); ++x) {
            for(int z = (int)Math.floor(box.minZ); (double)z < Math.ceil(box.maxZ); ++z) {
                intersections.add(new BlockPos(x, pos.getY(), z));
            }
        }

        return intersections;
    }

    public static List<BlockPos> getAllInBox(Box box) {
        List<BlockPos> intersections = new ArrayList();

        for(int x = (int)Math.floor(box.minX); (double)x < Math.ceil(box.maxX); ++x) {
            for(int y = (int)Math.floor(box.minY); (double)y < Math.ceil(box.maxY); ++y) {
                for(int z = (int)Math.floor(box.minZ); (double)z < Math.ceil(box.maxZ); ++z) {
                    intersections.add(new BlockPos(x, y, z));
                }
            }
        }

        return intersections;
    }
}