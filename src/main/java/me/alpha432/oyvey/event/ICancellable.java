package me.alpha432.oyvey.event;

public interface ICancellable {
    void setCancelled(boolean cancelled);
    default void cancel() { setCancelled(true); }
    boolean isCancelled();
}
