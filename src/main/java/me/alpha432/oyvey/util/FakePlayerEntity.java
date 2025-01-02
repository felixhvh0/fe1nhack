package me.alpha432.oyvey.util;

import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FakePlayerEntity extends OtherClientPlayerEntity implements Util {
    public static final AtomicInteger CURRENT_ID = new AtomicInteger(1000000);
    private final PlayerEntity player;

    public FakePlayerEntity(PlayerEntity player, String name) {
        super(MinecraftClient.getInstance().world, new GameProfile(UUID.fromString("2c83d964-298e-4559-b1bf-314f9ad63f7b"), name));
        this.player = player;
        this.copyPositionAndRotation(player);
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.headYaw = player.headYaw;
        this.prevHeadYaw = this.headYaw;
        this.bodyYaw = player.bodyYaw;
        this.prevBodyYaw = this.bodyYaw;
        Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(PlayerEntity player, GameProfile profile) {
        super(MinecraftClient.getInstance().world, profile);
        this.player = player;
        this.copyPositionAndRotation(player);
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.headYaw = player.headYaw;
        this.prevHeadYaw = this.headYaw;
        this.bodyYaw = player.bodyYaw;
        this.prevBodyYaw = this.bodyYaw;
        Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(PlayerEntity player) {
        this(player, player.getName().getString());
    }

    public void spawnPlayer() {
        if (mc.world != null) {
            this.unsetRemoved();
            mc.world.addEntity(this);
        }

    }

    public void despawnPlayer() {
        if (mc.world != null) {
            mc.world.removeEntity(this.getId(), RemovalReason.DISCARDED);
            this.setRemoved(RemovalReason.DISCARDED);
        }

    }

    public boolean method_29504() {
        return false;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }
}
