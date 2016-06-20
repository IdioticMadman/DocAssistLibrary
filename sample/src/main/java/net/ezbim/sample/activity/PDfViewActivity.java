package net.ezbim.sample.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.ezbim.docassist.pdfium.PDFView;
import net.ezbim.docassist.pdfium.ScrollBar;
import net.ezbim.docassist.pdfium.listener.OnPageChangeListener;
import net.ezbim.sample.R;

public class PDfViewActivity extends AppCompatActivity implements OnPageChangeListener {

    private static final String TAG = "PDfViewActivity";

    private final static int REQUEST_CODE = 42;
    private static String pdfFileName = "adobe.pdf";
    private PDFView pdfView;
    private ScrollBar scrollBar;
    private int pageNumber;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        setTitle("PDFViewDemo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
    }

    private void initView() {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
    }

    private void initData() {
        pdfView.setScrollBar(scrollBar);
        if (uri != null) {
            displayFromUri(uri);
        } else {
            displayFromAsset(pdfFileName);
        }
        setTitle(pdfFileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            displayFromUri(uri);
        }
    }

    private void displayFromAsset(String assetFileName) {
        //pdfFileName = assetFileName;

        pdfView.fromAsset(assetFileName)
                .defaultPage(1)
                .onPageChange(this)
                .swipeVertical(true)
                .showMinimap(false)
                .load();
    }

    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .swipeVertical(true)
                .showMinimap(false)
                .load();
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdfview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.pickFile:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, REQUEST_CODE);
        }
        return true;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page, pageCount));
    }
}
