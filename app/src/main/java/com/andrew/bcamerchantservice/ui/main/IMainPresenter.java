package com.andrew.bcamerchantservice.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public interface IMainPresenter {
    void onBackPressFragment();

    void changeFragment(Fragment fragment, FragmentTransaction fragmentTransaction);

    void checkConnectionInternet();
}
