package dev.voidsponge.nyx.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidsponge.nyx.mixin.ChunkAccessAccessor;
import dev.voidsponge.nyx.module.Category;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.render.NyxRenderTypes;
import dev.voidsponge.nyx.render.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.sdl.SDLScancode;

import java.util.ArrayList;
import java.util.List;

/**
 * Block ESP : une boîte colorée sur les coffres, barils, shulkers, spawners...
 *
 * Les "block entities" sont les blocs qui stockent des données en plus (inventaire,
 * minuteur...). Chaque chunk en garde une Map, qu'on lit via ChunkAccessAccessor.
 */
public class BlockEsp extends Module {
	private static final int CHUNK_RADIUS = 8;
	private static final int FILL_ALPHA = 0x40;
	private static final int LINE_ALPHA = 0xE0;

	public BlockEsp() {
		super("Block ESP", Category.RENDER, "Surligner coffres, shulkers et spawners", "block_esp", SDLScancode.SDL_SCANCODE_B);
	}

	/** Couleur selon le type de bloc, ou 0 pour l'ignorer. */
	private static int colorOf(BlockEntity blockEntity) {
		if (blockEntity instanceof TrappedChestBlockEntity) return 0xFFFF5555;
		if (blockEntity instanceof ChestBlockEntity) return 0xFFFFAA00;
		if (blockEntity instanceof BarrelBlockEntity) return 0xFFC08040;
		if (blockEntity instanceof ShulkerBoxBlockEntity) return 0xFFFF55FF;
		if (blockEntity instanceof EnderChestBlockEntity) return 0xFFAA55FF;
		if (blockEntity instanceof SpawnerBlockEntity) return 0xFF55FFFF;
		return 0;
	}

	/**
	 * Appelé par l'événement de rendu du monde (voir NyxClient).
	 * Les coordonnées sont relatives à la caméra : on décale tout de -camera.
	 */
	public void render(LevelRenderContext context) {
		if (mc.level == null || mc.player == null) return;

		Vec3 camera = context.levelState().cameraRenderState.pos;
		PoseStack poseStack = context.poseStack();
		poseStack.pushPose();
		poseStack.translate(-camera.x, -camera.y, -camera.z);

		// 1. On collecte les blocs à surligner
		List<AABB> boxes = new ArrayList<>();
		List<Integer> colors = new ArrayList<>();
		int pcx = mc.player.blockPosition().getX() >> 4;
		int pcz = mc.player.blockPosition().getZ() >> 4;

		for (int cx = pcx - CHUNK_RADIUS; cx <= pcx + CHUNK_RADIUS; cx++) {
			for (int cz = pcz - CHUNK_RADIUS; cz <= pcz + CHUNK_RADIUS; cz++) {
				if (!mc.level.getChunkSource().hasChunk(cx, cz)) continue;
				LevelChunk chunk = mc.level.getChunk(cx, cz);

				for (BlockEntity blockEntity : ((ChunkAccessAccessor) chunk).nyx$getBlockEntities().values()) {
					int color = colorOf(blockEntity);
					if (color == 0) continue;
					BlockPos pos = blockEntity.getBlockPos();
					boxes.add(new AABB(pos).deflate(0.01));
					colors.add(color);
				}
			}
		}
		if (boxes.isEmpty()) {
			poseStack.popPose();
			return;
		}

		// 2. Remplissage translucide, puis contour plus opaque, visibles à travers les murs
		context.submitNodeCollector().submitCustomGeometry(poseStack, NyxRenderTypes.ESP_BOX, (pose, buffer) -> {
			for (int i = 0; i < boxes.size(); i++) {
				RenderUtil.filledBox(pose, buffer, boxes.get(i), RenderUtil.withAlpha(colors.get(i), FILL_ALPHA));
			}
		});
		context.submitNodeCollector().submitCustomGeometry(poseStack, NyxRenderTypes.ESP_LINES, (pose, buffer) -> {
			for (int i = 0; i < boxes.size(); i++) {
				RenderUtil.outlinedBox(pose, buffer, boxes.get(i), RenderUtil.withAlpha(colors.get(i), LINE_ALPHA), 2.0f);
			}
		});

		poseStack.popPose();
	}
}
