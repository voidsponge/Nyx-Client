package dev.voidsponge.nyx.module.modules;

import dev.voidsponge.nyx.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.sdl.SDLScancode;

/**
 * ESP : contour lumineux autour des entités, visible à travers les murs.
 *
 * On réutilise l'effet "Glowing" de Minecraft (celui de la flèche spectrale) :
 * MinecraftMixin dit au jeu que l'entité brille, EntityRendererMixin choisit la couleur.
 */
public class Esp extends Module {
	public Esp() {
		super("ESP", "Voir les entités à travers les murs", "esp", SDLScancode.SDL_SCANCODE_Z);
	}

	public boolean shouldHighlight(Entity entity) {
		if (!isEnabled() || entity == mc.player) return false;
		return entity instanceof LivingEntity || entity instanceof ItemEntity;
	}

	/** Couleur ARGB du contour selon le type d'entité. */
	public int getColor(Entity entity) {
		if (entity instanceof Player) return 0xFF3FA9F5;   // bleu : joueurs
		if (entity.getType().getCategory() == MobCategory.MONSTER) return 0xFFFF4040; // rouge : monstres
		if (entity instanceof ItemEntity) return 0xFFFFD23F; // jaune : objets au sol
		return 0xFF4CD964;                                 // vert : animaux et autres
	}
}
