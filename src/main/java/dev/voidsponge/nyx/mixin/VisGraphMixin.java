package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * X-Ray, partie 3 : l'occlusion des sections.
 *
 * Le jeu calcule quelles sections de 16x16x16 sont visibles depuis la caméra
 * (une grotte entourée de pierre ne l'est pas). Avec l'X-Ray, tout doit l'être :
 * on empêche de marquer les blocs comme opaques dans ce calcul.
 */
@Mixin(VisGraph.class)
public abstract class VisGraphMixin {
	@Inject(method = "setOpaque", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(BlockPos pos, CallbackInfo ci) {
		if (ModuleManager.XRAY.isEnabled()) ci.cancel();
	}
}
