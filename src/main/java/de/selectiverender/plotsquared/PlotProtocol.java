package de.selectiverender.plotsquared;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

final class PlotProtocol {
    static final String RESPONSE_CHANNEL = "selectiverender:plot_response";
    static final int MAGIC = 0x53525031;
    static final int VERSION = 1;
    static final int MAX_REGIONS = 256;
    static final int STATUS_OK = 0;
    static final int STATUS_NO_PLOT = 1;
    static final int STATUS_NO_PERMISSION = 2;
    static final int STATUS_ERROR = 3;
    static final int STATUS_OFF = 4;
    static final int STATUS_INFO = 5;
    static final int STATUS_TOGGLE = 6;
    static final int STATUS_SAVE = 7;

    private PlotProtocol() { }

    static void writeString(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 256) throw new IOException("String exceeds protocol limit");
        output.writeInt(bytes.length);
        output.write(bytes);
    }
}
