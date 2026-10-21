package dev.voidsponge.nyx.module.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.voidsponge.nyx.mixin.ChunkAccessAccessor;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.render.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.rendertype.RenderTypes;
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

/**
 * Block ESP : une boîte colorée sur les coffres, barils, shulkers, spawners...
 *
 * Les "block entities" sont les blocs qui stockent des données en plus (inventaire,
 * minuteur...). Chaque chunk en garde une Map, qu'on lit via ChunkAccessAccessor.
 */
public class BlockEsp extends Module {
	private static final int CHUNK_RADIUS = 8;
	private static final int ALPHA = 0x55;

	public BlockEsp() {
		super("Block ESP", "Surligner coffres, shulkers et spawners", "block_esp", SDLScancode.SDL_SCANCODE_B);
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

		context.submitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.debugFilledBox(), (pose, buffer) -> {
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
						AABB box = new AABB(pos).inflate(0.002);
						RenderUtil.filledBox(pose, buffer, box, RenderUtil.withAlpha(color, ALPHA));
					}
				}
			}
		});

		poseStack.popPose();
	}
}
