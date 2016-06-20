package net.ezbim.sample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import net.ezbim.docassist.txt.TxtActivity;
import net.ezbim.docassist.txt.bean.BookInfo;
import net.ezbim.sample.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by robert on 2016/6/20.
 */
public class TxtDemoActivity extends TxtActivity {

    @Override
    public String getBookPath() {
        return Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "txt/";
    }

    @Override
    public BookInfo setBook() {

        return new BookInfo(1, "abc.txt", 1);

    }

    @Override
    public String setTitle() {
        return "TxtDemo";
    }

    public boolean onCreateOptionsMenu(Menu menu) {// 创建菜单
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_txt, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {// 操作菜单
        int ID = item.getItemId();
        switch (ID) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.fontsize:
                new AlertDialog.Builder(this)
                        .setTitle("请选择")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(font, whichSize,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        setFontSize(Integer.parseInt(font[which]));
                                        whichSize = which;
                                        //Toast.makeText(mContext, "您选中的是"+font[which], Toast.LENGTH_SHORT).show();
                                        // dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.nowprogress:
                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(net.ezbim.docassist.R.layout.bar, (ViewGroup) findViewById(net.ezbim.docassist.R.id.seekbar));
                SeekBar seek = (SeekBar) layout.findViewById(net.ezbim.docassist.R.id.seek);
                final TextView textView = (TextView) layout.findViewById(net.ezbim.docassist.R.id.textprogress);
                txtProgress = pagefactory.getCurProgress();
                seek.setProgress(txtProgress);
                textView.setText(String.format(getString(net.ezbim.docassist.R.string.progress), txtProgress + "%"));
                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressBar = 0;

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int progressBar = seekBar.getProgress();
                        int m_mbBufLen = pagefactory.getBufLen();
                        int pos = m_mbBufLen * progressBar / 100;
                        if (progressBar == 0) {
                            pos = 1;
                        }
                        pagefactory.setBeginPos(Integer.valueOf(pos));
                        try {
                            pagefactory.prePage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //setContentView(mPageWidget);
                        pagefactory.onDraw(mCurPageCanvas);
                        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
                        //mPageWidget.invalidate();
                        mPageWidget.postInvalidate();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //Toast.makeText(mContext, "StartTouch", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        if (fromUser) {
                            textView.setText(String.format(getString(net.ezbim.docassist.R.string.progress), progress + "%"));
                        }
                    }
                });
                new AlertDialog.Builder(this).setTitle("跳转").setView(layout)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(mContext, "您选中的是", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                        ).show();
                break;
            default:
                break;

        }
        return true;
    }
}
