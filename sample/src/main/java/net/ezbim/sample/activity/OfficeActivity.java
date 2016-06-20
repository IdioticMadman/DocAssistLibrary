package net.ezbim.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.ezbim.docassist.office.MyIntent;
import net.ezbim.sample.Common;
import net.ezbim.sample.R;

import java.io.File;

/**
 * Created by robert on 2016/6/20.
 */
public class OfficeActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_open_doc;
    private Button btn_open_excel;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office);
        setTitle("OfficeDemo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
        btn_open_doc = (Button) findViewById(R.id.btn_open_doc);
        btn_open_excel = (Button) findViewById(R.id.btn_open_excel);
        btn_open_doc.setOnClickListener(this);
        btn_open_excel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_doc:
                Intent wordFileIntent = MyIntent.getWordFileIntent(Common.officePathFolder + File.separator + "abc.docx");
                boolean available = MyIntent.isIntentAvailable(mContext, wordFileIntent);
                if (available) {
                    startActivity(wordFileIntent);
                } else {
                    Toast.makeText(mContext, "请安装打开office的APP", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_excel:
                startActivity(MyIntent.getWordFileIntent(Common.officePathFolder + File.separator + "excel.xls"));
                break;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
