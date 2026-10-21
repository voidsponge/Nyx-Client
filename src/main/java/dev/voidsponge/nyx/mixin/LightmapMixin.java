package dev.voidsponge.nyx.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fullbright.
 *
 * La lightmap est une petite texture (16x16) : en X le niveau de lumière des blocs,
 * en Y celui du ciel. Chaque pixel du monde est multiplié par la case correspondante.
 * Si on la remplit de blanc, tout est éclairé au maximum.
 */
@Mixin(Lightmap.class)
public abstract class LightmapMixin {
	// @Shadow : "ce champ existe déjà dans la classe cible, donne-moi accès"
	@Shadow
	@Final
	private GpuTexture texture;

	// @Inject au début (HEAD) de render(), cancellable : on peut annuler la méthode d'origine
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void nyx$fullbright(LightmapRenderState renderState, CallbackInfo ci) {
		if (ModuleManager.FULLBRIGHT.isEnabled() || ModuleManager.XRAY.isEnabled()) {
			RenderSystem.getDevice().createCommandEncoder().clearColorTexture(texture, new Vector4f(1));
			ci.cancel();
		}
	}
}
