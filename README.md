# Selective Render Plots

[Download Selective Render Plots on Modrinth](https://modrinth.com/plugin/selective-render-plots)

The existing SRP 1.1.0 bridge is compatible with Selective Render 1.9.0; no server update or
protocol change is required for the client release.

Selective Render Plots connects PlotSquared servers to
[Selective Render](https://modrinth.com/mod/selective-render). It sends the exact
shape of the plot under a player to their Fabric client, including merged and irregular plots
represented by multiple PlotSquared regions.

The project provides separate server JARs for Paper and Fabric. Both use the same protocol and
work with the same Selective Render client. The bridge performs no rendering by itself;
Selective Render must be installed on the connecting client.

## Usage

The commands are part of Selective Render's client-side command tree. `/sr` is the short alias
for `/selectiverender`, `p` for `plot`, and `s` for `save`.

```text
/sr plot
/sr plot [minY] [maxY] [xzMargin]
/sr plot clear
/sr plot save NAME [minY] [maxY] [xzMargin]
/sr p s NAME [minY] [maxY] [xzMargin]
```

- `/sr plot` adds the plot under the player to temporary isolation. Using it again on an active
  plot removes only that plot, so several plots can be rendered together.
- `/sr plot clear` clears the complete temporary plot group.
- Optional Y values set custom inclusive vertical bounds. Omitted values use the client's configured
  minimum (initially `-100`) and maximum `400`.
- A positive `xzMargin` expands the complete plot shape horizontally; a negative value shrinks it.
  A margin that would erase the entire plot shape is rejected.
- `/sr plot save NAME` permanently saves the exact plot shape as one normal Selective Render
  preset and immediately activates it. It accepts the same optional Y values and margin.
- `s` is the short alias for `save`.
- Only the first plot in an empty selection automatically enables rendering. Switch it off with
  `/sr t` to collect further plots without hiding the world; `/sr t` enables the selection again.

Y values and margins accept whole numbers, including Y values outside the dimension's normal build
range. Omitting the margin preserves the exact plot bounds. Preset names must be unique; delete or
rename an existing preset before reusing its name.

## Plot regions and presets

Temporary plot mode exists only in client memory, but its plot groups and enabled state survive
reconnects and dimension changes during the current Minecraft session. They remain separate per
server or world and dimension. `/sr plot save` stores the result in Selective Render's normal
server- and dimension-specific configuration.

A merged or irregular plot may contain several internal cuboids, but it appears as one named
entry in `/sr list`. Toggle, hide, rename, and delete operations treat every internal cuboid as
one preset. Active hide regions continue to be subtracted while temporary plot mode is enabled.
The protocol accepts up to 256 PlotSquared regions per plot.

## Requirements

Client:

- Minecraft 1.20.1 with Fabric Loader
- Selective Render 1.8.0 or newer; [1.9.0](https://github.com/cepreni-pengwing/selective-render/releases/tag/v1.9.0) is recommended
- Fabric API and Sodium as required by Selective Render

Paper server:

- Paper 1.20.1 or a compatible Bukkit implementation
- Java 17
- PlotSquared 7.x, tested with 7.3.9

Fabric server:

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or newer
- Fabric API 0.92.2+1.20.1
- [ArdaCraft PlotSquared Fabric](https://github.com/ArdaCraft/PlotSquared), tested with 7.3.9-SNAPSHOT
- The Multiworld, Stimuli, and WorldEdit/FAWE dependencies required by that PlotSquared build

## Installation

### Paper

1. Install PlotSquared on the server.
2. Place `selective-render-plots-paper-VERSION.jar` in the server's `plugins` directory.
3. Restart the server; do not use `/reload` for installation.

### Fabric

1. Install the ArdaCraft PlotSquared Fabric fork and all of its required dependencies.
2. Place `selective-render-plots-fabric-VERSION.jar` in the server's `mods` directory.
3. Restart the server.

For either platform, install Selective Render and its dependencies on each client. Join the server
and stand inside a claimed plot before using `/sr plot`.

Selective Render Plots does not modify chunks, collisions, permissions, plot data, or network
chunk loading. It only resolves the current plot through PlotSquared and sends its block
boundaries to clients that explicitly request them.

## Permission

`selectiverender.plot.solo` permits use of the integration.

On Paper it is granted by default and can be managed with a Bukkit-compatible permissions
plugin. On the ArdaCraft Fabric port, its built-in permission handler or Fabric LuckPerms
integration determines access according to the server's PlotSquared permission setup.
With LuckPerms on Fabric, grant the node explicitly to every intended user or group. On Paper, use
the same grant if the server's permission policy overrides the plugin's default or access is denied.
For example:

```text
/lp user PLAYER permission set selectiverender.plot.solo true
```

## Building

Requirements: JDK 21 for Gradle/Fabric Loom, plus internet access for the first build. Generated
classes target Java 17 and run on Minecraft 1.20.1 with Java 17.

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The installable JARs are generated in:

```text
paper/build/libs/selective-render-plots-paper-VERSION.jar
fabric/build/libs/selective-render-plots-fabric-VERSION.jar
```

The `common` module contains the shared protocol and response encoder. It is bundled into both
platform JARs and must not be installed separately.

## Target versions

- Minecraft 1.20.1
- Paper 1.20.1 with PlotSquared 7.3.9
- Fabric Loader 0.15.11 with ArdaCraft PlotSquared 7.3.9-SNAPSHOT
- Selective Render bridge protocol version 2 (the internal client-server message format)

## Known limitations

- The bridge does not change render distance, chunk loading, collision, or server world state.
- The Fabric build specifically requires the ArdaCraft PlotSquared Fabric port; the regular
  Bukkit PlotSquared JAR cannot replace it on a Fabric server.
- Plot region lists are processed linearly and capped at 256 cuboids per logical plot.
- Rendering behavior belongs to the Selective Render client and cannot be fixed by SRP alone.

## Support and contributing

Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com).

See [CONTRIBUTING.md](CONTRIBUTING.md) before submitting changes. Use the structured GitHub issue
forms for crashes, integration failures, and platform compatibility problems. Release history is
maintained in [CHANGELOG.md](CHANGELOG.md).

## License

GPL-3.0-only. See `LICENSE`.

PlotSquared is a separate GPL-3.0 runtime dependency and is not included in either Selective
Render Plots JAR. See `THIRD_PARTY_NOTICES.md` for dependency notices.
