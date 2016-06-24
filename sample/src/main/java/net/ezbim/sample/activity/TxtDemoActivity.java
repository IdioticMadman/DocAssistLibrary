package net.ezbim.sample.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;

import net.ezbim.docassist.txt.TxtActivity;
import net.ezbim.docassist.txt.bean.BookInfo;
import net.ezbim.docassist.utils.FileUtil;

import java.io.File;

/**
 * Created by robert on 2016/6/20.
 */
public class TxtDemoActivity extends TxtActivity {

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
}
