package de.selectiverender.plots.fabric;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.regions.CuboidRegion;
import de.selectiverender.plots.PlotProtocol;
import de.selectiverender.plots.PlotProtocol.PlotRegion;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public final class SelectiveRenderPlotsFabricMod implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("SelectiveRenderPlots");
    private static final ResourceLocation REQUEST_CHANNEL = new ResourceLocation(PlotProtocol.REQUEST_CHANNEL);
    private static final ResourceLocation RESPONSE_CHANNEL = new ResourceLocation(PlotProtocol.RESPONSE_CHANNEL);

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_CHANNEL, (server, player, handler, buffer, responseSender) -> {
            byte[] payload = new byte[buffer.readableBytes()];
            buffer.readBytes(payload);
            PlotProtocol.Request request;
            try {
                request = PlotProtocol.readRequest(payload);
            } catch (IOException exception) {
                LOGGER.warn("Rejected malformed plot request from {}: {}", player.getGameProfile().getName(), exception.getMessage());
                return;
            }
            server.execute(() -> respond(player, request));
        });
        LOGGER.info("Selective Render Plots Fabric bridge protocol v{} enabled", PlotProtocol.VERSION);
    }

    private static void respond(ServerPlayer player, PlotProtocol.Request request) {
        if (player.hasDisconnected()) return;
        PlotPlayer<?> plotPlayer = PlotSquared.platform().playerManager().getPlayerIfExists(player.getUUID());
        if (plotPlayer == null) {
            send(player, request.id(), PlotProtocol.STATUS_ERROR, "", List.of(), null, null);
            return;
        }
        if (!plotPlayer.hasPermission("selectiverender.plot.solo")) {
            send(player, request.id(), PlotProtocol.STATUS_NO_PERMISSION, "", List.of(), null, null);
            return;
        }

        try {
            Plot plot = plotPlayer.getCurrentPlot();
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
            LOGGER.error("Could not resolve plot regions for {}", player.getGameProfile().getName(), exception);
            send(player, request.id(), PlotProtocol.STATUS_ERROR, "", List.of(), null, null);
        }
    }

    private static void send(ServerPlayer player, long requestId, int status, String name, List<PlotRegion> regions,
                             Integer requestedMinY, Integer requestedMaxY) {
        try {
            byte[] payload = PlotProtocol.writeResponse(requestId, status, name, regions, requestedMinY, requestedMaxY);
            ServerPlayNetworking.send(player, RESPONSE_CHANNEL, new FriendlyByteBuf(Unpooled.wrappedBuffer(payload)));
        } catch (IOException exception) {
            LOGGER.error("Could not encode plot response for {}: {}", player.getGameProfile().getName(), exception.getMessage());
        }
    }
}
