package dev.voidsponge.nyx.mixin.sodium;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * X-Ray avec Sodium (et donc avec les shaders d'Iris, qui passent par Sodium).
 * Sodium a son propre "BlockRenderer" pour construire les chunks : même idée que
 * ModelBlockRendererMixin, on annule le dessin des blocs qui ne sont pas des minerais.
 */
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class SodiumBlockRendererMixin {
	@Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
		if (ModuleManager.XRAY.isEnabled() && !Xray.isVisible(state.getBlock())) ci.cancel();
	}
}
