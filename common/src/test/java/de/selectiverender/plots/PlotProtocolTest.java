package de.selectiverender.plots;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlotProtocolTest {
    @Test
    void readsValidSaveRequest() throws IOException {
        byte[] payload = request(42L, PlotProtocol.ACTION_SAVE, "castle", -64, 320);
        assertEquals(new PlotProtocol.Request(42L, PlotProtocol.ACTION_SAVE, "castle", -64, 320),
                PlotProtocol.readRequest(payload));
    }

    @Test
    void rejectsMalformedRequests() throws IOException {
        byte[] invalidAction = request(1L, 99, "", 0, 0);
        assertThrows(IOException.class, () -> PlotProtocol.readRequest(invalidAction));
        byte[] invertedY = request(1L, PlotProtocol.ACTION_SAVE, "name", 10, 9);
        assertThrows(IOException.class, () -> PlotProtocol.readRequest(invertedY));
    }

    @Test
    void encodesRegionsAndRequestedHeightOverride() throws IOException {
        byte[] payload = PlotProtocol.writeResponse(7L, PlotProtocol.STATUS_SAVE, "plot",
                List.of(new PlotProtocol.PlotRegion(1, 2, 3, 4, 5, 6)), -10, 20);
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
            assertEquals(PlotProtocol.MAGIC, input.readInt());
            assertEquals(PlotProtocol.VERSION, input.readInt());
            assertEquals(7L, input.readLong());
            assertEquals(PlotProtocol.STATUS_SAVE, input.readUnsignedByte());
            int nameLength = input.readInt();
            assertEquals("plot", new String(input.readNBytes(nameLength), StandardCharsets.UTF_8));
            assertEquals(1, input.readInt());
            assertEquals(1, input.readInt());
            assertEquals(2, input.readInt());
            assertEquals(-10, input.readInt());
            assertEquals(20, input.readInt());
            assertEquals(5, input.readInt());
            assertEquals(6, input.readInt());
            assertEquals(0, input.available());
        }
    }

    private static byte[] request(long id, int action, String name, int minY, int maxY) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeInt(PlotProtocol.MAGIC);
            output.writeInt(PlotProtocol.VERSION);
            output.writeLong(id);
            output.writeByte(action);
            byte[] encodedName = name.getBytes(StandardCharsets.UTF_8);
            output.writeInt(encodedName.length);
            output.write(encodedName);
            output.writeInt(minY);
            output.writeInt(maxY);
        }
        return bytes.toByteArray();
    }
}
