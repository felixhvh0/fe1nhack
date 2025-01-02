package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.Items;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastUse", "", Category.PLAYER, true, false, false);
    }

    private final Setting<Boolean> exp = this.register(new Setting<>("Exp",true));




    @Override public void onUpdate() {
        if (exp.getValue()) {
            if (nullCheck()) return;

            if (mc.player.isHolding(Items.EXPERIENCE_BOTTLE)) {
                mc.itemUseCooldown = 0;
            }
        }



    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }
}