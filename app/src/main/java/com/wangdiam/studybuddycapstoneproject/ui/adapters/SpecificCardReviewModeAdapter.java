package com.wangdiam.studybuddycapstoneproject.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wangdiam.studybuddycapstoneproject.ui.fragments.SpecificReviewModeCardFragment;

public class SpecificCardReviewModeAdapter extends FragmentStatePagerAdapter {
    public static final String POSITION = "POSITION";

    public SpecificCardReviewModeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION,i);
        Fragment fragment = new SpecificReviewModeCardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
