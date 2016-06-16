package net.ezbim.sample.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.ezbim.docassist.pdf.adapter.BasePDFPagerAdapter;
import net.ezbim.docassist.pdf.view.PDFViewPager;
import net.ezbim.docassist.utils.CopyAsset;
import net.ezbim.docassist.utils.CopyAssetThreadImpl;
import net.ezbim.sample.Common;
import net.ezbim.sample.activity.MainActivity;
import net.ezbim.sample.R;

import java.io.File;

public class PDFFragment extends Fragment {


    private static final String TAG = "PDFFragment";
    private static String pdfFileName = "adobe.pdf";

    private FrameLayout fl_pdf;
    private Context mContext;
    private File pdfFolder;
    private PDFViewPager pdfViewPager;

    public PDFFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pdfFolder = new File(Common.pdfPathFolder);
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf, container, false);
        fl_pdf = (FrameLayout) view.findViewById(R.id.fl_pdf);
        ((MainActivity) mContext).setTitle("PDFDemo");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CopyAsset copyAsset = new CopyAssetThreadImpl(getActivity().getApplicationContext(), new Handler(), new CopyAsset.Listener() {
            @Override
            public void success(String assetName, String destinationPath) {
                pdfViewPager = new PDFViewPager(mContext, getPDFPathOnSD());
                fl_pdf.removeAllViews();
                fl_pdf.addView(pdfViewPager);
            }

            @Override
            public void failure(Exception e) {
                Log.e(TAG, "failure: " + e.toString());
                Toast.makeText(mContext, "文件拷贝出错", Toast.LENGTH_SHORT).show();
            }
        });
        copyAsset.copy(pdfFileName, getPDFPathOnSD());
    }

    private String getPDFPathOnSD() {
        return new File(pdfFolder, pdfFileName).getAbsolutePath();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BasePDFPagerAdapter adapter = (BasePDFPagerAdapter) pdfViewPager.getAdapter();
        if (adapter != null) {
            adapter.close();
            adapter = null;
        }
    }
}
