package dev.voidsponge.nyx.module.modules;

import dev.voidsponge.nyx.module.Module;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import org.lwjgl.sdl.SDLScancode;

import java.util.function.Consumer;

/**
 * Fly : vol façon créatif, en survie.
 *
 * En solo, le jeu lance un vrai serveur dans le même processus (l'"integrated server").
 * Plutôt que de tromper le serveur, on lui demande directement de nous autoriser à voler :
 * on modifie les capacités (Abilities) du ServerPlayer, et le serveur synchronise le client tout seul.
 * C'est pour ça que ce module ne marche qu'en solo, et c'est voulu.
 */
public class Flight extends Module {
	private static final float FLY_SPEED = 0.1f; // 0.05 = vitesse créatif normale

	public Flight() {
		super("Fly", "Voler en survie", "flight", SDLScancode.SDL_SCANCODE_V);
	}

	@Override
	protected void onEnable() {
		onServerPlayer(player -> {
			Abilities abilities = player.getAbilities();
			abilities.mayfly = true;
			abilities.flying = true;
			abilities.setFlyingSpeed(FLY_SPEED);
			player.onUpdateAbilities(); // envoie les nouvelles capacités au client
		});
	}

	@Override
	public void onTick() {
		// Changer de mode de jeu (/gamemode) réinitialise les capacités : on les remet si besoin.
		if (mc.player != null && !mc.player.getAbilities().mayfly) onEnable();
	}

	@Override
	protected void onDisable() {
		onServerPlayer(player -> {
			Abilities abilities = player.getAbilities();
			abilities.setFlyingSpeed(0.05f);
			if (player.isCreative() || player.isSpectator()) {
				player.onUpdateAbilities();
				return;
			}
			abilities.mayfly = false;
			abilities.flying = false;
			player.resetFallDistance(); // pas de dégâts de chute en coupant le vol
			player.onUpdateAbilities();
		});
	}

	/**
	 * Exécute une action sur notre joueur côté serveur.
	 * {@code server.execute} est obligatoire : le serveur tourne sur son propre thread,
	 * et toucher à ses objets depuis le thread du client provoquerait des bugs aléatoires.
	 */
	private void onServerPlayer(Consumer<ServerPlayer> action) {
		IntegratedServer server = mc.getSingleplayerServer();
		if (server == null || mc.player == null) return;

		var uuid = mc.player.getUUID();
		server.execute(() -> {
			ServerPlayer player = server.getPlayerList().getPlayer(uuid);
			if (player != null) action.accept(player);
		});
	}
}
