package net.ezbim.uhflibrary;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by robert on 2016/6/23.
 */
public class UHFDialog extends Dialog {
    public UHFDialog(Context context) {
        super(context);
    }

    public UHFDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UHFDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
