package dev.voidsponge.nyx.module;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/**
 * Base de tous les modules (fullbright, x-ray...).
 *
 * Un module = un état (activé ou non) + une touche + des hooks
 * appelés au bon moment : onEnable, onDisable, onTick.
 */
public abstract class Module {
	protected static final Minecraft mc = Minecraft.getInstance();

	private final String name;
	private final Category category;
	private final String description;
	private final KeyMapping key;
	private boolean enabled;

	protected Module(String name, Category category, String description, String id, int defaultScancode) {
		this.name = name;
		this.category = category;
		this.description = description;
		// La KeyMapping apparaît dans Options > Contrôles, donc le joueur peut la changer.
		this.key = new KeyMapping("key.nyx." + id, InputConstants.Type.KEYBOARD, defaultScancode, ModuleManager.CATEGORY);
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) return;
		this.enabled = enabled;
		if (enabled) onEnable();
		else onDisable();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getName() {
		return name;
	}

	public Category getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public KeyMapping getKey() {
		return key;
	}

	protected void onEnable() {
	}

	protected void onDisable() {
	}

	/** Appelé à chaque tick client (20 fois par seconde) tant que le module est actif. */
	public void onTick() {
	}
}
