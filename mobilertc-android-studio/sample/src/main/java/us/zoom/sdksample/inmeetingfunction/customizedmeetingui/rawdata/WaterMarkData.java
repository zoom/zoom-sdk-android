package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata;

public class WaterMarkData {

    private int width;

    private int height;

    private byte[] yuv;

    public WaterMarkData(int width, int height, byte[] yuv) {
        this.width = width;
        this.height = height;
        this.yuv = yuv;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getYuv() {
        return yuv;
    }
}
