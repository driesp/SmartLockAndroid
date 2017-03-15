package com.github.driesp.smartlockandroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.Vector;

/**
 * Created by Dries Peeters on 23/02/2017.
 */
public class customPagerAdapter extends FragmentPagerAdapter implements ViewPager.PageTransformer{
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private customLinearLayout cur = null;
    private customLinearLayout next = null;
    private mainActivity context;
    private FragmentManager fm;
    private float scale;
    private int FIRST_PAGE = 0;
    private int PAGES = 0;
    private int LOOPS = 1000;
    private Vector<Lock> LOCKS ;

    public customPagerAdapter(mainActivity context, FragmentManager fm, int FirstPage, int Pages, int Loops, Vector<Lock> locks) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.FIRST_PAGE = FirstPage;
        this.PAGES = Pages;
        this.LOOPS = Loops;
        this.LOCKS = locks;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == FIRST_PAGE)
            scale = BIG_SCALE;
        else
            scale = SMALL_SCALE;

        position = position % PAGES;
        return customFragment.newInstance(context,position, LOCKS.elementAt(position).room(), LOCKS.elementAt(position).accessible(),  scale);
    }

    @Override
    public int getCount() {
        return PAGES * LOOPS;
    }

    @Override
    public void transformPage(View page, float position) {
        customLinearLayout myLinearLayout = (customLinearLayout) page.findViewById(R.id.root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        myLinearLayout.setScaleBoth(scale);
    }
}
