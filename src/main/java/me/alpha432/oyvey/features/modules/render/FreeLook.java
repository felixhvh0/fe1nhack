package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;

public class FreeLook extends Module {

    public FreeLook() {
        super("FreeLook", "Free Look", Category.RENDER, true, false, false);
    }


    @Override
    public void onRender2D(me.alpha432.oyvey.features.impl.Render2DEvent event) {
        // Compatibility method, no changes needed here
    }
}
