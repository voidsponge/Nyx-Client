package dev.voidsponge.nyx.mixin.sodium;

import dev.voidsponge.nyx.module.ModuleManager;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * X-Ray avec Sodium : tout éclairer au maximum.
 * Un minerai enfoui est dans le noir total, et Sodium prend aussi la lumière des blocs
 * voisins (la pierre, à 0). Avec les shaders, notre Fullbright (qui modifie la lightmap)
 * ne suffit pas : on force la lumière de chaque bloc à 15 pendant la construction des chunks.
 */
@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
	@Unique
	private static final int FULL_LIGHT = 15 | 15 << 4 | 15 << 8;

	@ModifyVariable(method = "compute", at = @At("TAIL"), name = "bl")
	private int nyx$xrayLight(int bl) {
		return ModuleManager.XRAY.isEnabled() ? FULL_LIGHT : bl;
	}

	@ModifyVariable(method = "compute", at = @At("STORE"), name = "sl")
	private int nyx$xraySkyLight(int sl) {
		return ModuleManager.XRAY.isEnabled() ? 15 : sl;
	}
}
