package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class VisualRange extends Module {
    public Setting<Boolean> compact;
    public Setting<Boolean> displayPosition; // New boolean setting for showing coordinates

    private final List<PlayerEntity> players;
    private final MinecraftClient mc;

    private final Setting<Boolean> sound = this.register(new Setting<>("Sound", true));

    public VisualRange() {
        super("VisualRange", "Notifies about players getting in or out of render", Category.RENDER, true, false, false);
        this.displayPosition = register(new Setting<>("DisplayPosition", false)); // Default is true, you can toggle this
        this.players = new CopyOnWriteArrayList<>();
        this.mc = MinecraftClient.getInstance(); // Access Minecraft client
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.players.clear();
        if (fullNullCheck()) return;

        // Add existing players to the list
        for (PlayerEntity entity : mc.world.getPlayers()) {
            if (entity != mc.player) {
                this.players.add(entity);
            }
        }
    }

    @Override
    public void onUpdate() {
        List<AbstractClientPlayerEntity> currentPlayers = mc.world.getPlayers();

        // Check for new players entering the visual range
        for (PlayerEntity entity : currentPlayers) {
            if (!this.players.contains(entity) && !entity.equals(mc.player)) {
                // If the displayPosition setting is true, fetch the player's coordinates
                String message = "[VisualRange] "+Formatting.AQUA+entity.getDisplayName().getString() +Formatting.GRAY+ " entered visual range";

                if ((Boolean) this.displayPosition.getValue()) {
                    // Round the coordinates to integers
                    int x = (int) Math.round(entity.getX());
                    int y = (int) Math.round(entity.getY());
                    int z = (int) Math.round(entity.getZ());

                    // Format the message with the rounded coordinates
                    String coordinates = String.format("[%d, %d, %d]", x, y, z);
                    message += " at " + coordinates;
                }
                Command.sendRawMessage(message);
                this.players.add(entity);
                if (sound.getValue()) {
                mc.player.getWorld().playSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP , mc.player.getSoundCategory(), 1, 3, false);
            }}
        }

        // Check for players leaving the visual range
        for (PlayerEntity entity : this.players) {
            if (!currentPlayers.contains(entity) && !entity.equals(mc.player)) {
                // If the displayPosition setting is true, fetch the player's coordinates
                String message = "[VisualRange] "+Formatting.AQUA+entity.getDisplayName().getString() + Formatting.GRAY+ " left visual range";

                if ((Boolean) this.displayPosition.getValue()) {
                    // Round the coordinates to integers
                    int x = (int) Math.round(entity.getX());
                    int y = (int) Math.round(entity.getY());
                    int z = (int) Math.round(entity.getZ());

                    // Format the message with the rounded coordinates
                    String coordinates = String.format("[%d, %d, %d]", x, y, z);
                    message += " at " + coordinates;
                }
                    Command.sendRawMessage(message);

                this.players.remove(entity);
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        // Optional rendering code (if needed)
    }
}