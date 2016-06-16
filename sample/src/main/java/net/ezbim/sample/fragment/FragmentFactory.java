package net.ezbim.sample.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentFactory {

    private static Map<Integer, Fragment> mFragments = new HashMap<>();

    public static Fragment createFragment(int position) {
        Fragment fragment = null;
        fragment = mFragments.get(position);  //在集合中取出来Fragment
        if (fragment == null) {  //如果再集合中没有取出来 需要重新创建
            if (position == 0) {
                fragment = new PDFFragment();
            } else if (position == 1) {
                fragment = new TxtFragment();
            } else if (position == 2) {
                fragment = new PictureFragment();
            } else if (position == 3) {
                fragment = new OfficeFragment();
            }
            if (fragment != null) {
                mFragments.put(position, fragment);// 把创建好的Fragment存放到集合中缓存起来
            }
        }
        return fragment;

    }
}
