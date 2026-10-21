package dev.voidsponge.nyx.module.modules;

import dev.voidsponge.nyx.module.Module;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.sdl.SDLScancode;

import java.util.Set;

/**
 * X-Ray : on ne dessine que les minerais (et quelques blocs utiles).
 *
 * Trois mixins travaillent ensemble :
 * - ModelBlockRendererMixin / AltModelBlockRendererImplMixin : ne pas dessiner les autres blocs
 * - BlockMixin : forcer l'affichage de toutes les faces d'un minerai (sinon elles sont "cullées")
 * - VisGraphMixin : désactiver l'occlusion des sections, sinon les grottes cachées ne sont jamais rendues
 *
 * Les liquides (LiquidBlockRenderer) et les block entities (coffres) ont leur propre rendu :
 * ils restent visibles. Bon exercice pour la suite !
 */
public class Xray extends Module {
	private static final Set<Block> VISIBLE = Set.of(
		Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
		Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
		Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
		Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE,
		Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
		Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
		Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
		Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
		Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS,
		Blocks.SPAWNER, Blocks.BARREL
	);

	public Xray() {
		super("X-Ray", "Ne voir que les minerais", "xray", SDLScancode.SDL_SCANCODE_X);
	}

	public static boolean isVisible(Block block) {
		return VISIBLE.contains(block);
	}

	@Override
	protected void onEnable() {
		reloadChunks();
	}

	@Override
	protected void onDisable() {
		reloadChunks();
	}

	/** Les chunks sont "compilés" en mesh une seule fois : il faut tout reconstruire pour voir le changement. */
	private void reloadChunks() {
		if (mc.level != null && mc.levelExtractor != null) mc.levelExtractor.allChanged();
	}
}
