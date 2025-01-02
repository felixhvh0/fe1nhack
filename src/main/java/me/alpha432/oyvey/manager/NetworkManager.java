package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.mixin.accessor.AccessorClientWorld;
import me.alpha432.oyvey.util.Util;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NetworkManager implements Util {

    private static final Set<Packet<?>> PACKET_CACHE = new HashSet<>();

    public static void sendPacket(final Packet<?> p) {
        if (mc.getNetworkHandler() != null) {
            PACKET_CACHE.add(p);
            mc.getNetworkHandler().sendPacket(p);
        }
    }

    public static void sendSequencedPacket(final SequencedPacketCreator o) {
        if (mc.world != null) {
            PendingUpdateManager updater =
                    ((AccessorClientWorld) mc.world).hookGetPendingUpdateManager().incrementSequence();
            try {
                int i = updater.getSequence();
                Packet<ServerPlayPacketListener> packet = o.predict(i);
                Objects.requireNonNull(mc.getNetworkHandler()).sendPacket((Packet<?>) packet);
            } catch (Throwable e) {
                e.printStackTrace();
                if (updater != null) {
                    try {
                        updater.close();
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                        e.addSuppressed(e1);
                    }
                }
                throw e;
            }
            if (updater != null) {
                updater.close();
            }
        }
    }
    public static boolean isCached(Packet<?> p) {
        return PACKET_CACHE.contains(p);
    }
}
