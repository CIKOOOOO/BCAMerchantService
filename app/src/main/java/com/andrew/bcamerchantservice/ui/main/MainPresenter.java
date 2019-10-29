package com.andrew.bcamerchantservice.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.andrew.bcamerchantservice.R;

public class MainPresenter implements IMainPresenter {

    private IMainView iMainView;

    MainPresenter(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    @Override
    public void onBackPressFragment() {


    }

    @Override
    public void changeFragment(Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
