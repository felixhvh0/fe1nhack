package me.alpha432.oyvey.event.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class ArmRenderEvent {
    public static ArmRenderEvent INSTANCE = new ArmRenderEvent();

    public MatrixStack matrix;
    public Hand hand;

    public static ArmRenderEvent get(Hand hand, MatrixStack matrices) {
        INSTANCE.matrix = matrices;
        INSTANCE.hand = hand;

        return INSTANCE;
    }
}