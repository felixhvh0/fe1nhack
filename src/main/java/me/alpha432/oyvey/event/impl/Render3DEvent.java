package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class Render3DEvent extends Event {
    private final float delta;
    private final MatrixStack matrix;
    private Matrix4f projectionMatrix;

    public Render3DEvent(MatrixStack matrix, float delta) {
        this.matrix = matrix;
        this.delta = delta;
        this.projectionMatrix = projectionMatrix;

    }


    public MatrixStack getMatrixStack() {
        return this.matrix;
    }
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}


