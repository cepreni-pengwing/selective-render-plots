# Selective Render Plots

Selective Render Plots is a Paper plugin for Minecraft 1.20.1 that connects
PlotSquared servers to [Selective Render](https://github.com/cepreni-pengwing/selective-render).
It sends the exact shape of the plot under a player to their Fabric client, including
merged and non-rectangular plots represented by multiple PlotSquared regions.

The plugin performs no rendering by itself. Selective Render must be installed on
the connecting client; no Fabric mod is required on the server.

## Usage

The commands are added to Selective Render's existing client-side command tree.
`/sr` is the short alias for `/selectiverender`, `p` for `plot`, and `s` for `save`.

Available commands:

```text
/sr plot
/sr plot save NAME minY maxY
/sr p s NAME minY maxY
```

- `/sr plot` toggles temporary isolation of the PlotSquared plot under the player.
  Running it again disables plot mode and returns to the regular Selective Render state.
- `/sr plot save NAME minY maxY` permanently saves the exact plot shape as one normal
  Selective Render preset and immediately activates it. Both Y boundaries are inclusive.
- `s` is the short alias for `save`.

The client-side command tree exposes `<name>`, `<minY>`, and `<maxY>` as Brigadier
arguments. Both Y values accept any whole number, including values below or above
the dimension's normal build range.

Preset names must be unique. An existing Selective Render preset is never overwritten;
delete or rename it before reusing its name.

## Plot regions and presets

Temporary plot mode exists only in client memory and does not modify saved presets.
It is cleared when the player disconnects or changes dimension.

`/sr plot save` stores the result in Selective Render's normal server- and
dimension-specific configuration. A merged or irregular plot may contain several
internal cuboids, but it appears as one named entry in `/sr list`. Toggle, hide,
rename, and delete operations treat every internal cuboid as one preset.

Active Selective Render hide regions continue to be subtracted while temporary plot
mode is enabled. The protocol accepts up to 256 PlotSquared regions per plot.

## Requirements

Server:

- Paper 1.20.1 or a compatible Bukkit implementation
- Java 17
- PlotSquared 7.x (tested with 7.3.9)

Client:

- Minecraft 1.20.1 with Fabric Loader
- [Selective Render 1.7.0](https://github.com/cepreni-pengwing/selective-render/releases/tag/v1.7.0) or newer
- The Fabric API and Sodium requirements listed by Selective Render

## Installation

1. Install PlotSquared on the Paper server.
2. Place the Selective Render Plots JAR in the server's `plugins` directory.
3. Restart the server; do not use `/reload` for plugin installation.
4. Install the matching Selective Render JAR and its dependencies on each Fabric client.
5. Join the server and stand inside a plot before using `/sr plot`.

Selective Render Plots does not modify chunks, collisions, permissions, plot data,
or network chunk loading. It only resolves the current plot through PlotSquared and
sends its block boundaries to the requesting Selective Render client.

## Permission

`selectiverender.plot.solo` permits use of the `/sr plot` integration.
It is granted to all players by default and can be managed with any Bukkit-compatible
permissions plugin.

## Building

Requirements: JDK 17 or newer and internet access for the first build.

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

The installable JAR is generated in `build/libs`.

## Target versions

- Minecraft 1.20.1
- Paper 1.20.1
- PlotSquared 7.x
- Selective Render protocol version 2

## License

GPL-3.0. See `LICENSE`.
