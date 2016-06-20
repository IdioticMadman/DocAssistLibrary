package net.ezbim.docassist.pdfandroid.adapter;

import android.graphics.Bitmap;

/**
 * @author robert
 * @version 1.0
 * @time 2016/6/15.
 * @description
 */
public interface BitmapContainer {
    Bitmap get(int position);
    void remove(int position);
    void clear();
}
