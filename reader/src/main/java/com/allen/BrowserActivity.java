package com.allen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.allen.db.DBhelper;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BrowserActivity extends ListActivity {
	private List<File> fileNameList;
	String s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filebrowser);
		initFileList();
	}

	private void initFileList() {
		File path = android.os.Environment.getExternalStorageDirectory();
		File[] f = path.listFiles();
		fill(f);
	}

	private void fill(File[] files) {
		fileNameList = new ArrayList<File>();
		for (File file : files) {
			if (isValidFileOrDir(file)) {
				fileNameList.add(file);
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				fileToStrArr(fileNameList));
		setListAdapter(adapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String fileName = null;
		File file = fileNameList.get(position);
		// 判断是否为文件夹
		if (file.isDirectory()) {
			File[] f = file.listFiles();
			fill(f);
		} else {
			final DBhelper helper = new DBhelper(getApplicationContext());
			fileName = file.getAbsolutePath();
			ContentValues values = new ContentValues();
			values.put("bname", getFileName(fileName));
			values.put("url", fileName);
			values.put("mark", "");
			helper.save(values);
			Intent intent = new Intent(BrowserActivity.this, ViewActivity.class);
			startActivity(intent);
			finish();

		}

	}

	/* 检查是否为合法的文件名，或者是否为路径 */
	private boolean isValidFileOrDir(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			String fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".txt")) {
				return true;
			}
		}
		return false;
	}

	private String[] fileToStrArr(List<File> fl) {
		ArrayList<String> fnList = new ArrayList<String>();
		for (int i = 0; i < fl.size(); i++) {
			String nameString = fl.get(i).getName();
			fnList.add(nameString);
		}
		return fnList.toArray(new String[0]);
	}

	// 获取文件名
	public String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return null;
		}
	}
}
