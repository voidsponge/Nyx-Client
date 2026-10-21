# Nyx Client

[![build](https://github.com/voidsponge/Nyx-Client/actions/workflows/build.yml/badge.svg)](https://github.com/voidsponge/Nyx-Client/actions/workflows/build.yml)

Mod client **Fabric** pour **Minecraft 26.3**, écrit pour apprendre le modding : mixins, événements Fabric API, rendu, keybinds, GUI.

> **Solo uniquement.** Les modules se coupent automatiquement dès que tu n'es pas sur un monde solo. Pas de bypass d'anti-cheat, pas d'usage sur serveur : c'est un projet d'apprentissage.

## Modules

| Module | Touche | Ce qu'il fait | Concept appris |
|---|---|---|---|
| Fullbright | `N` | Tout est éclairé | Mixin `@Inject` + `@Shadow`, lightmap |
| X-Ray | `X` | Seuls les minerais sont visibles | Rendu des chunks, face culling, occlusion |
| ESP | `Z` | Contour coloré des entités à travers les murs | `@ModifyReturnValue`, render states |
| Fly | `V` | Vol en survie | Integrated server, threads client/serveur |
| Menu | `Shift droit` | Activer/désactiver les modules à la souris | `Screen`, `Button` |

Toutes les touches sont modifiables dans *Options > Contrôles > Nyx Client*.

## Installer

1. Installer [Fabric Loader](https://fabricmc.net/use/) pour Minecraft 26.3.
2. Télécharger [Fabric API](https://modrinth.com/mod/fabric-api) (version 26.3) dans `.minecraft/mods`.
3. Télécharger `nyx-client-x.y.z.jar` depuis la [dernière release](https://github.com/voidsponge/Nyx-Client/releases/latest) et le mettre dans `.minecraft/mods`.

## Branches et releases

```
develop ──●──●──●───────●──●──►   le travail au quotidien (build à chaque push)
           \             \
main ───────●─────────────●────►  une PR develop → main = une release
          v0.1.0        v0.2.0
```

1. On travaille sur `develop` (ou une branche qui part de `develop`).
2. Avant de publier : augmenter `version` dans `gradle.properties`.
3. Ouvrir une PR `develop` → `main`. La CI vérifie que le build passe et que la version n'existe pas déjà.
4. Au merge, `release.yml` compile le jar et publie la release `vX.Y.Z` avec le jar attaché.

## Développer

Prérequis : **JDK 25**.

```bash
./gradlew build        # compile le jar dans build/libs/
./gradlew runClient    # lance Minecraft avec le mod
./gradlew genSources   # décompile Minecraft pour lire son code dans l'IDE
```

IntelliJ IDEA + le plugin *Minecraft Development* est le combo le plus confortable.

## Apprendre

Le fichier [docs/GUIDE.md](docs/GUIDE.md) explique chaque fichier, chaque mixin et propose des exercices pour aller plus loin.

```
src/main/java/dev/voidsponge/nyx/
├── NyxClient.java         point d'entrée, touches, tick, garde-fou solo
├── module/                Module (base), ModuleManager, et les modules
├── gui/                   menu (ModuleScreen) et HUD (ModuleHud)
└── mixin/                 les injections dans le code du jeu
```

## Licence

MIT
