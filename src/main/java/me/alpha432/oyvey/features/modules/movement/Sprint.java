package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Sprint extends Module {

    public enum sprintMode {
        LEGIT,
        RAGE
    }
    public Setting<sprintMode> mode = this.register(new Setting("Mode", sprintMode.LEGIT));
    public Sprint() {
        super("Sprint", "Sprints (HIGHLY CHINESE)", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        mc.player.setSprinting(
                mc.player.getHungerManager().getFoodLevel() > 6 && !mc.player.horizontalCollision && moveCheck()
        );
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    @Override
    public void onRender2D(me.alpha432.oyvey.event.impl.Render2DEvent event) {

    }

    public boolean moveCheck() {
        if (mode.getValue() == sprintMode.LEGIT) {
            return mc.player.input.movementForward > 0;
        } else {
            if (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0) return true;
            return false;
        }
    }
}
