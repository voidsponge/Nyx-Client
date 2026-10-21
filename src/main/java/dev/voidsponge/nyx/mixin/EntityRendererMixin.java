package dev.voidsponge.nyx.mixin;

import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ESP, partie 2 : la couleur du contour.
 *
 * Le rendu des entités se fait en deux temps : extractRenderState copie les infos
 * utiles de l'entité dans un "RenderState", puis le renderer dessine ce state.
 * On s'injecte juste APRÈS l'écriture du champ outlineColor (PUTFIELD) pour l'écraser.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
	@Inject(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;outlineColor:I", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
	private void nyx$espColor(T entity, S state, float partialTicks, CallbackInfo ci) {
		if (ModuleManager.ESP.shouldHighlight(entity)) {
			state.outlineColor = ModuleManager.ESP.getColor(entity);
		}
	}
}
