package dev.voidsponge.nyx.mixin.sodium;

import dev.voidsponge.nyx.module.ModuleManager;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** X-Ray avec Sodium : couper l'occlusion des sections (équivalent de VisGraphMixin). */
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class SodiumWorldRendererMixin {
	@ModifyVariable(method = "setupTerrain", at = @At("HEAD"), argsOnly = true, name = "useOcclusionCulling")
	private boolean nyx$xray(boolean useOcclusionCulling) {
		return useOcclusionCulling && !ModuleManager.XRAY.isEnabled();
	}
}
