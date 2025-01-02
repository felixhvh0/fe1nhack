package me.alpha432.oyvey.event;

import net.minecraft.client.util.math.MatrixStack;

public class Event {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        cancelled = true;
    }

    public MatrixStack getMatrix() {
        return null;
    }
}
