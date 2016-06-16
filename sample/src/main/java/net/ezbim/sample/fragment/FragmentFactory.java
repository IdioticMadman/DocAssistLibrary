package net.ezbim.sample.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentFactory {

    public static final int PDFFragment = 0;
    public static final int TxtFragment = 1;
    public static final int PictureFragment = 2;
    public static final int OfficeFragment = 3;
    private static Map<Integer, Fragment> mFragments = new HashMap<>();

    public static Fragment createFragment(int position) {
        Fragment fragment = null;
        fragment = mFragments.get(position);  //在集合中取出来Fragment
        if (fragment == null) {  //如果再集合中没有取出来 需要重新创建
            if (position == PDFFragment) {
                fragment = new PDFFragment();
            } else if (position == TxtFragment) {
                fragment = new TxtFragment();
            } else if (position == PictureFragment) {
                fragment = new PictureFragment();
            } else if (position == OfficeFragment) {
                fragment = new OfficeFragment();
            }
            if (fragment != null) {
                mFragments.put(position, fragment);// 把创建好的Fragment存放到集合中缓存起来
            }
        }
        return fragment;

    }
}
