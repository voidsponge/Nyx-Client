package dev.voidsponge.nyx.gui;

import dev.voidsponge.nyx.NyxClient;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Comparator;
import java.util.List;

/**
 * Le HUD du client :
 * - en haut à gauche, le "watermark" Nyx + version ;
 * - en haut à droite, la liste des modules actifs, triés du plus long au plus court
 *   (le look "escalier" classique des clients), avec un dégradé animé.
 *
 * Depuis la refonte du rendu, l'interface ne dessine plus directement :
 * on "extrait" un état de rendu (rectangles, textes...) que le jeu dessine ensuite.
 */
public final class ModuleHud {
	private static final int PADDING = 3;

	private ModuleHud() {
	}

	public static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		watermark(graphics, font);
		moduleList(graphics, font);
	}

	private static void watermark(GuiGraphicsExtractor graphics, Font font) {
		String name = "Nyx";
		String version = " " + NyxClient.VERSION;
		int width = font.width(name) + font.width(version) + PADDING * 2;
		int height = font.lineHeight + PADDING * 2;

		graphics.fill(4, 4, 4 + width, 4 + height, Theme.BACKGROUND);
		graphics.fill(4, 4, 4 + width, 5, Theme.accent(0)); // liseré du haut
		graphics.text(font, name, 4 + PADDING, 4 + PADDING + 1, Theme.accent(0));
		graphics.text(font, version, 4 + PADDING + font.width(name), 4 + PADDING + 1, Theme.TEXT_DIM);
	}

	private static void moduleList(GuiGraphicsExtractor graphics, Font font) {
		List<Module> active = ModuleManager.MODULES.stream()
			.filter(Module::isEnabled)
			.sorted(Comparator.comparingInt((Module m) -> font.width(m.getName())).reversed())
			.toList();

		int right = graphics.guiWidth() - 4;
		int y = 4;
		int lineHeight = font.lineHeight + 3;

		for (int i = 0; i < active.size(); i++) {
			String name = active.get(i).getName();
			int color = Theme.accent(i * 0.4f);
			int left = right - font.width(name) - PADDING * 2;

			graphics.fill(left, y, right, y + lineHeight, Theme.BACKGROUND);
			graphics.fill(right, y, right + 2, y + lineHeight, color); // barre d'accent à droite
			graphics.text(font, name, left + PADDING, y + 2, color);
			y += lineHeight;
		}
	}
}
