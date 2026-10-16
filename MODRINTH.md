# Selective Render Plots

Optional server add-on for [Selective Render](https://modrinth.com/mod/selective-render). Players isolate their PlotSquared plot with exact outlines, including merged plots.

**Server:** Paper 1.20.1 / 1.21.1 with PlotSquared, or Fabric 1.20.1 with the ArdaCraft PlotSquared Fabric port · **Client:** Selective Render

## Commands

Entered by players in the Selective Render client while standing on a plot.

<table>
<thead><tr><th>Alias</th><th>Command</th><th>Action</th><th>Key</th></tr></thead>
<tbody>
<tr><td rowspan="3"><code>/sr&nbsp;p</code></td><td><code>plot [minY] [maxY] [xzMargin]</code></td><td>Add or remove current plot</td><td>Not bound</td></tr>
<tr><td><code>plot clear</code> (<code>c</code>)</td><td>Clear temporary plots</td><td>Not bound</td></tr>
<tr><td><code>plot save NAME [minY] [maxY] [xzMargin]</code> (<code>s</code>)</td><td>Save plot as region</td><td></td></tr>
</tbody>
</table>

- Y limits default to `-64` and `319`; a negative margin shrinks the plot
- Temporary plots last for the current game session
- Permission: `selectiverender.plot.solo` (granted by default on Paper)

More: [GitHub](https://github.com/cepreni-pengwing/selective-render-plots#readme) · Contact: [pengwing.ac@gmail.com](mailto:pengwing.ac@gmail.com)

Licensed under GPL-3.0-only. PlotSquared is not included.
