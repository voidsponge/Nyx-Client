package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * X-Ray, partie 4 : l'ombrage des coins ("ambient occlusion").
 *
 * Le jeu assombrit les coins d'un bloc selon les blocs pleins autour. Un minerai entouré
 * de pierre devient donc presque noir. En disant que chaque bloc laisse passer
 * toute la lumière (1.0), il n'y a plus d'ombre.
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
	@Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		if (ModuleManager.XRAY.isEnabled()) cir.setReturnValue(1.0f);
	}
}
