package com.younge.wynews.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.younge.wynews.R;

/**
 * Created by Allen Lake on 2016/2/25 0025.
 */
public class ForumFragment extends Fragment{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forum, container, false);
   /*     initData();
        initView();
        setAdapter();
        setListener();*/
        return view;
    }
}
