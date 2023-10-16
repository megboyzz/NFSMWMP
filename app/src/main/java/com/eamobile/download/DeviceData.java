package com.eamobile.download;

import java.util.EnumSet;
import java.util.Set;

public class DeviceData {
    private String brandName;
    private String deviceName;
    private String glExtensions;
    private int height;
    private int width;

    public enum TextureType {
        PVR,
        ATI,
        DXT,
        ETC,
        S3TC,
        _3DC,
        PALETTED,
        LATC,
        UNCOMPRESSED
    }

    public void forceTexture(Set<TextureType> set) {
        this.glExtensions = "";
        if (set.contains(TextureType.PVR)) {
            this.glExtensions += "GL_IMG_texture_compression_pvrtc ";
        }
        if (set.contains(TextureType.ATI)) {
            this.glExtensions += "GL_ATI_compressed_texture_atitc ";
        }
        if (set.contains(TextureType.DXT)) {
            this.glExtensions += "GL_EXT_texture_compression_dxt1 ";
        }
        if (set.contains(TextureType.ETC)) {
            this.glExtensions += "GL_OES_compressed_ETC1_RGB8_texture ";
        }
        if (set.contains(TextureType.S3TC)) {
            this.glExtensions += "GL_OES_texture_compression_S3TC ";
        }
        if (set.contains(TextureType._3DC)) {
            this.glExtensions += "GL_AMD_compressed_3DC_texture ";
        }
        if (set.contains(TextureType.PALETTED)) {
            this.glExtensions += "GL_OES_compressed_paletted_texture ";
        }
        if (set.contains(TextureType.LATC)) {
            this.glExtensions += "GL_EXT_texture_compression_latc ";
        }
    }

    public String getBrandName() {
        return this.brandName;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getGlExtensions() {
        return this.glExtensions;
    }

    public int getHeight() {
        return this.height;
    }

    public EnumSet<TextureType> getSupportedTextureTypes() {
        EnumSet<TextureType> of = EnumSet.of(TextureType.UNCOMPRESSED);
        if (this.glExtensions.contains("GL_IMG_texture_compression_pvrtc")) {
            of.add(TextureType.PVR);
        }
        if (this.glExtensions.contains("GL_ATI_compressed_texture_atitc") || this.glExtensions.contains("GL_AMD_compressed_ATC_texture") || this.glExtensions.contains("GL_ATI_texture_compression_atitc")) {
            of.add(TextureType.ATI);
        }
        if (this.glExtensions.contains("GL_EXT_texture_compression_dxt1") || this.glExtensions.contains("GL_EXT_texture_compression_dxt3") || this.glExtensions.contains("GL_EXT_texture_compression_dxt5")) {
            of.add(TextureType.DXT);
        }
        if (this.glExtensions.contains("GL_OES_compressed_ETC1_RGB8_texture")) {
            of.add(TextureType.ETC);
        }
        if (this.glExtensions.contains("GL_OES_texture_compression_S3TC") || this.glExtensions.contains("GL_EXT_texture_compression_s3tc")) {
            of.add(TextureType.S3TC);
        }
        if (this.glExtensions.contains("GL_AMD_compressed_3DC_texture")) {
            of.add(TextureType._3DC);
        }
        if (this.glExtensions.contains("GL_OES_compressed_paletted_texture")) {
            of.add(TextureType.PALETTED);
        }
        if (this.glExtensions.contains("GL_EXT_texture_compression_latc")) {
            of.add(TextureType.LATC);
        }
        return of;
    }

    public int getWidth() {
        return this.width;
    }

    public void setBrandName(String str) {
        this.brandName = str;
    }

    public void setDeviceName(String str) {
        this.deviceName = str;
    }

    public void setGlExtensions(String str) {
        this.glExtensions = str;
    }

    public void setResolution(int i, int i2) {
        this.width = i;
        this.height = i2;
    }
}
