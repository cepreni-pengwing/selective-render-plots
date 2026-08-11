package de.selectiverender.plots;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public final class SelectiveRenderPlotsPlugin extends JavaPlugin implements PluginMessageListener {
    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, PlotProtocol.REQUEST_CHANNEL, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlotProtocol.RESPONSE_CHANNEL);
        getLogger().info("Selective Render Plots bridge protocol v" + PlotProtocol.VERSION + " enabled");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] payload) {
        if (!PlotProtocol.REQUEST_CHANNEL.equals(channel)) return;
        PlotProtocol.Request request;
        try {
            request = PlotProtocol.readRequest(payload);
        } catch (IOException exception) {
            getLogger().warning("Rejected malformed plot request from " + player.getName() + ": " + exception.getMessage());
            return;
        }
        Runnable response = () -> respond(player, request);
        if (Bukkit.isPrimaryThread()) response.run(); else getServer().getScheduler().runTask(this, response);
    }

    private void respond(Player player, PlotProtocol.Request request) {
        if (!player.isOnline()) return;
        if (!player.hasPermission("selectiverender.plot.solo")) {
            send(player, request.id(), PlotProtocol.STATUS_NO_PERMISSION, "", List.of(), null, null);
            return;
        }

        try {
            Plot plot = BukkitUtil.adapt(player).getCurrentPlot();
            if (plot == null) {
                send(player, request.id(), PlotProtocol.STATUS_NO_PLOT, "", List.of(), null, null);
                return;
            }
            List<CuboidRegion> regions = plot.getRegions().stream()
                    .sorted(Comparator.comparingInt((CuboidRegion region) -> region.getMinimumPoint().getX())
                            .thenComparingInt(region -> region.getMinimumPoint().getZ())
                            .thenComparingInt(region -> region.getMinimumPoint().getY()))
                    .toList();
            if (regions.isEmpty() || regions.size() > PlotProtocol.MAX_REGIONS) {
                throw new IOException("Plot returned an invalid region count: " + regions.size());
            }
            boolean save = request.action() == PlotProtocol.ACTION_SAVE;
            send(player, request.id(), save ? PlotProtocol.STATUS_SAVE : PlotProtocol.STATUS_TOGGLE,
                    save ? request.name() : plot.getId().toString(), regions,
                    save ? request.minY() : null, save ? request.maxY() : null);
        } catch (RuntimeException | IOException exception) {
            getLogger().severe("Could not resolve plot regions for " + player.getName() + ": " + exception.getMessage());
            send(player, request.id(), PlotProtocol.STATUS_ERROR, "", List.of(), null, null);
        }
    }

    private void send(Player player, long requestId, int status, String name,
                      List<CuboidRegion> regions, Integer requestedMinY, Integer requestedMaxY) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(bytes)) {
                output.writeInt(PlotProtocol.MAGIC);
                output.writeInt(PlotProtocol.VERSION);
                output.writeLong(requestId);
                output.writeByte(status);
                PlotProtocol.writeString(output, name);
                output.writeInt(regions.size());
                for (CuboidRegion region : regions) {
                    BlockVector3 min = region.getMinimumPoint();
                    BlockVector3 max = region.getMaximumPoint();
                    output.writeInt(min.getX());
                    output.writeInt(max.getX());
                    output.writeInt(requestedMinY == null ? min.getY() : requestedMinY);
                    output.writeInt(requestedMaxY == null ? max.getY() : requestedMaxY);
                    output.writeInt(min.getZ());
                    output.writeInt(max.getZ());
                }
            }
            player.sendPluginMessage(this, PlotProtocol.RESPONSE_CHANNEL, bytes.toByteArray());
        } catch (IOException exception) {
            getLogger().severe("Could not encode plot response for " + player.getName() + ": " + exception.getMessage());
        }
    }
}
