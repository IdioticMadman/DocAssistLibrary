package net.ezbim.uhflibrary;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Util {


    public static SoundPool sp;
    public static Map<Integer, Integer> suondMap;
    public static Context mContext;

    //初始化声音池
    public static void initSoundPool(Context context) {
        Util.mContext = context;
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        suondMap = new HashMap<>();
        suondMap.put(1, sp.load(context, R.raw.msg, 1));
    }

    //播放声音池声音
    public static void play(int sound, int number) {
        AudioManager am = (AudioManager) Util.mContext.getSystemService(Util.mContext.AUDIO_SERVICE);
        //返回当前AlarmManager最大音量
        float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //返回当前AudioManager对象的音量值
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolume / audioMaxVolume;
        sp.play(
                suondMap.get(sound), //播放的音乐Id
                audioCurrentVolume, //左声道音量
                audioCurrentVolume, //右声道音量
                1, //优先级，0为最低
                number, //循环次数，0无不循环，-1无永远循环
                1);//回放速度，值在0.5-2.0之间，1为正常速度
    }

    /**
     * 电源开启
     */
    public static final String powerOn = "1";
    /**
     * 电源关闭
     */
    public static final String powerOff = "0";

    /**
     * 控制UHFReader的供电
     *
     * @param str 供电参数
     */
    public static void controlUHFReaderPower(String str) {
        try {
            FileWriter localFileWriterOn = new FileWriter(new File("/proc/gpiocontrol/set_id"));
            localFileWriterOn.write(str);
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static String SCAN_ACTION = "scan.rcv.message";

    public static void registerScanReceiver(ScanReceiver scanReceiver, Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SCAN_ACTION);
        context.registerReceiver(scanReceiver, intentFilter);
    }

    public static void unRegisterScanReceiver(ScanReceiver scanReceiver, Context context) {
        if (scanReceiver != null) {
            context.unregisterReceiver(scanReceiver);
            scanReceiver = null;
        }
    }
}
