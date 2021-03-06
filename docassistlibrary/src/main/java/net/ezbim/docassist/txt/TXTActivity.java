package net.ezbim.docassist.txt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import net.ezbim.docassist.R;
import net.ezbim.docassist.txt.bean.BookInfo;

import java.io.File;
import java.io.IOException;

/**
 * Created by robert on 2016/6/20.
 */
public abstract class TxtActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    //public final static int OPENMARK = 0;
    //public final static int SAVEMARK = 1;
    //public final static int TEXTSET = 2;
    private static final int REQUEST_CODE = 50;

    protected PageWidget mPageWidget;
    protected Bitmap mCurPageBitmap, mNextPageBitmap;
    protected Canvas mCurPageCanvas, mNextPageCanvas;
    protected BookPageFactory pagefactory;
    protected static Boolean isExit = false;//用于判断是否推出
    protected static Boolean hasTask = false;
    protected int whichSize = 4;//当前的字体大小
    protected int txtProgress = 0;//当前阅读的进度

    protected final String[] font = new String[]{"40", "46", "50", "56", "60", "66", "70"};
    //protected int curPostion;
    //protected DbHelper db;
    protected Context mContext;
    //protected Cursor mCursor;
    protected BookInfo book = null;
    // protected SetupInfo setup = null;
    private int actionBarHeight;
    private int widthPixels;
    private int heightPixels;
    private String bookPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = TxtActivity.this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(setTitle());
        //获取actionBar的高度
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        //Display用于提供屏幕尺寸和分辨率的信息
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels - this.actionBarHeight;
        System.out.println(widthPixels + "\t" + heightPixels);
        mCurPageBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);//当前页位图
        mNextPageBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);//下一页位图

        mCurPageCanvas = new Canvas(mCurPageBitmap);//显示当前页位图
        mNextPageCanvas = new Canvas(mNextPageBitmap);//显示下一页位图
        pagefactory = new BookPageFactory(widthPixels, heightPixels);
        pagefactory.setBgBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.bg));

        //取得传递的参数
        Intent intent = getIntent();
        String bookid = intent.getStringExtra("bookid");
        //db = new DbHelper(mContext);
//        try {
//            book = db.getBookInfo(Integer.parseInt(bookid));
//            setup = db.getSetupInfo();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        book = setBook();
        bookPath = getBookPath() + book.bookname;
        display();
    }

    public void openFile(String bookPath, String bookname) {
        this.book.bookname = bookname;
        this.bookPath = bookPath;
        display();
    }

    public void display() {
        if (new File(bookPath).exists()) {
            pagefactory.setFileName(book.bookname);
            mPageWidget = new PageWidget(this, widthPixels, heightPixels);
            setContentView(mPageWidget);
            pagefactory.openbook(bookPath);
            int m_mbBufLen = pagefactory.getBufLen();

            if (book.bookmark > 0) {
                //whichSize = setup.fontsize;
                //pagefactory.setFontSize(Integer.parseInt(font[setup.fontsize]));
                pagefactory.setFontSize(Integer.parseInt(font[whichSize]));
                //pos = String.valueOf(m_mbBufLen*0.1);
                int begin = m_mbBufLen * 100 / 100;
                pagefactory.setBeginPos(Integer.valueOf(book.bookmark));
                try {
                    pagefactory.prePage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //setContentView(mPageWidget);
                pagefactory.onDraw(mNextPageCanvas);
                mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
                //mPageWidget.invalidate();
                mPageWidget.postInvalidate();
                //db.close();
            } else {
                pagefactory.onDraw(mCurPageCanvas);
                //setContentView(mPageWidget);
                mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
            }

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
                                if (pagefactory.isfirstPage()) {
                                    Toast.makeText(mContext, "已经是第一页", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                                pagefactory.onDraw(mNextPageCanvas);
                            } else {
                                try {
                                    pagefactory.nextPage();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                if (pagefactory.islastPage()) {
                                    Toast.makeText(mContext, "已经是最后一页", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
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
        } else {
            View errorView = getLayoutInflater().inflate(R.layout.open_error, null);
            setContentView(errorView);
            //Snackbar.make(errorView, "电子书不存在！可能已经删除", Snackbar.LENGTH_SHORT).show();
            //TxtActivity.this.finish();
        }
    }

    /**
     * 设置图书的路径
     *
     * @return 图书的路径
     */
    public abstract String getBookPath();

    /**
     * 设置图书的信息
     *
     * @return 图书的信息
     */
    public abstract BookInfo setBook();

    public abstract String setTitle();

    protected void setFontSize(int size) {
        pagefactory.setFontSize(size);
        int pos = pagefactory.getCurPostionBeg();
        pagefactory.setBeginPos(pos);
        try {
            pagefactory.nextPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(mPageWidget);
        pagefactory.onDraw(mNextPageCanvas);
        //mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
        mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
        mPageWidget.invalidate();
        //mPageWidget.postInvalidate();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //addBookMark();
            this.finish();
        }
        return false;
    }

    // 创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_txt, menu);
        return true;
    }

    // 操作菜单
    public boolean onOptionsItemSelected(MenuItem item) {
        int ID = item.getItemId();
        //选择文件
        if (ID == R.id.pickFile) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            startActivityForResult(intent, REQUEST_CODE);
        } else if (ID == android.R.id.home) {
            //返回上一层
            onBackPressed();
        } else if (ID == R.id.fontsize) {
            //设置字体大小
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
        } else if (ID == R.id.nowprogress) {
            //跳转进度
            LayoutInflater inflater = getLayoutInflater();
            final View layout = inflater.inflate(R.layout.bar, (ViewGroup) findViewById(R.id.seekbar));
            SeekBar seek = (SeekBar) layout.findViewById(R.id.seek);
            final TextView textView = (TextView) layout.findViewById(R.id.textprogress);
            txtProgress = pagefactory.getCurProgress();
            seek.setProgress(txtProgress);
            textView.setText(String.format(getString(R.string.progress), txtProgress + "%"));
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
                        textView.setText(String.format(getString(R.string.progress), progress + "%"));
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
        }
        return true;
    }

//    //添加书签
//    public void addBookMark() {
//        Message msg = new Message();
//        msg.what = SAVEMARK;
//        msg.arg1 = whichSize;
//        curPostion = pagefactory.getCurPostion();
//        msg.arg2 = curPostion;
//        mhHandler.sendMessage(msg);
//    }
//
//    Handler mhHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//
//                case TEXTSET:
//                    pagefactory.changBackGround(msg.arg1);
//                    pagefactory.onDraw(mCurPageCanvas);
//                    mPageWidget.postInvalidate();
//                    break;
//
//                case OPENMARK:
//                    try {
//                        mCursor = db.select();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (mCursor.getCount() > 0) {
//                        mCursor.moveToPosition(mCursor.getCount() - 1);
//                        String pos = mCursor.getString(2);
//                        String tmp = mCursor.getString(1);
//
//                        pagefactory.setBeginPos(Integer.valueOf(pos));
//                        try {
//                            pagefactory.prePage();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        pagefactory.onDraw(mNextPageCanvas);
//                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
//                        mPageWidget.invalidate();
//                        db.close();
//                    }
//                    break;
//
//                case SAVEMARK:
//                    try {
//                        db.update(book.id, book.bookname, String.valueOf(msg.arg2));
//                        db.updateSetup(setup.id, String.valueOf(msg.arg1), "0", "0");
//                        //mCursor = db.select();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    db.close();
//                    break;
//
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };
}
