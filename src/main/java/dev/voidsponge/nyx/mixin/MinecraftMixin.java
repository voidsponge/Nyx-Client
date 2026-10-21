package dev.voidsponge.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * ESP, partie 1 : le jeu demande "cette entité doit-elle avoir un contour ?".
 * @ModifyReturnValue (MixinExtras, fourni avec Fabric) permet de modifier la valeur
 * renvoyée sans réécrire la méthode : on garde l'original et on ajoute notre condition.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
	private boolean nyx$esp(boolean original, Entity entity) {
		return original || ModuleManager.ESP.shouldHighlight(entity);
	}
}
