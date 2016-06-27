package net.ezbim.uhflibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by robert on 2016/6/22.
 */
public class ScreenStateReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //reader.powerOn();
        //屏亮
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //("1");
            UHFUtils.controlUHFReaderPower(UHFUtils.powerOn);
            Log.e("ScreenStateReceiver", "screen on");

        }//屏灭
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //reader.powerOff();
            //powerOn("0");
            UHFUtils.controlUHFReaderPower(UHFUtils.powerOff);
            Log.e("ScreenStateReceiver", "screen off");
        }

    }

}