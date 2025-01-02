package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.render.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {

    /**
     * Modifies the arguments for the method call to NativeImage.setColor during LightmapTextureManager's update method.
     * Ensures that if Fullbright is active and in GAMMA mode, the brightness is set to maximum.
     *
     * @param args The arguments passed to the target method.
     */
    @ModifyArgs(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"
            )
    )
    private void onUpdateModifyArgs(Args args) {
            Fullbright fullbright = Fullbright.getInstance();
            // Ensure Fullbright instance is valid and enabled
            if (fullbright.isEnabled() && fullbright.mode.getValue() == Fullbright.Mode.GAMMA) {
                // Set the brightness to maximum (ARGB: 0xFFFFFFFF)
                Command.sendMessage("Fullbright mode enabled");
                args.set(2, 0xFFFFFFFF);
            }
    }
}
