package org.terasology.gestalt.example.asteroids.common.core.rendering.formats;

import org.joml.Vector2i;
import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.gestalt.assets.format.AbstractAssetFileFormat;
import org.terasology.gestalt.assets.format.AssetDataFile;
import org.terasology.gestalt.example.asteroids.common.core.rendering.TextureData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import de.matthiasmann.twl.utils.PNGDecoder;

public class PngTextureFormat extends AbstractAssetFileFormat<TextureData> {

    public PngTextureFormat() {
        super("png");
    }

    @Override
    public TextureData load(ResourceUrn resourceUrn, List<AssetDataFile> inputs) throws IOException {
        try (InputStream pngStream = inputs.get(0).openStream()) {
            PNGDecoder decoder = new PNGDecoder(pngStream);

            ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();

            int height = decoder.getHeight();
            int width = decoder.getWidth();

            return new TextureData(new Vector2i(width, height), buf);
        } catch (UnsupportedOperationException e) {
            throw new IOException(e);
        }
    }
}
