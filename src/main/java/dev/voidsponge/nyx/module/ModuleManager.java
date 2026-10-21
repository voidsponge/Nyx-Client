package dev.voidsponge.nyx.module;

import dev.voidsponge.nyx.module.modules.BlockEsp;
import dev.voidsponge.nyx.module.modules.Esp;
import dev.voidsponge.nyx.module.modules.Flight;
import dev.voidsponge.nyx.module.modules.Fullbright;
import dev.voidsponge.nyx.module.modules.Tracers;
import dev.voidsponge.nyx.module.modules.Xray;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Registre central des modules. Les mixins y accèdent en statique
 * (ex. {@code ModuleManager.XRAY.isEnabled()}) car ils n'ont pas
 * d'autre moyen simple de retrouver l'instance du module.
 */
public final class ModuleManager {
	public static final KeyMapping.Category CATEGORY =
		KeyMapping.Category.register(Identifier.fromNamespaceAndPath("nyx", "main"));

	public static final Fullbright FULLBRIGHT = new Fullbright();
	public static final Xray XRAY = new Xray();
	public static final Esp ESP = new Esp();
	public static final BlockEsp BLOCK_ESP = new BlockEsp();
	public static final Tracers TRACERS = new Tracers();
	public static final Flight FLIGHT = new Flight();

	public static final List<Module> MODULES = List.of(FULLBRIGHT, XRAY, ESP, BLOCK_ESP, TRACERS, FLIGHT);

	private ModuleManager() {
	}

	public static void disableAll() {
		for (Module module : MODULES) module.setEnabled(false);
	}
}
