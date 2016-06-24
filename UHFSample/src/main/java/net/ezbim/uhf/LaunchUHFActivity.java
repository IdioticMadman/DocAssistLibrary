package net.ezbim.uhf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import net.ezbim.uhflibrary.UHFActivity;

import java.util.List;

public class LaunchUHFActivity extends AppCompatActivity {

    private final int requestCode = 20;
    private final int ACTIVITY = 1;
    private final int DIALOG = 2;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_uhf);
        et = (EditText) findViewById(R.id.et_epc);
    }

    public void activity(View view) {
        Intent intent = new Intent(this, UHFActivity.class);
        intent.putExtra("style", ACTIVITY);
        this.startActivityForResult(intent, requestCode);
    }

    public void dialog(View view) {
        Intent intent = new Intent(this, UHFActivity.class);
        intent.putExtra("style", DIALOG);
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20 && resultCode == 25) {
            List<String> datas = data.getStringArrayListExtra("epcStrs");
            et.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            for (String str : datas) {
                et.append("读取到的标签数据:"+str+"\n");
            }
        }
    }
}
