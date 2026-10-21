package dev.voidsponge.nyx.mixin.sodium;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** X-Ray avec Sodium : afficher toutes les faces des minerais (équivalent de BlockMixin). */
@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class AbstractBlockRenderContextMixin {
	@Shadow
	protected BlockState state;

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private void nyx$xray(Direction facing, CallbackInfoReturnable<Boolean> cir) {
		if (ModuleManager.XRAY.isEnabled() && Xray.isVisible(state.getBlock())) cir.setReturnValue(true);
	}
}
