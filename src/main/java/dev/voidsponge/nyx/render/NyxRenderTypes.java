package dev.voidsponge.nyx.render;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

/**
 * Nos propres "RenderTypes", visibles à travers les murs.
 *
 * Un RenderPipeline décrit COMMENT le GPU dessine : shaders, format des sommets,
 * mélange des couleurs, et le test de profondeur (depth test).
 * Le depth test, c'est ce qui cache un objet derrière un mur : chaque pixel compare
 * sa distance à celle déjà dessinée. Avec CompareOp.ALWAYS_PASS, le test réussit
 * toujours : on dessine par-dessus tout, d'où l'effet "ESP".
 *
 * On part des "snippets" de Minecraft (morceaux de pipeline réutilisables) et on
 * ne change que le depth test.
 */
public final class NyxRenderTypes {
	private static final DepthStencilState SEE_THROUGH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);

	private static final RenderPipeline BOX_PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath("nyx", "pipeline/esp_box"))
			.withDepthStencilState(SEE_THROUGH)
			.build()
	);

	private static final RenderPipeline LINES_PIPELINE = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath("nyx", "pipeline/esp_lines"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(SEE_THROUGH)
			.build()
	);

	/** Quads colorés (format POSITION_COLOR). */
	public static final RenderType ESP_BOX = RenderType.create("nyx_esp_box",
		RenderSetup.builder(BOX_PIPELINE).sortOnUpload().createRenderSetup());

	/** Lignes (format POSITION_COLOR_NORMAL_LINE_WIDTH). */
	public static final RenderType ESP_LINES = RenderType.create("nyx_esp_lines",
		RenderSetup.builder(LINES_PIPELINE).createRenderSetup());

	private NyxRenderTypes() {
	}

	/** Force le chargement de la classe au démarrage, pour enregistrer les pipelines tôt. */
	public static void init() {
	}
}
