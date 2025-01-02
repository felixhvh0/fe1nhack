package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Flight extends Module {

    public Flight() {
        super("Flight", "Makes you levitate", Category.MOVEMENT, true, false, false);
    }

    private final Setting<Flight.FlightModes> flightMode = this.register(new Setting<>("Mode", FlightModes.VANILLA));


    public enum FlightModes {
        VANILLA,
        PACKET
    }



    public void onUpdate() {
        if (!(flightMode.getValue() == FlightModes.VANILLA)) {
            ClientPlayerEntity player = mc.player;
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
            player.sendAbilitiesUpdate();
        } else if (flightMode.getValue() == FlightModes.VANILLA) {
            ClientPlayerEntity player = mc.player;
            if (player != null) {
                player.getAbilities().allowFlying = true;
                player.sendAbilitiesUpdate();
            }
        }
    }

    public void onDisable() {
        ClientPlayerEntity player = mc.player;
        player.getAbilities().allowFlying = false;
        player.getAbilities().flying = false;
        player.sendAbilitiesUpdate();
    }



    @Override
    public void onRender2D(Render2DEvent event) {
        //compatibility, dont remove
    }
}


