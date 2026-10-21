package dev.voidsponge.nyx;

import dev.voidsponge.nyx.gui.ModuleScreen;
import dev.voidsponge.nyx.gui.ModuleHud;
import dev.voidsponge.nyx.module.Module;
import dev.voidsponge.nyx.module.ModuleManager;
import dev.voidsponge.nyx.render.NyxRenderTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.sdl.SDLScancode;

/**
 * Point d'entrée du mod, déclaré dans fabric.mod.json ("entrypoints" > "client").
 * Fabric appelle onInitializeClient() une seule fois au lancement du jeu.
 */
public class NyxClient implements ClientModInitializer {
	public static final String MOD_ID = "nyx";
	public static final String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID)
		.map(mod -> mod.getMetadata().getVersion().getFriendlyString())
		.orElse("dev");

	private static final KeyMapping GUI_KEY = new KeyMapping("key.nyx.gui", SDLScancode.SDL_SCANCODE_RSHIFT, ModuleManager.CATEGORY);

	@Override
	public void onInitializeClient() {
		// 0. Charger nos pipelines de rendu (ESP à travers les murs) avant la première frame
		NyxRenderTypes.init();

		// 1. Enregistrer les touches pour qu'elles apparaissent dans Options > Contrôles
		KeyMappingHelper.registerKeyMapping(GUI_KEY);
		for (Module module : ModuleManager.MODULES) KeyMappingHelper.registerKeyMapping(module.getKey());

		// 2. Événement Fabric : appelé à la fin de chaque tick client
		ClientTickEvents.END_CLIENT_TICK.register(NyxClient::onTick);

		// 3. Élément de HUD : la liste des modules actifs en haut à gauche
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "module_list"), ModuleHud::extract);

		// 4. Rendu 3D dans le monde : avant le terrain translucide (eau, verre...)
		LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(context -> {
			if (ModuleManager.BLOCK_ESP.isEnabled()) ModuleManager.BLOCK_ESP.render(context);
			if (ModuleManager.TRACERS.isEnabled()) ModuleManager.TRACERS.render(context);
		});
	}

	private static void onTick(Minecraft client) {
		if (client.player == null) {
			// Retour au menu principal : on remet tout à zéro
			ModuleManager.disableAll();
			return;
		}

		// Garde-fou : Nyx est un outil d'apprentissage pour le solo, pas pour les serveurs.
		if (!client.hasSingleplayerServer()) {
			if (ModuleManager.MODULES.stream().anyMatch(Module::isEnabled)) {
				ModuleManager.disableAll();
				client.player.sendSystemMessage(Component.literal("[Nyx] Modules désactivés : solo uniquement.").withStyle(ChatFormatting.RED));
			}
			// On vide les clics en attente pour ne pas les traiter plus tard
			while (GUI_KEY.consumeClick()) { }
			for (Module module : ModuleManager.MODULES) while (module.getKey().consumeClick()) { }
			return;
		}

		while (GUI_KEY.consumeClick()) client.gui.setScreen(new ModuleScreen());

		for (Module module : ModuleManager.MODULES) {
			// consumeClick() renvoie true une fois par appui : c'est le bon outil pour un toggle
			while (module.getKey().consumeClick()) {
				module.toggle();
				client.player.sendSystemMessage(Component.literal("[Nyx] " + module.getName() + " ")
					.append(module.isEnabled()
						? Component.literal("ON").withStyle(ChatFormatting.GREEN)
						: Component.literal("OFF").withStyle(ChatFormatting.RED)));
			}
			if (module.isEnabled()) module.onTick();
		}
	}
}
