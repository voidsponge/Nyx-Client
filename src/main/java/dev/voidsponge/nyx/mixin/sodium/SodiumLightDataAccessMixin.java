package dev.voidsponge.nyx.mixin.sodium;

import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.module.modules.Xray;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * X-Ray avec Sodium : éclairer les minerais au maximum.
 * Un minerai enfoui est dans le noir total ; avec les shaders, notre Fullbright
 * (qui modifie la lightmap) ne suffit pas, donc on force la lumière du bloc à 15.
 */
@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
	@Unique
	private static final int FULL_LIGHT = 15 | 15 << 4 | 15 << 8;

	@Shadow
	protected BlockAndTintGetter level;

	@Shadow
	@Final
	private BlockPos.MutableBlockPos pos;

	@ModifyVariable(method = "compute", at = @At("TAIL"), name = "bl")
	private int nyx$xrayLight(int bl) {
		if (ModuleManager.XRAY.isEnabled() && Xray.isVisible(level.getBlockState(pos).getBlock())) return FULL_LIGHT;
		return bl;
	}
}
