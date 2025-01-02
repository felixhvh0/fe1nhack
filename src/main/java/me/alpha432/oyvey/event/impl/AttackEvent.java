package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
    boolean pre;
    private final Entity entity;

    public AttackEvent(Entity entity, boolean pre) {
        this.entity = entity;
        this.pre = pre;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isPre() {
        return this.pre;
    }
}
