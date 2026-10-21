package dev.voidsponge.nyx.module;

/** Les catégories de modules : une colonne par catégorie dans le menu. */
public enum Category {
	RENDER("Render"),
	WORLD("World"),
	MOVEMENT("Movement");

	private final String displayName;

	Category(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
