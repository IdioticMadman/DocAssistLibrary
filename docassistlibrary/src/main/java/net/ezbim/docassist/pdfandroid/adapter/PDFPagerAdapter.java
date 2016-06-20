package net.ezbim.docassist.pdfandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.ezbim.docassist.R;
import net.ezbim.docassist.image.PhotoViewAttacher;

import java.lang.ref.WeakReference;


/**
 * @author robert
 * @version 1.0
 * @time 2016/6/15.
 * @description net.ezbim.docassist.pdf.bean
 */
public class PDFPagerAdapter extends BasePDFPagerAdapter {
    SparseArray<WeakReference<PhotoViewAttacher>> attachers;

    public PDFPagerAdapter(Context context, String pdfPath, int offScreenSize) {
        super(context, pdfPath, offScreenSize);
        attachers = new SparseArray<>();
    }

    @Override
    @SuppressWarnings("NewApi")
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.view_pdf_page, container, false);
        ImageView iv = (ImageView) v.findViewById(R.id.imageView);

        if (renderer == null || getCount() < position)
            return v;

        PdfRenderer.Page page = getPDFPage(renderer, position);

        Bitmap bitmap = bitmapContainer.get(position);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        page.close();

        PhotoViewAttacher attacher = new PhotoViewAttacher(iv);

        attachers.put(position, new WeakReference<PhotoViewAttacher>(attacher));

        iv.setImageBitmap(bitmap);
        attacher.update();
        ((ViewPager) container).addView(v, 0);

        return v;
    }

    @Override
    public void close() {
        super.close();
        if (attachers != null) {
            attachers.clear();
            attachers = null;
        }
    }
}
