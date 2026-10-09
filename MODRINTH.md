# Selective Render Plots

Selective Render Plots is an optional server add-on for [Selective Render](https://modrinth.com/mod/selective-render). It lets players use accurate PlotSquared plot shapes as temporary viewing regions, including merged plots. Selective Render must also be installed on each player's client.

## Install

Install PlotSquared and the server file matching your platform and game version. Put the Paper file in `plugins/` or the Fabric file in `mods/`, then restart the server. Fabric requires the ArdaCraft PlotSquared Fabric port and its dependencies; the regular Paper PlotSquared file does not work on Fabric. Install the matching Selective Render client and its dependencies for players using the feature.

## Commands

Commands are entered in the player's Selective Render client, not on the server. `/sr` is short for `/selectiverender`; `p` means `plot` and `s` means `save`.

<table>
<thead><tr><th>Alias</th><th>Expanded command</th><th>Purpose</th><th>Key</th></tr></thead>
<tbody>
<tr><td rowspan="3"><code>/sr p</code></td><td><code>plot [minY] [maxY] [xzMargin]</code></td><td>Add or remove the plot beneath you</td><td>Not Bound</td></tr>
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

The default height limits are the client's configured minimum (`-64` initially) and `400`. A positive margin expands the plot; a negative one shrinks it. Temporary plot groups remain available through reconnects and dimension changes for the current Minecraft session.

## Permissions

The permission is `selectiverender.plot.solo`. It is granted by default on Paper unless server permissions override that. On Fabric, it follows the PlotSquared permission setup. If using LuckPerms, grant the node to the players or groups who should have access.

This add-on only returns plot boundaries to permitted clients that request them. It does not change world data, collision, or chunk loading.

For setup details and current server variants, see the [GitHub README](https://github.com/cepreni-pengwing/selective-render-plots#readme). Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com).

Licensed under GPL-3.0-only. PlotSquared is a separate dependency and is not included.
