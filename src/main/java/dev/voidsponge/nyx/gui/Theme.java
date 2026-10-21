package dev.voidsponge.nyx.gui;

/** Couleurs du client, au même endroit pour changer de thème facilement. */
public final class Theme {
	public static final int ACCENT_A = 0xFFB388FF; // violet
	public static final int ACCENT_B = 0xFF00E5FF; // cyan
	public static final int BACKGROUND = 0x99000000;
	public static final int TEXT = 0xFFFFFFFF;
	public static final int TEXT_DIM = 0xFFAAAAAA;

	// Menu (ClickGui)
	public static final int PANEL_HEADER = 0xF00E0E14;
	public static final int PANEL_BODY = 0xE0161620;
	public static final int ROW_HOVER = 0x20FFFFFF;
	public static final int SWITCH_OFF = 0xFF3A3A48;
	public static final int SHADOW = 0x50000000;

	private Theme() {
	}

	/**
	 * Couleur animée qui oscille entre ACCENT_A et ACCENT_B.
	 * {@code offset} décale la vague : chaque ligne du HUD a une teinte un peu différente.
	 */
	public static int accent(float offset) {
		double t = System.currentTimeMillis() / 1000.0;
		float k = (float) (Math.sin(t * 2.0 + offset) * 0.5 + 0.5);
		return lerp(ACCENT_A, ACCENT_B, k);
	}

	/** Remplace l'alpha (0-255) d'une couleur ARGB. */
	public static int withAlpha(int color, int alpha) {
		return (alpha << 24) | (color & 0x00FFFFFF);
	}

	/** Mélange deux couleurs ARGB, canal par canal. */
	public static int lerp(int a, int b, float k) {
		int r = (int) (((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * k);
		int g = (int) (((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * k);
		int bl = (int) ((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
		return 0xFF000000 | (r << 16) | (g << 8) | bl;
	}
}
