package org.terasology.gestalt.example.asteroids.common.core.rendering;

import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.gestalt.assets.AssetData;

import java.nio.ByteBuffer;

/**
 * Data for loading a texture.
 *
 * Simplifies the following features:
 * <ul>
 *     <li>Mipmap control - limited to mipmapping or not</li>
 *     <li>Texture borders</li>
 *     <li>LOD bias</li>
 *     <li>No independent wrap mode for different dimensions</li>
 *     <li>Only 2D texture support</li>
 * </ul>
 * These are all things that can be expanded upon in the future.
 */
public class TextureData implements AssetData {
    private final Vector2i size = new Vector2i();
    private ByteBuffer pixelData;
    private WrapMode wrapMode = WrapMode.CLAMP;
    private FilterMode filterMode = FilterMode.LINEAR;
    private boolean mipmap = true;

    /**
     * Creates texture of a given size, with empty data
     * @param size The size of the texture. Sides must be a power of 2
     */
    public TextureData(Vector2ic size) {
        Preconditions.checkArgument(IntMath.isPowerOfTwo(size.x()), "Texture sizes must be a power of 2");
        Preconditions.checkArgument(IntMath.isPowerOfTwo(size.y()), "Texture sizes must be a power of 2");
        this.size.set(size);
    }

    /**
     * @param size The size of the texture. Sides must be a power of 2
     * @param byteBuffer The texture data. Should be 4 bytes per pixel, in order RGBA.
     */
    public TextureData(Vector2ic size, ByteBuffer byteBuffer) {
        this(size);
        Preconditions.checkArgument(byteBuffer.limit() == 4 * size.x() * size.y(), "Data must be 4 bytes power pixel (RGBA)");
        this.pixelData = byteBuffer;
    }

    /**
     * @return The size of the texture
     */
    public Vector2i getSize() {
        return size;
    }

    /**
     * Note: Will clear pixel data
     * @param newSize The new size for the texture.
     */
    public void setSize(Vector2ic newSize) {
        size.set(newSize);
        pixelData = null;
    }

    /**
     * @return Texture data, or null if the texture is empty
     */
    public ByteBuffer getPixelData() {
        return pixelData;
    }

    /**
     * @param data Sets the pixel data. Should be 4 bytes per pixel, in order RGBA.
     */
    public void setPixelData(ByteBuffer data) {
        Preconditions.checkArgument(data.limit() == 4 * size.x() * size.y(), "Data must be 4 bytes power pixel (RGBA)");
        this.pixelData = data;
    }

    /**
     * @return How the texture will be filtered
     */
    public FilterMode getFilterMode() {
        return filterMode;
    }

    /**
     * @param filterMode How the texture should be filtered
     */
    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    /**
     * @return How the texture will wrap
     */
    public WrapMode getWrapMode() {
        return wrapMode;
    }

    /**
     * @param wrapMode How the texture should wrap
     */
    public void setWrapMode(WrapMode wrapMode) {
        this.wrapMode = wrapMode;
    }

    /**
     * @return Should mipmaps be generated for the texture
     */
    public boolean isMipmap() {
        return mipmap;
    }

    /**
     * @param mipmap Should mipmaps be generated for the texture
     */
    public void setMipmap(boolean mipmap) {
        this.mipmap = mipmap;
    }
}
