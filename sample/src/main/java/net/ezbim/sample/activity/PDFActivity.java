package net.ezbim.sample.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.ezbim.docassist.pdfandroid.adapter.BasePDFPagerAdapter;
import net.ezbim.docassist.pdfandroid.view.PDFViewPager;
import net.ezbim.docassist.utils.CopyAsset;
import net.ezbim.docassist.utils.CopyAssetThreadImpl;
import net.ezbim.sample.Common;
import net.ezbim.sample.R;

import java.io.File;

/**
 * Created by robert on 2016/6/20.
 */
public class PDFActivity extends AppCompatActivity {

    private static final String TAG = "PDFActivity";
    private File pdfFolder;
    private File destinationFile;

    private static String pdfFileName = "adobe.pdf";
    private FrameLayout fl_pdf;
    private Context mContext;
    private PDFViewPager pdfViewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        setTitle("PDFAndroidDemo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfFolder = new File(Common.pdfPathFolder);
        if (!pdfFolder.exists()) {
            boolean mkdirs = pdfFolder.mkdirs();
            Log.e(TAG, "创建文件夹是否成功:" + mkdirs);
        }
        destinationFile = new File(pdfFolder, pdfFileName);
        mContext = PDFActivity.this;
        fl_pdf = (FrameLayout) findViewById(R.id.fl_pdf);

        if (destinationFile.exists()) {
            setPDFViewPager();
        } else {
            CopyAsset copyAsset = new CopyAssetThreadImpl(getApplicationContext(), new Handler(), new CopyAsset.Listener() {
                @Override
                public void success(String assetName, String destinationPath) {
                    setPDFViewPager();
                }

                @Override
                public void failure(Exception e) {
                    Log.e(TAG, "failure: " + e.toString());
                    Toast.makeText(mContext, "文件拷贝出错", Toast.LENGTH_SHORT).show();
                }
            });
            copyAsset.copy(pdfFileName, getPDFPathOnSD());
        }
    }

    private void setPDFViewPager() {
        pdfViewPager = new PDFViewPager(mContext, getPDFPathOnSD());
        fl_pdf.removeAllViews();
        fl_pdf.addView(pdfViewPager);
    }

    private String getPDFPathOnSD() {
        return destinationFile.getAbsolutePath();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pdfViewPager != null) {
            BasePDFPagerAdapter adapter = (BasePDFPagerAdapter) pdfViewPager.getAdapter();
            if (adapter != null) {
                adapter.close();
                adapter = null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
