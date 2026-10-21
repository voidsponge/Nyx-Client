package dev.voidsponge.nyx.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;

/**
 * Petits outils de dessin 3D.
 *
 * Un cube = 6 faces, chaque face = 4 sommets (un "quad"). Chaque sommet a une
 * position et une couleur ARGB. Le GPU remplit l'intérieur des quads.
 */
public final class RenderUtil {
	private RenderUtil() {
	}

	public static void filledBox(PoseStack.Pose pose, VertexConsumer buffer, AABB box, int color) {
		float x0 = (float) box.minX, y0 = (float) box.minY, z0 = (float) box.minZ;
		float x1 = (float) box.maxX, y1 = (float) box.maxY, z1 = (float) box.maxZ;
		// Devant
		quad(pose, buffer, color, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0);
		// Derrière
		quad(pose, buffer, color, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1);
		// Gauche
		quad(pose, buffer, color, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1);
		// Droite
		quad(pose, buffer, color, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0);
		// Dessus
		quad(pose, buffer, color, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1);
		// Dessous
		quad(pose, buffer, color, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0);
	}

	private static void quad(PoseStack.Pose pose, VertexConsumer buffer, int color,
			float ax, float ay, float az, float bx, float by, float bz,
			float cx, float cy, float cz, float dx, float dy, float dz) {
		buffer.addVertex(pose, ax, ay, az).setColor(color);
		buffer.addVertex(pose, bx, by, bz).setColor(color);
		buffer.addVertex(pose, cx, cy, cz).setColor(color);
		buffer.addVertex(pose, dx, dy, dz).setColor(color);
	}

	/** Remplace l'alpha (0-255) d'une couleur ARGB. */
	public static int withAlpha(int color, int alpha) {
		return (alpha << 24) | (color & 0x00FFFFFF);
	}
}
