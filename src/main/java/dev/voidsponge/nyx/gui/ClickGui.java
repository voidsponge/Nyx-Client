package dev.voidsponge.nyx.gui;

import dev.voidsponge.nyx.NyxClient;
import dev.voidsponge.nyx.module.Category;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Le menu (Shift droit), façon "ClickGUI" : un panneau par catégorie, déplaçable à la souris.
 *
 * Pas de widgets Minecraft ici : on dessine tout à la main avec des rectangles (fill) et du
 * texte, et on gère les clics nous-mêmes. C'est plus de code, mais on contrôle chaque pixel.
 *
 * - clic gauche sur un module : activer / désactiver
 * - clic gauche sur un titre + glisser : déplacer le panneau
 * - clic droit sur un titre : replier / déplier
 */
public class ClickGui extends Screen {
	private static final int PANEL_WIDTH = 112;
	private static final int HEADER_HEIGHT = 18;
	private static final int ROW_HEIGHT = 16;
	private static final int GAP = 12;

	// Statique : les panneaux gardent leur position et leur état entre deux ouvertures du menu
	private static final Map<Category, Panel> PANELS = new EnumMap<>(Category.class);
	/** Animation de chaque interrupteur, de 0 (OFF) à 1 (ON). */
	private static final Map<Module, Float> SWITCH_ANIM = new HashMap<>();

	private Panel dragging;
	private double dragOffsetX, dragOffsetY;
	private long lastFrame = System.nanoTime();

	public ClickGui() {
		super(Component.literal("Nyx Client"));
	}

	@Override
	protected void init() {
		if (!PANELS.isEmpty()) return;
		// Première ouverture : les panneaux côte à côte, centrés en haut de l'écran
		Category[] categories = Category.values();
		int total = categories.length * PANEL_WIDTH + (categories.length - 1) * GAP;
		int x = (this.width - total) / 2;
		for (Category category : categories) {
			PANELS.put(category, new Panel(category, x, 30));
			x += PANEL_WIDTH + GAP;
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		// Temps écoulé depuis la frame précédente, pour des animations indépendantes des FPS
		long now = System.nanoTime();
		float dt = Math.min((now - lastFrame) / 1.0E9f, 0.1f);
		lastFrame = now;

		if (dragging != null) {
			dragging.x = (int) (mouseX - dragOffsetX);
			dragging.y = (int) (mouseY - dragOffsetY);
		}

		Module hovered = null;
		for (Panel panel : PANELS.values()) {
			Module h = panel.extract(graphics, mouseX, mouseY, dt);
			if (h != null) hovered = h;
		}

		footer(graphics);
		if (hovered != null && dragging == null) tooltip(graphics, hovered, mouseX, mouseY);
	}

	/** Fond : un voile sombre plutôt que le flou par défaut, pour garder le jeu visible derrière. */
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, this.width, this.height, 0x70000008);
	}

	private void footer(GuiGraphicsExtractor graphics) {
		String brand = "Nyx";
		String rest = " " + NyxClient.VERSION + "  •  clic : activer  •  glisser : déplacer  •  clic droit : replier";
		int w = this.font.width(brand) + this.font.width(rest);
		int x = (this.width - w) / 2;
		int y = this.height - 16;
		graphics.text(this.font, brand, x, y, Theme.accent(0));
		graphics.text(this.font, rest, x + this.font.width(brand), y, Theme.TEXT_DIM);
	}

	private void tooltip(GuiGraphicsExtractor graphics, Module module, int mouseX, int mouseY) {
		String desc = module.getDescription();
		String key = "Touche : " + module.getKey().getTranslatedKeyMessage().getString();
		int w = Math.max(this.font.width(desc), this.font.width(key)) + 10;
		int h = this.font.lineHeight * 2 + 10;
		int x = Math.min(mouseX + 10, this.width - w - 2);
		int y = Math.min(mouseY + 10, this.height - h - 2);

		graphics.fill(x + 2, y + 2, x + w + 2, y + h + 2, Theme.SHADOW);
		graphics.fill(x, y, x + w, y + h, Theme.PANEL_HEADER);
		graphics.fill(x, y, x + 1, y + h, Theme.accent(0));
		graphics.text(this.font, desc, x + 5, y + 4, Theme.TEXT);
		graphics.text(this.font, key, x + 5, y + 6 + this.font.lineHeight, Theme.TEXT_DIM);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		double mx = click.x(), my = click.y();
		// On parcourt les panneaux pour trouver celui sous la souris
		for (Panel panel : PANELS.values()) {
			if (panel.isOverHeader(mx, my)) {
				if (click.button() == 0) {
					dragging = panel;
					dragOffsetX = mx - panel.x;
					dragOffsetY = my - panel.y;
				} else if (click.button() == 1) {
					panel.collapsed = !panel.collapsed;
				}
				return true;
			}
			Module module = panel.moduleAt(mx, my);
			if (module != null && click.button() == 0) {
				module.toggle();
				return true;
			}
		}
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		dragging = null;
		return super.mouseReleased(click);
	}

	/** Le jeu continue de tourner pendant que le menu est ouvert. */
	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/** Une colonne : un titre puis la liste des modules de sa catégorie. */
	private final class Panel {
		final Category category;
		final List<Module> modules = new ArrayList<>();
		int x, y;
		boolean collapsed;

		Panel(Category category, int x, int y) {
			this.category = category;
			this.x = x;
			this.y = y;
			for (Module module : ModuleManager.MODULES) {
				if (module.getCategory() == category) modules.add(module);
			}
		}

		int bodyHeight() {
			return collapsed ? 0 : modules.size() * ROW_HEIGHT + 2;
		}

		boolean isOverHeader(double mx, double my) {
			return mx >= x && mx < x + PANEL_WIDTH && my >= y && my < y + HEADER_HEIGHT;
		}

		Module moduleAt(double mx, double my) {
			if (collapsed || mx < x || mx >= x + PANEL_WIDTH) return null;
			int index = (int) Math.floor((my - y - HEADER_HEIGHT) / ROW_HEIGHT);
			return index >= 0 && index < modules.size() ? modules.get(index) : null;
		}

		/** Dessine le panneau et renvoie le module survolé (ou null). */
		Module extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float dt) {
			int bottom = y + HEADER_HEIGHT + bodyHeight();

			// Ombre portée
			graphics.fill(x + 3, y + 3, x + PANEL_WIDTH + 3, bottom + 3, Theme.SHADOW);

			// En-tête : fond sombre, liseré dégradé en haut, titre et compteur de modules actifs
			graphics.fill(x, y, x + PANEL_WIDTH, y + HEADER_HEIGHT, Theme.PANEL_HEADER);
			gradientLine(graphics, x, y, PANEL_WIDTH, 2);
			graphics.text(font, category.getDisplayName(), x + 6, y + 6, Theme.TEXT);
			long active = modules.stream().filter(Module::isEnabled).count();
			String counter = active + "/" + modules.size();
			graphics.text(font, counter, x + PANEL_WIDTH - font.width(counter) - 6, y + 6, Theme.TEXT_DIM);

			if (collapsed) return null;

			graphics.fill(x, y + HEADER_HEIGHT, x + PANEL_WIDTH, bottom, Theme.PANEL_BODY);

			Module hovered = null;
			int rowY = y + HEADER_HEIGHT + 1;
			for (int i = 0; i < modules.size(); i++) {
				Module module = modules.get(i);
				boolean hover = dragging == null && mouseX >= x && mouseX < x + PANEL_WIDTH && mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;
				if (hover) hovered = module;

				// Animation de l'interrupteur : on rapproche la valeur de sa cible à chaque frame
				float target = module.isEnabled() ? 1f : 0f;
				float anim = SWITCH_ANIM.getOrDefault(module, target);
				anim += (target - anim) * Math.min(1f, dt * 12f);
				SWITCH_ANIM.put(module, anim);

				int accent = Theme.accent(i * 0.5f);
				if (hover) graphics.fill(x, rowY, x + PANEL_WIDTH, rowY + ROW_HEIGHT, Theme.ROW_HOVER);
				if (anim > 0.01f) {
					// Fond teinté + barre d'accent à gauche quand le module est actif
					graphics.fill(x, rowY, x + PANEL_WIDTH, rowY + ROW_HEIGHT, Theme.withAlpha(accent, (int) (0x22 * anim)));
					graphics.fill(x, rowY, x + 2, rowY + ROW_HEIGHT, Theme.withAlpha(accent, (int) (0xFF * anim)));
				}

				int textColor = Theme.lerp(Theme.TEXT_DIM, Theme.TEXT, anim);
				graphics.text(font, module.getName(), x + 8, rowY + 4, textColor);
				toggleSwitch(graphics, x + PANEL_WIDTH - 22, rowY + 4, anim, accent);

				rowY += ROW_HEIGHT;
			}
			return hovered;
		}
	}

	/** Petit interrupteur ON/OFF : une piste qui se colore et un curseur qui glisse. */
	private static void toggleSwitch(GuiGraphicsExtractor graphics, int x, int y, float anim, int accent) {
		int w = 16, h = 8;
		int track = Theme.lerp(Theme.SWITCH_OFF, accent, anim);
		graphics.fill(x + 1, y, x + w - 1, y + h, track); // coins "arrondis" en retirant 1px
		graphics.fill(x, y + 1, x + w, y + h - 1, track);
		int knobX = x + 1 + Math.round(anim * (w - h));
		graphics.fill(knobX, y + 1, knobX + h - 2, y + h - 1, Theme.TEXT);
	}

	/** Une ligne dégradée violet → cyan, dessinée en fines bandes verticales. */
	private static void gradientLine(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		int step = 4;
		for (int i = 0; i < width; i += step) {
			int color = Theme.accent(i / (float) width * 3f);
			graphics.fill(x + i, y, Math.min(x + i + step, x + width), y + height, color);
		}
	}
}
