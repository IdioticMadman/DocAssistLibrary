package net.ezbim.sample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
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
import net.ezbim.docassist.utils.FileUtil;
import net.ezbim.sample.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by robert on 2016/6/20.
 */
public class TxtDemoActivity extends TxtActivity {

    private static final int REQUEST_CODE = 50;
    private Uri uri;
    private String bookName;
    private String bookPath;

    @Override
    public String getBookPath() {
        bookPath = Environment.getExternalStorageDirectory() + File.separator + "sample" + File.separator + "txt/";
        return bookPath;
    }

    @Override
    public BookInfo setBook() {
        bookName = "abc.txt";
        return new BookInfo(1, bookName, 1);

    }

    @Override
    public String setTitle() {
        return "TxtDemo";
    }

    // 创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_txt, menu);
        return true;
    }

    //选择文件后，activity返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            displayFromUri(uri);
        }
    }

    /**
     * 根据uri打开文件
     *
     * @param uri 文件的uri
     */
    private void displayFromUri(Uri uri) {

        bookName = FileUtil.getFileName(mContext, uri);
        if (bookName.endsWith(".txt")) {
            bookPath = FileUtil.getFilePath(mContext, uri);
            openFile(bookPath, bookName);
        } else {
            Snackbar.make(mPageWidget, "请打开txt文件", Snackbar.LENGTH_SHORT).show();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {// 操作菜单
        int ID = item.getItemId();
        switch (ID) {
            //选择文件
            case R.id.pickFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, REQUEST_CODE);
                break;
            //返回上一层
            case android.R.id.home:
                onBackPressed();
                break;
            //设置字体大小
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
            //跳转进度
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
