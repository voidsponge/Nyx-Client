package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * X-Ray, partie 2 : le "face culling".
 *
 * Pour économiser le GPU, le jeu ne dessine pas une face collée à un bloc plein
 * (la face d'un minerai contre de la pierre, par ex.). Comme la pierre est cachée
 * par l'X-Ray, on force l'affichage de toutes les faces des blocs visibles.
 */
@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private static void nyx$xrayFaces(BlockState state, BlockState neighborState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (ModuleManager.XRAY.isEnabled() && Xray.isVisible(state.getBlock())) {
			// Deux blocs identiques côte à côte (deux minerais de fer) : la face reste cachée
			cir.setReturnValue(neighborState.getBlock() != state.getBlock());
		}
	}
}
