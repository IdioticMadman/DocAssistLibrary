package net.ezbim.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import net.ezbim.docassist.image.PhotoView;
import net.ezbim.docassist.image.PhotoViewAttacher;
import net.ezbim.sample.R;

/**
 * Created by robert on 2016/6/20.
 */
public class PictureActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("PictureDemo");
        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pb_progress);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Glide.with(this)
                .load("http://image.zzd.sm.cn/156303908323762768.gif")
                .into(new GlideDrawableImageViewTarget(photoView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (pb != null) {
                            pb.setVisibility(View.GONE);
                        }
                        attacher.update();
                    }
                });
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
