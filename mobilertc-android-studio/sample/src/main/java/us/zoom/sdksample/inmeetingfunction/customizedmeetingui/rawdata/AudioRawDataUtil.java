package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.rawdata;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import us.zoom.androidlib.util.ZMLog;
import us.zoom.sdk.IZoomSDKAudioRawDataDelegate;
import us.zoom.sdk.IZoomSDKAudioRawDataHelper;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAudioRawData;
import us.zoom.sdk.ZoomSDKAudioRawDataHelper;

public class AudioRawDataUtil {

    static final String TAG = "AudioRawDataUtil";

    private Map<Integer, FileChannel> map = new HashMap<>();

    private Context mContext;

    IZoomSDKAudioRawDataHelper audioRawDataHelper;


    public AudioRawDataUtil(Context context) {
        mContext = context.getApplicationContext();
        audioRawDataHelper = new ZoomSDKAudioRawDataHelper();
    }

    private FileChannel createFileChannel(int userId) {
        String fileName = "/sdcard/Android/data/" + mContext.getPackageName() + "/files/" + userId + ".pcm";
        File file = new File(fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            FileChannel fileChannel = fileOutputStream.getChannel();
            return fileChannel;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private IZoomSDKAudioRawDataDelegate dataDelegate = new IZoomSDKAudioRawDataDelegate() {
        @Override
        public void onMixedAudioRawDataReceived(ZoomSDKAudioRawData rawData) {
            Log.d(TAG, "onMixedAudioRawDataReceived:" + rawData.getBufferLen());
            saveAudioRawData(rawData, 0);

        }

        public void onOneWayAudioRawDataReceived(ZoomSDKAudioRawData rawData, int userId) {
            Log.d(TAG, "onOneWayAudioRawDataReceived:" + rawData.getBufferLen()+" userId="+userId);
            saveAudioRawData(rawData, userId);
        }
    };

    private void saveAudioRawData(ZoomSDKAudioRawData rawData, int userId) {
        try {
            ZMLog.d(TAG, "onMixedAudioRawDataReceived:" + rawData.getBufferLen());
            FileChannel fileChannel = map.get(userId);
            if (null == fileChannel) {
                fileChannel = createFileChannel(userId);
                map.put(userId, fileChannel);
            }
            if (null != fileChannel) {
                fileChannel.write(rawData.getBuffer(), rawData.getBufferLen());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void subscribeAudio() {
        audioRawDataHelper.subscribe(dataDelegate);
    }

    public void unSubscribe() {

        for (FileChannel fileChannel : map.values()) {
            if (null != fileChannel) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        audioRawDataHelper.unSubscribe();
    }
}
