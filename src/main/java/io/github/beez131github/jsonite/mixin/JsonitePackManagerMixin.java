package io.github.beez131github.jsonite.mixin;

import io.github.beez131github.jsonite.Jsonite;
import io.github.beez131github.jsonite.JsonitePackTracker;
import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourcePackManager.class)
public class JsonitePackManagerMixin {
    @Inject(method = "disable", at = @At("HEAD"), cancellable = true)
    private void onDisable(String profile, CallbackInfoReturnable<Boolean> cir) {
        Jsonite.LOGGER.info("Running pack locker", profile);
        if (JsonitePackTracker.hasJsoniteContent()) {
            Jsonite.LOGGER.info("Preventing disable of pack: {}", profile);
            cir.setReturnValue(false);
        }
    }
}
