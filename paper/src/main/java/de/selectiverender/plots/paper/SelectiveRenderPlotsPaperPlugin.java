package de.selectiverender.plots.paper;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.regions.CuboidRegion;
import de.selectiverender.plots.PlotProtocol;
import de.selectiverender.plots.PlotProtocol.PlotRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public final class SelectiveRenderPlotsPaperPlugin extends JavaPlugin implements PluginMessageListener {
    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, PlotProtocol.REQUEST_CHANNEL, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlotProtocol.RESPONSE_CHANNEL);
        getLogger().info("Selective Render Plots Paper bridge protocol v" + PlotProtocol.VERSION + " enabled");
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
            List<PlotRegion> regions = plot.getRegions().stream()
                    .sorted(Comparator.comparingInt((CuboidRegion region) -> region.getMinimumPoint().getX())
                            .thenComparingInt(region -> region.getMinimumPoint().getZ())
                            .thenComparingInt(region -> region.getMinimumPoint().getY()))
                    .map(region -> new PlotRegion(region.getMinimumPoint().getX(), region.getMaximumPoint().getX(),
                            region.getMinimumPoint().getY(), region.getMaximumPoint().getY(),
                            region.getMinimumPoint().getZ(), region.getMaximumPoint().getZ()))
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

    private void send(Player player, long requestId, int status, String name, List<PlotRegion> regions,
                      Integer requestedMinY, Integer requestedMaxY) {
        try {
            byte[] payload = PlotProtocol.writeResponse(requestId, status, name, regions, requestedMinY, requestedMaxY);
            player.sendPluginMessage(this, PlotProtocol.RESPONSE_CHANNEL, payload);
        } catch (IOException exception) {
            getLogger().severe("Could not encode plot response for " + player.getName() + ": " + exception.getMessage());
        }
    }
}
