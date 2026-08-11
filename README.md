# Selective Render Plots

Selective Render Plots connects PlotSquared servers to
[Selective Render](https://github.com/cepreni-pengwing/selective-render). It sends the exact
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
/sr plot save NAME minY maxY
/sr p s NAME minY maxY
```

- `/sr plot` toggles temporary isolation of the PlotSquared plot under the player.
- `/sr plot save NAME minY maxY` permanently saves the exact plot shape as one normal Selective
  Render preset and immediately activates it. Both Y boundaries are inclusive.
- `s` is the short alias for `save`.

Both Y values accept any whole number, including values outside the dimension's normal build
range. Preset names must be unique; delete or rename an existing preset before reusing its name.

## Plot regions and presets

Temporary plot mode exists only in client memory and is cleared when the player disconnects or
changes dimension. `/sr plot save` stores the result in Selective Render's normal server- and
dimension-specific configuration.

A merged or irregular plot may contain several internal cuboids, but it appears as one named
entry in `/sr list`. Toggle, hide, rename, and delete operations treat every internal cuboid as
one preset. Active hide regions continue to be subtracted while temporary plot mode is enabled.
The protocol accepts up to 256 PlotSquared regions per plot.

## Requirements

Client:

- Minecraft 1.20.1 with Fabric Loader
- [Selective Render 1.7.0](https://github.com/cepreni-pengwing/selective-render/releases/tag/v1.7.0) or newer
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

For either platform, install the matching Selective Render version and its dependencies on each
client. Join the server and stand inside a claimed plot before using `/sr plot`.

Selective Render Plots does not modify chunks, collisions, permissions, plot data, or network
chunk loading. It only resolves the current plot through PlotSquared and sends its block
boundaries to clients that explicitly request them.

## Permission

`selectiverender.plot.solo` permits use of the integration.

On Paper it is granted by default and can be managed with a Bukkit-compatible permissions
plugin. On the ArdaCraft Fabric port, its built-in permission handler or Fabric LuckPerms
integration determines access according to the server's PlotSquared permission setup.

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
- Selective Render protocol version 2

## License

MIT. See `LICENSE`.

PlotSquared is a separate GPL-3.0 runtime dependency and is not included in either Selective
Render Plots JAR. See `THIRD_PARTY_NOTICES.md` for dependency notices.
