package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.impl.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class NameProtect extends Module {
    public NameProtect() {
        super("NameProtect", "Changes your name client-side for your awesome crystal pvp media", Category.MISC, true, false, false);
        INSTANCE = this;
    }

    public static NameProtect INSTANCE;
    public final Setting<String> newName = this.register(new Setting("cool ass mfer", "Fe!n"));

    public String getFakeName() {
        return newName.getValue();
    }

    @Override
    public void onRender2D(Render2DEvent event) {

    }
}
