package de.selectiverender.plots;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class PlotProtocol {
    public static final String REQUEST_CHANNEL = "selectiverender:plot_request";
    public static final String RESPONSE_CHANNEL = "selectiverender:plot_response";
    public static final int MAGIC = 0x53525031;
    public static final int VERSION = 2;
    public static final int MAX_REGIONS = 256;
    public static final int ACTION_TOGGLE = 0;
    public static final int ACTION_SAVE = 1;
    public static final int STATUS_NO_PLOT = 1;
    public static final int STATUS_NO_PERMISSION = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_TOGGLE = 6;
    public static final int STATUS_SAVE = 7;

    private PlotProtocol() {
    }

    public static Request readRequest(byte[] payload) throws IOException {
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            if (input.readInt() != MAGIC) throw new IOException("Invalid protocol magic");
            if (input.readInt() != VERSION) throw new IOException("Unsupported protocol version");
            long requestId = input.readLong();
            int action = input.readUnsignedByte();
            if (action != ACTION_TOGGLE && action != ACTION_SAVE) throw new IOException("Invalid action");
            String name = readString(input);
            int minY = input.readInt();
            int maxY = input.readInt();
            if (action == ACTION_SAVE && (name.isBlank() || minY > maxY)) {
                throw new IOException("Invalid save arguments");
            }
            if (input.available() != 0) throw new IOException("Trailing request data");
            return new Request(requestId, action, name, minY, maxY);
        }
    }

    public static byte[] writeResponse(long requestId, int status, String name, List<PlotRegion> regions,
                                       Integer requestedMinY, Integer requestedMaxY) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeInt(MAGIC);
            output.writeInt(VERSION);
            output.writeLong(requestId);
            output.writeByte(status);
            writeString(output, name);
            output.writeInt(regions.size());
            for (PlotRegion region : regions) {
                output.writeInt(region.minX());
                output.writeInt(region.maxX());
                output.writeInt(requestedMinY == null ? region.minY() : requestedMinY);
                output.writeInt(requestedMaxY == null ? region.maxY() : requestedMaxY);
                output.writeInt(region.minZ());
                output.writeInt(region.maxZ());
            }
        }
        return bytes.toByteArray();
    }

    private static void writeString(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 256) throw new IOException("String exceeds protocol limit");
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    private static String readString(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0 || length > 256 || length > input.available()) {
            throw new IOException("Invalid string length");
        }
        return new String(input.readNBytes(length), StandardCharsets.UTF_8);
    }

    public record Request(long id, int action, String name, int minY, int maxY) {
    }

    public record PlotRegion(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
    }
}
