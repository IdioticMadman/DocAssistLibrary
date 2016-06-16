package net.ezbim.sample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.ezbim.sample.R;
import net.ezbim.sample.fragment.FragmentFactory;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content, FragmentFactory.createFragment(FragmentFactory.PDFFragment))
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pdf:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_content, FragmentFactory.createFragment(FragmentFactory.PDFFragment))
                        .commit();
                return true;
            case R.id.txt:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_content, FragmentFactory.createFragment(FragmentFactory.TxtFragment))
                        .commit();
                return true;
            case R.id.pic:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_content, FragmentFactory.createFragment(FragmentFactory.PictureFragment))
                        .commit();
                return true;
            case R.id.office:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_content, FragmentFactory.createFragment(FragmentFactory.OfficeFragment))
                        .commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
