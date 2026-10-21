package dev.voidsponge.nyx.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.render.NyxRenderTypes;
import dev.voidsponge.nyx.render.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.sdl.SDLScancode;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracers : une ligne du centre de l'écran vers chaque entité proche.
 *
 * La ligne part un peu devant la caméra (sinon elle serait réduite à un point),
 * et on interpole la position de l'entité avec le "partial tick" pour qu'elle
 * suive le mouvement de façon fluide entre deux ticks.
 */
public class Tracers extends Module {
	private static final double MAX_DISTANCE = 128;
	private static final int LINE_ALPHA = 0xC0;

	public Tracers() {
		super("Tracers", "Lignes vers les entités proches", "tracers", SDLScancode.SDL_SCANCODE_K);
	}

	public void render(LevelRenderContext context) {
		if (mc.level == null || mc.player == null) return;

		float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		Vec3 camera = context.levelState().cameraRenderState.pos;
		Vec3 start = camera.add(mc.player.getViewVector(partialTick).scale(0.5));

		List<Vec3> targets = new ArrayList<>();
		List<Integer> colors = new ArrayList<>();
		for (Entity entity : mc.level.entitiesForRendering()) {
			if (entity == mc.player || !(entity instanceof LivingEntity) || !entity.isAlive()) continue;
			Vec3 pos = entity.getPosition(partialTick);
			if (pos.distanceToSqr(camera) > MAX_DISTANCE * MAX_DISTANCE) continue;
			targets.add(pos.add(0, entity.getBbHeight() / 2, 0));
			// Même code couleur que l'ESP : bleu joueurs, rouge monstres, vert le reste
			colors.add(ModuleManager.ESP.getColor(entity));
		}
		if (targets.isEmpty()) return;

		PoseStack poseStack = context.poseStack();
		poseStack.pushPose();
		poseStack.translate(-camera.x, -camera.y, -camera.z);
		context.submitNodeCollector().submitCustomGeometry(poseStack, NyxRenderTypes.ESP_LINES, (pose, buffer) -> {
			for (int i = 0; i < targets.size(); i++) {
				Vec3 end = targets.get(i);
				RenderUtil.line(pose, buffer,
					(float) start.x, (float) start.y, (float) start.z,
					(float) end.x, (float) end.y, (float) end.z,
					RenderUtil.withAlpha(colors.get(i), LINE_ALPHA), 1.5f);
			}
		});
		poseStack.popPose();
	}
}
