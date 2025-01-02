package me.alpha432.oyvey.event.impl;

import me.alpha432.oyvey.event.Cancellable;
import me.alpha432.oyvey.event.Event;
import me.alpha432.oyvey.manager.NetworkManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;


public class PacketEvent extends Event {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Receive2 extends Cancellable {
        public Packet<?> packet;
        public ClientConnection connection;
        public Receive2(Packet<?> packet) {

            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
        public Packet<?> getPacket() {
            return packet;
        }
    }


    public static class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    public static class Outbound extends PacketEvent {
        private final boolean cached;

        public Outbound(Packet<?> packet) {
            super(packet);
            this.cached = NetworkManager.isCached(packet);
        }

        public boolean isClientPacket() {
            return this.cached;
        }
    }

}