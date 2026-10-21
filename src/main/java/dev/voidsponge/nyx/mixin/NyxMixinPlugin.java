package dev.voidsponge.nyx.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Plugin de mixins : décide, au chargement, quels mixins appliquer.
 *
 * Les mixins du paquet "sodium" visent des classes de Sodium. Si Sodium n'est pas
 * installé, ces classes n'existent pas : on ne les applique tout simplement pas.
 */
public class NyxMixinPlugin implements IMixinConfigPlugin {
	private static final boolean SODIUM = FabricLoader.getInstance().isModLoaded("sodium");

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.contains(".mixin.sodium.")) return SODIUM;
		return true;
	}

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
