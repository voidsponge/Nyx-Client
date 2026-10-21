package dev.voidsponge.nyx.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Un "Accessor" : une interface que Mixin implémente pour nous donner accès
 * à un champ privé ({@code blockEntities}) sans réflexion.
 * Usage : {@code ((ChunkAccessAccessor) chunk).nyx$getBlockEntities()}
 */
@Mixin(ChunkAccess.class)
public interface ChunkAccessAccessor {
	@Accessor("blockEntities")
	Map<BlockPos, BlockEntity> nyx$getBlockEntities();
}
