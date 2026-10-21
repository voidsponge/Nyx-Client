package dev.voidsponge.nyx.gui;

import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Affiche "Nyx" puis la liste des modules actifs en haut à gauche de l'écran.
 *
 * Depuis la refonte du rendu, l'interface ne dessine plus directement :
 * on "extrait" un état de rendu (rectangles, textes...) que le jeu dessine ensuite.
 */
public final class ModuleHud {
	private ModuleHud() {
	}

	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();

		int y = 4;
		graphics.text(mc.font, "Nyx", 4, y, 0xFFB388FF);
		for (Module module : ModuleManager.MODULES) {
			if (!module.isEnabled()) continue;
			y += mc.font.lineHeight + 1;
			graphics.text(mc.font, module.getName(), 4, y, 0xFFFFFFFF);
		}
	}
}
