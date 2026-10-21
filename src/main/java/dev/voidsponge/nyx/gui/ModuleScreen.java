package dev.voidsponge.nyx.gui;

import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Menu des modules (Shift droit) : un bouton par module pour l'activer ou le désactiver.
 */
public class ModuleScreen extends Screen {
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 20;
	private static final int SPACING = 24;

	public ModuleScreen() {
		super(Component.literal("Nyx Client"));
	}

	/** init() est rappelé à chaque redimensionnement de la fenêtre : on y (re)crée les widgets. */
	@Override
	protected void init() {
		int x = this.width / 2 - BUTTON_WIDTH / 2;
		int y = this.height / 2 - (ModuleManager.MODULES.size() * SPACING) / 2;

		for (Module module : ModuleManager.MODULES) {
			this.addRenderableWidget(Button.builder(label(module), button -> {
					module.toggle();
					button.setMessage(label(module));
				})
				.pos(x, y)
				.size(BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(Tooltip.create(Component.literal(module.getDescription())))
				.build());
			y += SPACING;
		}

		this.addRenderableWidget(Button.builder(Component.literal("Fermer"), button -> this.onClose())
			.pos(x, y + 8)
			.size(BUTTON_WIDTH, BUTTON_HEIGHT)
			.build());
	}

	private static Component label(Module module) {
		return Component.literal(module.getName() + " : ").append(module.isEnabled()
			? Component.literal("ON").withStyle(ChatFormatting.GREEN)
			: Component.literal("OFF").withStyle(ChatFormatting.RED));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		int top = this.height / 2 - (ModuleManager.MODULES.size() * SPACING) / 2 - 24;
		graphics.centeredText(this.font, this.title.getString(), this.width / 2, top - 4, Theme.accent(0));
		graphics.centeredText(this.font, "solo uniquement \u2022 Échap pour fermer", this.width / 2, top + 8, Theme.TEXT_DIM);
	}

	/** Le jeu continue de tourner pendant que le menu est ouvert. */
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
