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
import android.widget.TextView;
import android.widget.Toast;

import net.ezbim.docassist.txt.TxTUtil;
import net.ezbim.docassist.utils.CopyAsset;
import net.ezbim.docassist.utils.CopyAssetThreadImpl;
import net.ezbim.docassist.utils.FileUtil;
import net.ezbim.sample.Common;
import net.ezbim.sample.R;

import java.io.File;


public class TxtFragment extends Fragment {

    private static final String TAG = "TxtFragment";
    private static String txtFileName = "11.txt";
    private TextView tv_txt;
    private Context mContext;
    private File txtFolder;
    private File destinationFile;

    public TxtFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        txtFolder = new File(Common.txtPathFolder);
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
        tv_txt = (TextView) view.findViewById(R.id.tv_txt);
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
        FileUtil.getFileIncode(destinationFile);
        String text = TxTUtil.getText(mContext, destinationFile.getAbsolutePath());
        tv_txt.setText(text);
    }
}
