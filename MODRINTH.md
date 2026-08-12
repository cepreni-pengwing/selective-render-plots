# Selective Render Plots

Selective Render Plots is the optional server bridge that connects
[Selective Render](https://modrinth.com/mod/selective-render) clients to exact PlotSquared
regions. It supports merged and irregular plots represented by multiple PlotSquared cuboids.

The project provides separate downloads for Paper and Fabric servers. Install only the JAR that
matches your server platform. The bridge performs no rendering by itself: every player using the
feature needs Selective Render on their Fabric client.

## Requirements

Client:

- Minecraft 1.20.1 with Fabric Loader
- Selective Render 1.7.4 or newer
- Fabric API and Sodium as required by Selective Render

Paper server:

- Paper 1.20.1 or a compatible Bukkit implementation
- Java 17
- PlotSquared 7.x, tested with 7.3.9

Fabric server:

- Minecraft 1.20.1 with Fabric Loader 0.15.11 or newer
- Fabric API 0.92.2+1.20.1
- The [ArdaCraft PlotSquared Fabric port](https://github.com/ArdaCraft/PlotSquared), tested with
  7.3.9-SNAPSHOT
- The Multiworld, Stimuli, and WorldEdit/FAWE dependencies required by that PlotSquared build

The regular Bukkit PlotSquared JAR cannot be used on a Fabric server.

## Installation

- Paper: place `selective-render-plots-paper-VERSION.jar` in `plugins/`.
- Fabric: place `selective-render-plots-fabric-VERSION.jar` in `mods/`.
- Install Selective Render and its client dependencies for every player who will use the feature.
- Restart the server after installation.

The `common` JAR is not an installable server mod or plugin.

## Usage

The integration is part of Selective Render's client-side command tree. `/sr` abbreviates
`/selectiverender`, `p` abbreviates `plot`, and `s` abbreviates `save`.

```text
/sr p
/sr p minY maxY [xzMargin]
/sr p s NAME minY maxY [xzMargin]
```

- `/sr p` toggles temporary isolation of the plot under the player.
- Custom Y values set inclusive vertical bounds and may be outside normal build height.
- The optional non-negative `xzMargin` expands the plot horizontally; omitting it keeps the exact
  PlotSquared boundaries.
- Saving creates one normal named Selective Render preset and immediately activates it, even when
  an irregular plot contains several internal cuboids.

Temporary plot mode is cleared on disconnect or dimension change. Saved plot presets use Selective
Render's normal local, server- and dimension-specific configuration.

## Permissions and behavior

The permission is `selectiverender.plot.solo`. Paper permissions can be managed through standard
Bukkit permission plugins such as LuckPerms. On Fabric, access follows the permission handling of
the ArdaCraft PlotSquared stack.

Selective Render Plots only answers an explicit request from a permitted client with plot block
boundaries. It does not modify chunks, plots, collisions, render distance, world state, or network
chunk loading. Responses are capped at 256 cuboids per logical plot.

Rendering behavior and its limitations belong to the Selective Render client. The bridge uses
Selective Render protocol version 2; the optional X/Z margin is applied client-side.

Licensed under GPL-3.0-only. PlotSquared is a separate GPL-3.0 runtime dependency and is not
included in the distributed JARs.
