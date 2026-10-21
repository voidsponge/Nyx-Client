package dev.voidsponge.nyx.module.modules;

import dev.voidsponge.nyx.module.Category;
import dev.voidsponge.nyx.module.Module;
import org.lwjgl.sdl.SDLScancode;

/**
 * Fullbright : tout est éclairé au maximum.
 *
 * Le module ne fait rien lui-même : c'est {@code LightmapMixin} qui lit
 * {@link #isEnabled()} et remplit la lightmap en blanc.
 */
public class Fullbright extends Module {
	public Fullbright() {
		super("Fullbright", Category.RENDER, "Voir dans le noir", "fullbright", SDLScancode.SDL_SCANCODE_N);
	}
}
