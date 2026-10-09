# Selective Render Plots

[Download Selective Render Plots on Modrinth](https://modrinth.com/plugin/selective-render-plots)

Selective Render Plots is an optional server add-on for [Selective Render](https://modrinth.com/mod/selective-render). See the [Selective Render GitHub project](https://github.com/cepreni-pengwing/selective-render) for client details. It lets a compatible client use exact PlotSquared plot outlines, including merged and irregular plots. Install the matching Paper or Fabric server build; the add-on itself does not render anything.

## Commands and keybinds

These are Selective Render client commands, not server commands. `/sr` is short for `/selectiverender`, `p` for `plot`, and `s` for `save`. No command below has its own dedicated key except the plot toggle and clear bindings noted here.

<table>
<thead><tr><th>Alias</th><th>Expanded command</th><th>Purpose</th><th>Key</th></tr></thead>
<tbody>
<tr><td rowspan="3"><code>/sr p</code></td><td><code>plot [minY] [maxY] [xzMargin]</code></td><td>Add or remove the plot below you</td><td>Not Bound</td></tr>
<tr><td><code>plot clear</code></td><td>Clear temporary plots</td><td>Not Bound</td></tr>
<tr><td><code>plot save NAME [minY] [maxY] [xzMargin]</code> (<code>s</code> alias)</td><td>Save plot as a named region</td><td></td></tr>
<tr><td></td><td></td><td>Clear temporary plots</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Cycle boundary faces</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Cycle interaction mode</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Cycle player visibility</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Open settings</td><td><code>#</code></td></tr>
<tr><td></td><td></td><td>Set selection position 1</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Set selection position 2</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Toggle current plot region</td><td>Not Bound</td></tr>
<tr><td></td><td></td><td>Toggle all block filters</td><td>Not Bound</td></tr>
</tbody>
</table>

Keybinds shown in the table are defaults. Omitted height limits use the client's configured minimum (default `-64`) and maximum `400`. A positive margin expands the plot horizontally; a negative margin shrinks it. A margin that would remove the entire plot is rejected. Use `/sr toggle` (`/sr t`) to switch the current render group off while collecting more plots, then on again when ready. Only the first plot added to an empty group enables isolation automatically.

## Installation

1. Install PlotSquared and the matching Selective Render Plots server file. Put the Paper file in `plugins/` or the Fabric file in `mods/`.
2. Install Selective Render and its required client dependencies for every player who will use the feature.
3. Restart the server, join it, and stand on a plot before using `/sr p`.

Choose the server file that matches both the server platform and game version. The Fabric build requires the ArdaCraft PlotSquared Fabric port and its dependencies; the regular Bukkit/Paper PlotSquared file cannot be used on Fabric. Check the release assets for the currently available variants. The `common` JAR is not installed separately.

## Permissions

The permission node is `selectiverender.plot.solo`. Paper grants it by default unless the server's permission policy overrides that. On Fabric, access follows the PlotSquared permission setup. If LuckPerms or another permission manager is used, make sure intended players or groups have this permission. For LuckPerms, an administrator can grant it with:

```text
/lp user PLAYER permission set selectiverender.plot.solo true
```

## Behavior

Temporary plot groups stay in client memory and survive reconnects and dimension changes during the current Minecraft session. They remain separate per server/world and dimension. Saving a plot creates a normal region in Selective Render's local configuration.

The add-on answers explicit requests from permitted clients with plot block bounds. It does not change plot data, world state, collision, render distance, or chunk loading. A single plot can contain multiple cuboids; responses are limited to 256 cuboids per plot.

## Supported builds

- Paper server builds: Minecraft 1.20.1 and 1.21.1.
- Fabric server build: Minecraft 1.20.1 with the ArdaCraft PlotSquared Fabric port.
- The compatible Selective Render client is required on the player side. Use matching client and server release assets.

## Building and contributing

Use JDK 21 and run:

```powershell
.\gradlew.bat build
```

The Paper and Fabric JARs are generated under their respective `build/libs` directories. See [CONTRIBUTING.md](CONTRIBUTING.md) before submitting changes. Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com).

Licensed under GPL-3.0-only. PlotSquared is a separate runtime dependency and is not included in the distributed JARs. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
