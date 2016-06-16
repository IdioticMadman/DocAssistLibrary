package net.ezbim.sample.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import net.ezbim.docassist.image.PhotoView;
import net.ezbim.docassist.image.PhotoViewAttacher;
import net.ezbim.sample.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {


    public PictureFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.iv_photo);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb_progress);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Glide.with(this)
                .load("http://image.zzd.sm.cn/156303908323762768.gif")
                .into(new GlideDrawableImageViewTarget(photoView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        pb.setVisibility(View.GONE);
                        attacher.update();
                    }
                });

        return view;
    }

}
