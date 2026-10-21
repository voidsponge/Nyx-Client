package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * X-Ray, partie 1 bis (renderer Indigo de Fabric API).
 *
 * Fabric API remplace le dessin des chunks par son propre renderer, "Indigo".
 * Quand Fabric API est installé, ModelBlockRendererMixin ne suffit donc pas :
 * il faut aussi s'accrocher ici. Bonne leçon : un mixin peut viser une classe
 * d'un autre mod, pas seulement du jeu.
 */
@SuppressWarnings("UnstableApiUsage")
@Mixin(AltModelBlockRendererImpl.class)
public abstract class AltModelBlockRendererImplMixin {
	@Inject(method = "tesselateBlock", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(QuadEmitter output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel model, long seed, CallbackInfo ci) {
		if (ModuleManager.XRAY.isEnabled() && !Xray.isVisible(state.getBlock())) ci.cancel();
	}
}
