package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.AttackEvent;
import me.alpha432.oyvey.event.impl.HandleBlockBreakingEvent;
import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;
//RX MODULE, EDIT by jaxui :3
public class AntiMiss extends Module {
    public Setting<Boolean> onlySword = this.register(new Setting<>("OnlySword", false));
    public AntiMiss() {
        super("AntiMiss", "Prevents you from swinging if you aren't in range of an enemy", Category.COMBAT, true, false, false);
    }

    @Subscribe
    public void onAttack(AttackEvent e) {
        if (!(mc.crosshairTarget instanceof EntityHitResult) && e.isPre() && (!this.onlySword.getValue() || this.onlySword.getValue() && (mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getOffHandStack().getItem() instanceof SwordItem))) {
            e.cancel();
        }

    }

    @Subscribe
    public void onBlockBreaking(HandleBlockBreakingEvent e) {
        if (!(mc.crosshairTarget instanceof EntityHitResult) && (!this.onlySword.getValue() || this.onlySword.getValue() && (mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getOffHandStack().getItem() instanceof SwordItem))) {
            e.cancel();
        }

    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }

    @Override
    public void onRender2D(me.alpha432.oyvey.event.impl.Render2DEvent event) {

    }
}

