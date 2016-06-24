package net.ezbim.uhflibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: net.ezbim.uhflibrary.ScanReceiver.java
 * @author: robert
 * @date: 2016-06-24 18:21
 */
public class ScanReceiver extends BroadcastReceiver {

    private CallBack mCallBack;

    public ScanReceiver(CallBack callBack) {
        this.mCallBack = callBack;
    }

    public interface CallBack {
        void onSuccess(String result);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        byte[] barocode = intent.getByteArrayExtra("barocode");
        int barocodelen = intent.getIntExtra("length", 0);
        byte temp = intent.getByteExtra("barcodeType", (byte) 0);
        android.util.Log.i("debug", "----codetype--" + temp);
        String barcodeStr = new String(barocode, 0, barocodelen);
        mCallBack.onSuccess(barcodeStr+"\n");
    }
}
