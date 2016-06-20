package net.ezbim.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import net.ezbim.sample.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
            Log.e(TAG, "onCreate: actionBarHeight" + actionBarHeight);
        }
    }

    public void pdf(View v) {

        mContext.startActivity(new Intent(mContext, PDFActivity.class));
    }

    public void pdfview(View v) {

        mContext.startActivity(new Intent(mContext, PDfViewActivity.class));
    }

    public void txt(View v) {

        mContext.startActivity(new Intent(mContext, TxtDemoActivity.class));
    }

    public void picture(View v) {

        mContext.startActivity(new Intent(mContext, PictureActivity.class));
    }

    public void office(View v) {

        mContext.startActivity(new Intent(mContext, OfficeActivity.class));
    }

}
