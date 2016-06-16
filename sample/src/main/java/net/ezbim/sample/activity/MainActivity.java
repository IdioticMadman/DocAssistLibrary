package net.ezbim.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import net.ezbim.sample.R;
import net.ezbim.sample.fragment.PDFFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PDFFragment pdfFragment = new PDFFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content, pdfFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


}
