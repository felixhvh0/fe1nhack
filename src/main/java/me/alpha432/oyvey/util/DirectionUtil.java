package me.alpha432.oyvey.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.List;

public class DirectionUtil {

    public enum EightWayDirections {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST),
        NORTHEAST(Direction.NORTH, Direction.EAST),
        NORTHWEST(Direction.NORTH, Direction.WEST),
        SOUTHEAST(Direction.SOUTH, Direction.EAST),
        SOUTHWEST(Direction.SOUTH, Direction.WEST);

        private final List<Direction> directions;

        EightWayDirections(Direction... directions) {
            this.directions = Arrays.asList(directions);
        }

        public BlockPos offset(BlockPos pos) {
            BlockPos result = pos;
            for (Direction direction : directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }

    public static Boolean isCardinal(EightWayDirections dir) {
        return dir.equals(EightWayDirections.EAST) || dir.equals(EightWayDirections.WEST) || dir.equals(EightWayDirections.SOUTH) || dir.equals(EightWayDirections.NORTH);
    }

    public enum FourWayDirections {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST);

        private final List<Direction> directions;

        FourWayDirections(Direction... directions) {
            this.directions = Arrays.asList(directions);
        }

        public BlockPos offset(BlockPos pos) {
            BlockPos result = pos;
            for (Direction direction : directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }
}