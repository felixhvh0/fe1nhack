package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Event;

import javax.swing.text.html.parser.Entity;

public class PushEntityEvent extends Event {
    private final Entity pushed;
    private final Entity pusher;

    public PushEntityEvent(Entity pushed, Entity pusher) {
        this.pushed = pushed;
        this.pusher = pusher;
    }

    public Entity getPushed() {
        return this.pushed;
    }

    public Entity getPusher() {
        return this.pusher;
    }
}