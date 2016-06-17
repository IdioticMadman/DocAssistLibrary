package com.allen;

import com.allen.db.DBhelper;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ViewActivity extends ListActivity {

	private static final int DIALOG_LOGOUT_ID = 0;
	private Bundle bundle;
	String s;
	private String fileNameKey = "fileName";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		final DBhelper helper = new DBhelper(this);
		Cursor c = helper.query();
		if (c.getCount() == 0) {
			Toast t = Toast.makeText(this, "还没有数据，请按功能键添加", Toast.LENGTH_SHORT);
			t.show();
		}
		String[] form = { "bname" };
		int[] to = { R.id.txt };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.row, c, form, to);
		ListView listView = getListView();
		listView.setAdapter(adapter);

		final String[] sel = { "打开", "删除" };

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				// _id
				final long temp = arg3;
				builder.setTitle("您要").setItems(sel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (which == 0) {
							Intent intent = new Intent(ViewActivity.this, TurntestActivity.class);
							bundle = new Bundle();
							Cursor c = helper.getWritableDatabase()
									.rawQuery("select * from dbTbl where _id=" + "'" + temp + "'", null);
							if (c.moveToFirst()) {
								do {
									s = c.getString(c.getColumnIndex("url"));
								} while (c.moveToNext());
							}
							Log.v("tag", s);
							bundle.putString(fileNameKey, s);
							bundle.putLong("mark", temp);
							intent.putExtras(bundle);
							startActivity(intent);
						} else if (which == 1) {
							helper.del((int) temp);
							Cursor c = helper.query();
							String[] form = { "bname" };
							int[] to = { R.id.txt };

							SimpleCursorAdapter adapter = new SimpleCursorAdapter(ViewActivity.this, R.layout.row, c, form, to);
							ListView listView = getListView();
							listView.setAdapter(adapter);
							Intent intent = new Intent(ViewActivity.this, ViewActivity.class);
							startActivity(intent);
							finish();
						}
					}
				});
				AlertDialog ad = builder.create();
				ad.show();
			}
		});
		helper.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, "增加书本");
		menu.add(0, 2, 2, "关于");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			Intent intent = new Intent(ViewActivity.this, BrowserActivity.class);
			startActivity(intent);
			finish();
		} else {

		}
		return true;
	}

	@Override

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			this.showTips();

			return false;

		}

		return false;

	}

	private void showTips() {

		AlertDialog alertDialog = new AlertDialog.Builder(this)

				.setTitle("退出程序")

				.setMessage("是否退出程序")

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						finish();

					}

				}).setNegativeButton("取消",

						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {

								return;

							}
						})
				.create(); // 创建对话框

		alertDialog.show(); // 显示对话框

	}

}
