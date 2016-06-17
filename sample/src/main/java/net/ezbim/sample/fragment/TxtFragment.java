package net.ezbim.sample.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import net.ezbim.docassist.txt.BookPageFactory;
import net.ezbim.docassist.txt.PageWidget;
import net.ezbim.docassist.utils.CopyAsset;
import net.ezbim.docassist.utils.CopyAssetThreadImpl;
import net.ezbim.sample.Common;
import net.ezbim.sample.R;

import java.io.File;
import java.io.IOException;


public class TxtFragment extends Fragment {

    private static final String TAG = "TxtFragment";
    private static String txtFileName = "abc.txt";
    private Context mContext;
    private File destinationFile;

    private PageWidget mPageWidget;
    Bitmap mCurPageBitmap, mNextPageBitmap;
    Canvas mCurPageCanvas, mNextPageCanvas;
    BookPageFactory pagefactory;
    //long markid;
    private FrameLayout fl_content;

    public TxtFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        File txtFolder = new File(Common.txtPathFolder);
        if (txtFolder.exists()) {
            if (!txtFolder.isDirectory()) {
                boolean mkdirs = txtFolder.mkdirs();
                Log.e(TAG, "创建txt文件夹是否成功：" + mkdirs);
            }
        } else {
            boolean mkdirs = txtFolder.mkdirs();
            Log.e(TAG, "创建txt文件夹是否成功：" + mkdirs);
        }
        destinationFile = new File(txtFolder, txtFileName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_txt, container, false);
        fl_content = (FrameLayout) view.findViewById(R.id.fl_content);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (destinationFile.exists()) {
            setTxtView();
        } else {
            CopyAsset copyAsset = new CopyAssetThreadImpl(mContext, new Handler(), new CopyAsset.Listener() {
                @Override
                public void success(String assetName, String destinationPath) {
                    setTxtView();
                }

                @Override
                public void failure(Exception e) {
                    Log.e(TAG, "failure: " + e.toString());
                    Toast.makeText(mContext, "文件拷贝出错", Toast.LENGTH_SHORT).show();
                }
            });
            copyAsset.copy(txtFileName, destinationFile.getAbsolutePath());
        }
    }

    private void setTxtView() {
        mPageWidget = new PageWidget(mContext);

        Handler handler = new Handler();
        mPageWidget.setHandler(handler);
        // 自适应
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.e(TAG, "屏幕宽度" + dm.widthPixels);
        Log.e(TAG, "屏幕高度" + dm.heightPixels);
        mCurPageBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_4444);
        mNextPageBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_4444);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        pagefactory = new BookPageFactory(dm.widthPixels, dm.heightPixels);
        //pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg1));

        try {

            String filenameString = destinationFile.getAbsolutePath();
            Log.v(TAG, filenameString);
            //Log.v("t", "" + markid);

            // 电子书地址
            pagefactory.openbook(filenameString);
            // 查询数据库 书签位置

            pagefactory.onDraw(mCurPageCanvas);

        } catch (IOException e1) {
            e1.printStackTrace();

        }

        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {

                boolean ret = false;
                if (v == mPageWidget) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        mPageWidget.abortAnimation();
                        mPageWidget.calcCornerXY(e.getX(), e.getY());

                        pagefactory.onDraw(mCurPageCanvas);
                        if (mPageWidget.DragToRight()) {
                            try {
                                pagefactory.prePage();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            if (pagefactory.isfirstPage())
                                return false;
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            try {
                                pagefactory.nextPage();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            if (pagefactory.islastPage())
                                return false;
                            pagefactory.onDraw(mNextPageCanvas);
                        }
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                    }

                    ret = mPageWidget.doTouchEvent(e);
                    return ret;
                }
                return false;
            }

        });
        fl_content.removeAllViews();
        fl_content.addView(mPageWidget);

    }

    /*private void readText() {
        String fileIncode = FileUtil.getFileIncode(destinationFile);
        String text = TxTUtil.getText(mContext, destinationFile.getAbsolutePath(), fileIncode);
    }*/
}
