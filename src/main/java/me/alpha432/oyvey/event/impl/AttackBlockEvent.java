package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class AttackBlockEvent extends Event {
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private boolean canceled;

    public AttackBlockEvent(BlockPos pos, BlockState state, Direction direction) {
        this.pos = pos;
        this.state = state;
        this.direction = direction;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean cancel) {
        this.canceled = cancel;
    }
}
