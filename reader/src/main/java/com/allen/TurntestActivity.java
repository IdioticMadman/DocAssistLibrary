package com.allen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;

public class TurntestActivity extends Activity {
	/** Called when the activity is first created. */
	private String filenameString;
	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;
	BookPageFactory pagefactory;
	private DisplayMetrics dm;
	private Handler handler = null;
	long markid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mPageWidget = new PageWidget(this);
		setContentView(mPageWidget);
		handler = new Handler();
		mPageWidget.setHandler(handler);
		// 自适应
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mCurPageBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		pagefactory = new BookPageFactory(dm.widthPixels, dm.heightPixels);
		pagefactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg1));

		try {

			Bundle bunde = this.getIntent().getExtras();
			filenameString = bunde.getString("fileName");
			markid = bunde.getLong("mark");
			Log.v("ta", filenameString);
			Log.v("t", "" + markid);

			// 电子书地址
			pagefactory.openbook(filenameString);
			// 查询数据库 书签位置

			pagefactory.onDraw(mCurPageCanvas);

		} catch (IOException e1) {
			e1.printStackTrace();

		}

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
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

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "改变字体");
		menu.add(0, 2, 2, "改变亮度");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			LayoutInflater inflater = getLayoutInflater();
			final View layout = inflater.inflate(R.layout.dialog, null);

			new AlertDialog.Builder(this).setTitle("更改字体大小").setView(layout)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							EditText et = (EditText) layout.findViewById(R.id.et_size);
							int size = Integer.parseInt(et.getText().toString());
							pagefactory.ChangeFontSize(size);

							pagefactory.onDraw(mCurPageCanvas);
							mPageWidget.updateBookUI();
						}
					}).setNegativeButton("取消", null).show();

		}
		if (item.getItemId() == 2) {
			LayoutInflater inflater = getLayoutInflater();
			final View layout = inflater.inflate(R.layout.seek, null);
			new AlertDialog.Builder(this).setTitle("调整亮度").setView(layout)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SeekBar sBar = (SeekBar) layout.findViewById(R.id.seekbar);
							sBar.setProgress((int) (android.provider.Settings.System.getInt(getContentResolver(),
									android.provider.Settings.System.SCREEN_BRIGHTNESS, 255)));
							sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

								@Override
								public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
									if (fromUser) {
										int tmpint = seekBar.getProgress();
										android.provider.Settings.System.getInt(getContentResolver(),
												android.provider.Settings.System.SCREEN_BRIGHTNESS, tmpint);
										tmpint = android.provider.Settings.System.getInt(getContentResolver(),
												android.provider.Settings.System.SCREEN_BRIGHTNESS, -1);

										WindowManager.LayoutParams lp = getWindow().getAttributes();
										Float tmpFloat = (float) (tmpint / 255);
										if (0 < tmpFloat && tmpFloat <= 1) {
											lp.screenBrightness = tmpFloat;
										}
										getWindow().setAttributes(lp);
									}

								}

								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub

								}
							});

						}
					}).show();

		}

		return true;

	}

}