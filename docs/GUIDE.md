# Guide : comprendre Nyx Client

Ce guide explique comment le mod fonctionne, fichier par fichier, pour apprendre le modding Fabric en lisant du vrai code.

## 1. Les bases d'un mod Fabric

| Fichier | Rôle |
|---|---|
| `build.gradle` / `gradle.properties` | Versions de Minecraft, Fabric Loader, Fabric API. **Loom** (le plugin Gradle de Fabric) télécharge le jeu et le rend utilisable en dev. |
| `src/main/resources/fabric.mod.json` | La carte d'identité du mod : id, dépendances, point d'entrée, fichier de mixins. `"environment": "client"` = mod client uniquement. |
| `src/main/resources/nyx.mixins.json` | La liste des mixins à appliquer. |
| `NyxClient.java` | Le point d'entrée (`ClientModInitializer`). Fabric appelle `onInitializeClient()` une fois au lancement. |

Depuis la 26.1, Minecraft n'est plus obfusqué : les noms de classes que tu vois (`Minecraft`, `LocalPlayer`, `ModelBlockRenderer`...) sont les **vrais noms de Mojang**. Pour explorer le code du jeu : `./gradlew genSources` puis, dans IntelliJ, Ctrl+clic sur une classe.

## 2. Deux façons de modifier le jeu

### a) Les événements de Fabric API (la façon propre)

Fabric API expose des "hooks" prêts à l'emploi. Dans `NyxClient.java` :

```java
ClientTickEvents.END_CLIENT_TICK.register(NyxClient::onTick);      // chaque tick (20/s)
KeyMappingHelper.registerKeyMapping(key);                           // une touche configurable
HudElementRegistry.addLast(id, ModuleHud::extract);                 // un élément du HUD
```

Toujours chercher un événement Fabric avant d'écrire un mixin : c'est plus simple et plus compatible avec les autres mods.

### b) Les mixins (quand aucun événement n'existe)

Un mixin injecte ton code **à l'intérieur** d'une méthode du jeu, au chargement de la classe. Les annotations utilisées ici :

| Annotation | Ce qu'elle fait | Exemple dans Nyx |
|---|---|---|
| `@Mixin(Classe.class)` | Cible la classe à modifier | tous |
| `@Inject(at = @At("HEAD"), cancellable = true)` | Exécute du code au début, avec possibilité d'annuler la méthode (`ci.cancel()`) ou de forcer le retour (`cir.setReturnValue(...)`) | `LightmapMixin`, `BlockMixin` |
| `@At(value = "FIELD", opcode = PUTFIELD, shift = AFTER)` | S'injecte juste après l'écriture d'un champ | `EntityRendererMixin` |
| `@ModifyReturnValue` (MixinExtras) | Modifie la valeur renvoyée, en gardant l'originale | `MinecraftMixin` |
| `@Shadow` | Accède à un champ privé de la classe cible | `LightmapMixin` |

Convention : préfixer les méthodes injectées (`nyx$...`) pour éviter les collisions avec d'autres mods.

## 3. Les modules, un par un

### Fullbright (`N`)
`LightmapMixin` : la lightmap est une texture 16×16 qui dit "à tel niveau de lumière bloc + ciel, multiplie la couleur par ça". On annule son calcul et on la remplit de blanc : tout est éclairé au max.

### X-Ray (`X`)
Le monde est découpé en sections 16×16×16, transformées une fois pour toutes en "mesh" (des triangles) envoyé au GPU. D'où le `levelExtractor.allChanged()` quand on active/désactive : il faut tout reconstruire.

Trois mixins :
1. **Ne pas dessiner les blocs inutiles** : `ModelBlockRendererMixin` (renderer vanilla) et `AltModelBlockRendererImplMixin` (renderer Indigo de Fabric API, qui remplace le vanilla quand Fabric API est installé).
2. **Afficher toutes les faces des minerais** : `BlockMixin`. Normalement une face collée à de la pierre n'est pas dessinée (culling).
3. **Désactiver l'occlusion** : `VisGraphMixin`. Sinon le jeu considère qu'une grotte entourée de pierre est invisible et ne la rend même pas.

Limites (bons exercices) : les liquides et les coffres ont leur propre renderer et restent visibles. Ne marche pas avec Sodium (autre renderer, il faudrait des mixins dédiés).

### ESP (`Z`)
On réutilise l'effet "Glowing" (flèche spectrale) qui dessine déjà un contour à travers les murs :
- `MinecraftMixin` : `shouldEntityAppearGlowing` renvoie `true` pour les entités ciblées ;
- `EntityRendererMixin` : on change `outlineColor` dans le *render state* (bleu joueurs, rouge monstres, vert animaux, jaune objets).

Le rendu moderne sépare **extraction** (copier les données de l'entité dans un `RenderState`) et **dessin**. On modifie l'extraction, c'est le plus stable.

### Fly (`V`)
En solo, le jeu lance un vrai serveur dans le même processus (l'*integrated server*). Plutôt que de tromper le serveur, on lui demande de nous donner les capacités de vol (`mayfly`) via `mc.getSingleplayerServer()`, puis `onUpdateAbilities()` synchronise le client. Point important : `server.execute(...)` exécute le code **sur le thread du serveur**. Toucher aux objets du serveur depuis le thread client donne des bugs aléatoires.

### Menu (`Shift droit`) et HUD
- `ModuleScreen` : un `Screen` avec un `Button` par module. `init()` est rappelé à chaque redimensionnement.
- `ModuleHud` : la liste des modules actifs en haut à gauche.

## 4. Le garde-fou "solo uniquement"
Dans `NyxClient.onTick` : si `!client.hasSingleplayerServer()`, tous les modules sont coupés. Nyx est un projet d'apprentissage, pas un client pour serveurs.

## 5. Idées pour progresser
1. **Facile** : ajouter un module `AutoSprint` (`mc.player.setSprinting(true)` dans `onTick`).
2. **Facile** : rendre la liste des blocs X-Ray configurable dans le menu.
3. **Moyen** : sauvegarder l'état des modules et leur config dans un fichier JSON (`FabricLoader.getInstance().getConfigDir()`).
4. **Moyen** : cacher les liquides en X-Ray (mixin sur `LiquidBlockRenderer`).
5. **Avancé** : un "Tracers" (lignes vers les entités) avec l'événement de rendu du monde de Fabric API.
