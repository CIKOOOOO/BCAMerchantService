package com.andrew.bcamerchantservice.ui.profile.hiddenforum;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HiddenForumFragment extends Fragment {

    private View v;

    public HiddenForumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_hidden_forum, container, false);
        initVar();
        return v;
    }

    private void initVar() {

    }
}
