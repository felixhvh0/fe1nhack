package me.alpha432.oyvey.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.DisconnectEvent;
import me.alpha432.oyvey.event.impl.PushEntityEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.FakePlayerEntity;

public class FakePlayer extends Module {
    private FakePlayerEntity fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns fake player 4 testing", Category.PLAYER, true, false, false);
    }

    public void onEnable() {
        if (mc.player != null && mc.world != null) {
            this.fakePlayer = new FakePlayerEntity(mc.player, "FEIN");
            this.fakePlayer.spawnPlayer();
        }

    }

    public void onDisable() {
        if (this.fakePlayer != null) {
            this.fakePlayer.despawnPlayer();
            this.fakePlayer = null;
        }

    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        this.fakePlayer = null;
        this.disable();
    }

    @Subscribe
    public void onPushEntity(PushEntityEvent event) {
        if (event.getPushed().equals(mc.player) && event.getPusher().equals(this.fakePlayer)) {
        }

    }
}
