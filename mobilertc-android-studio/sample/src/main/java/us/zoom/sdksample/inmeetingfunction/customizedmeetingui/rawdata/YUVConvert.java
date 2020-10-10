package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

import us.zoom.sdk.ZoomSDKPreProcessRawData;

public class YUVConvert {

    public static byte[] convertBitmapToYuv(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] data = convert_argb_to_i420(pixels, width, height);
        return data;
    }

    public static byte[] convert_argb_to_i420(int[] argb, int width, int height) {
        final int frameSize = width * height;

        byte[] i420 = new byte[frameSize + ((width + 1) / 2) * ((height + 1) / 2) * 2];

        int yIndex = 0;                   // Y start index
        int uIndex = frameSize;           // U statt index
        int vIndex = frameSize + ((width + 1) / 2) * ((height + 1) / 2); // V start index: w*h*5/4

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                a = (argb[index] & 0xff000000) >> 24; //  is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // I420(YUV420p) -> YYYYYYYY UU VV
                i420[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && i % 2 == 0) {
                    i420[uIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    if (vIndex < i420.length - 1) {
                        i420[vIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    }
                }
                index++;
            }
        }
        return i420;
    }

    public static void addWaterMark(ZoomSDKPreProcessRawData rawData, WaterMarkData waterMark, int off_x,
                                    int off_y, boolean enableTransparent) {
        if (null == waterMark | null == rawData) {
            return;
        }
        int width = waterMark.getWidth();
        int height = waterMark.getHeight();
        if (width > rawData.getWidth() || height > rawData.getHeight()) {
            return;
        }
        int uvWidth = width / 2;
        int size = width * height;

        byte[] waterMarkYuv = waterMark.getYuv();

        for (int i = 0, len = height; i < len; i++) {
            int index = off_y + i;
            ByteBuffer yBuffer = rawData.getYBuffer(index);
            yBuffer.position(off_x);
            if (enableTransparent) {
                for (int j = 0; j < width; j++) {
                    int offset = i * width + j;
                    byte b = waterMarkYuv[offset];
                    if ((b != 16 && b != -128)) {
                        yBuffer.put(off_x+j, b);
                    }
                }
            } else {
                yBuffer.put(waterMarkYuv, i * width, width);
            }
        }

        for (int i = 0, len = height / 2; i < len; i++) {
            int index = off_y / 2 + i;
            ByteBuffer uBuffer = rawData.getUBuffer(index);
            uBuffer.position(off_x / 2);

            ByteBuffer vBuffer = rawData.getVBuffer(index);
            vBuffer.position(off_x / 2);

            if (enableTransparent) {
                for (int j = 0; j < uvWidth; j++) {
                    byte uByte = waterMarkYuv[size + i * uvWidth + j];

                    byte vByte = waterMarkYuv[size * 5 / 4 + i * uvWidth + j];
                    if (vByte != 16 && vByte != -128 && vByte != -21) {
                        vBuffer.put(vByte);
                    }

                    if (uByte != 16 && uByte != -128 && uByte != -21) {
                        uBuffer.put(uByte);
                    }
                }
            } else {
                uBuffer.put(waterMarkYuv, size + i * uvWidth, uvWidth);
                vBuffer.put(waterMarkYuv, size * 5 / 4 + i * uvWidth, uvWidth);
            }
        }

    }

}
