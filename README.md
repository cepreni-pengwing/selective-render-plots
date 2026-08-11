# Selective Render Plot

Selective Render PlotSquared Addon is an optional Paper plugin that sends exact PlotSquared plot regions to Selective Render clients. It supports merged and non-rectangular plots through PlotSquared's `Plot#getRegions()` API.

## Requirements

- Paper 1.20.1 or a compatible Bukkit implementation
- Java 17
- PlotSquared 7.x
- Selective Render 1.7.0 or newer on connecting Fabric clients

## Installation

Place the built JAR in the server's `plugins` directory next to PlotSquared, then restart the server.

The addon does not modify chunks, collisions, permissions, or network chunk loading. It provides the server-side `/srp` command and sends the exact regions of the plot under the player to their Selective Render client.

## Commands

- `/srp solo` or `/srp s` isolates the current plot.
- `/srp refresh` or `/srp r` updates the active plot regions.
- `/srp off` or `/srp o` leaves plot mode.
- `/srp status` shows the current plot and client-side plot-mode state.

Plot mode is temporary and does not modify the client's saved Selective Render presets. Merged and non-rectangular plots are represented by all regions returned by PlotSquared.

## Build

```bash
./gradlew build
```

The resulting JAR is written to `build/libs`.

## Permission

`selectiverender.plot.solo` allows a player to request the current plot regions and is granted by default.

## License

This project is licensed under GPL-3.0.
