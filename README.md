# Selective Render Plots

[Modrinth](https://modrinth.com/plugin/selective-render-plots) · [Releases](https://github.com/cepreni-pengwing/selective-render-plots/releases) · [Changelog](CHANGELOG.md)

Optional server add-on for [Selective Render](https://github.com/cepreni-pengwing/selective-render). It sends exact PlotSquared plot outlines, including merged and irregular plots, to players who ask for them. It renders nothing itself and does not change plots, world data or chunk loading.

## Supported servers

| Platform | Minecraft | Requires |
| --- | --- | --- |
| Paper | 1.20.1, 1.21.1 | PlotSquared |
| Fabric | 1.20.1 | ArdaCraft PlotSquared Fabric port |

Players need the matching [Selective Render](https://github.com/cepreni-pengwing/selective-render) client.

## Installation

1. Install PlotSquared.
2. Put the Paper JAR into `plugins/` or the Fabric JAR into `mods/`. The `common` JAR is not installed separately.
3. Restart the server.

## Usage

Players use Selective Render client commands while standing on a plot:

| Command | Action |
| --- | --- |
| `/sr plot [minY] [maxY] [xzMargin]` | Add or remove the plot |
| `/sr plot clear` | Clear temporary plots |
| `/sr plot save NAME` | Save the plot as a normal region |

Temporary plots last for the current game session, separately per server and dimension.

## Permissions

| Node | Default |
| --- | --- |
| `selectiverender.plot.solo` | Granted on Paper; on Fabric follows PlotSquared |

With LuckPerms: `/lp user PLAYER permission set selectiverender.plot.solo true`

## Building

Requires Java 21 or newer.

```powershell
.\gradlew.bat build
```

JARs end up in `paper/mc1.20.1/build/libs`, `paper/mc1.21.1/build/libs` and `fabric/build/libs`. See [CONTRIBUTING.md](CONTRIBUTING.md). Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com)

Licensed under GPL-3.0-only. PlotSquared is not included, see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
