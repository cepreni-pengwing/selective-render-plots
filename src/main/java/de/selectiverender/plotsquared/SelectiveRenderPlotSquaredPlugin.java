package de.selectiverender.plotsquared;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class SelectiveRenderPlotSquaredPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {
    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlotProtocol.RESPONSE_CHANNEL);
        PluginCommand command = getCommand("selectiverenderplot");
        if (command == null) throw new IllegalStateException("The selectiverenderplot command is missing from plugin.yml");
        command.setExecutor(this);
        command.setTabCompleter(this);
        getLogger().info("Selective Render PlotSquared bridge protocol v" + PlotProtocol.VERSION + " enabled");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }
        if (!player.hasPermission("selectiverender.plot.solo")) {
            send(player, PlotProtocol.STATUS_NO_PERMISSION, "", List.of());
            return true;
        }
        if (!player.getListeningPluginChannels().contains(PlotProtocol.RESPONSE_CHANNEL)) {
            player.sendMessage("Selective Render 1.7.0 or newer is required on your Fabric client.");
            return true;
        }

        if (args.length == 0) {
            sendCurrentPlot(player, PlotProtocol.STATUS_TOGGLE, null, null, null);
            return true;
        }
        if (args.length == 4 && ("save".equalsIgnoreCase(args[0]) || "s".equalsIgnoreCase(args[0]))) {
            Integer minY = parseCoordinate(player, args[2], "minY");
            Integer maxY = parseCoordinate(player, args[3], "maxY");
            if (minY == null || maxY == null) return true;
            if (minY > maxY) {
                player.sendMessage("minY must not be greater than maxY.");
                return true;
            }
            sendCurrentPlot(player, PlotProtocol.STATUS_SAVE, args[1], minY, maxY);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return List.of("save", "s").stream().filter(value -> value.startsWith(input)).toList();
        }
        if (args.length == 2 && isSave(args[0])) return List.of("name");
        if (sender instanceof Player player && isSave(args[0])) {
            if (args.length == 3) return List.of(Integer.toString(player.getWorld().getMinHeight()));
            if (args.length == 4) return List.of(Integer.toString(player.getWorld().getMaxHeight() - 1));
        }
        return List.of();
    }

    private boolean isSave(String value) {
        return "save".equalsIgnoreCase(value) || "s".equalsIgnoreCase(value);
    }

    private Integer parseCoordinate(Player player, String value, String label) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            player.sendMessage(label + " must be a whole number.");
            return null;
        }
    }

    private void sendCurrentPlot(Player player, int status, String presetName, Integer minY, Integer maxY) {
        try {
            Plot plot = BukkitUtil.adapt(player).getCurrentPlot();
            if (plot == null) {
                send(player, status == PlotProtocol.STATUS_TOGGLE
                        ? PlotProtocol.STATUS_TOGGLE : PlotProtocol.STATUS_NO_PLOT, "", List.of());
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
            send(player, status, presetName == null ? plot.getId().toString() : presetName,
                    regions, minY, maxY);
        } catch (RuntimeException | IOException exception) {
            getLogger().severe("Could not resolve plot regions for " + player.getName() + ": " + exception.getMessage());
            send(player, PlotProtocol.STATUS_ERROR, "", List.of());
        }
    }

    private void send(Player player, int status, String plotId, List<CuboidRegion> regions) {
        send(player, status, plotId, regions, null, null);
    }

    private void send(Player player, int status, String plotId, List<CuboidRegion> regions,
                      Integer requestedMinY, Integer requestedMaxY) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(bytes)) {
                output.writeInt(PlotProtocol.MAGIC);
                output.writeInt(PlotProtocol.VERSION);
                output.writeByte(status);
                PlotProtocol.writeString(output, plotId);
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
