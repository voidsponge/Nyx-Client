package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * X-Ray, partie 1 (renderer vanilla) : on annule le dessin des blocs non listés.
 * tesselateBlock transforme le modèle d'un bloc en triangles pour le mesh du chunk.
 */
@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
	@Inject(method = "tesselateBlock", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(BlockQuadOutput output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel model, long seed, CallbackInfo ci) {
		if (ModuleManager.XRAY.isEnabled() && !Xray.isVisible(state.getBlock())) ci.cancel();
	}
}
