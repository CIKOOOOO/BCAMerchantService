package com.andrew.bcamerchantservice.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.previewproquest.PreviewProquest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequest;
import com.andrew.bcamerchantservice.utils.BaseActivity;
import com.andrew.bcamerchantservice.utils.BottomNavigationViewBehavior;
import com.andrew.bcamerchantservice.utils.FloatingActionButtonBehavior;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , IMainView, View.OnClickListener {

    public static BottomNavigationView bottomNavigationView;
    public static FloatingActionButton floatingActionButton;

    private IMainPresenter iMainPresenter;

    public interface onBackPressFragment {
        void onBackPress(boolean check, Context context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVar();
    }

    private void initVar() {
        iMainPresenter = new MainPresenter(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        floatingActionButton = findViewById(R.id.fab_add_main);

        iMainPresenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        floatingActionButton.setOnClickListener(this);

        bottomNavigationView.setVisibility(View.VISIBLE);
        floatingActionButton.show();

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams();
        layoutParams2.setBehavior(new FloatingActionButtonBehavior());
        layoutParams.setBehavior(new BottomNavigationViewBehavior());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);

        MainActivity.onBackPressFragment onBackPressFragment;

        if (fragment instanceof MainForum) {
            if (MainForum.trendingIsVisible) {
                onBackPressFragment = new MainForum();
                onBackPressFragment.onBackPress(false, fragment.getContext());
            }
            if (MainForum.showcase_condition) {
                MainForum.showcase_condition = false;
                MainForum.frame_showcase.setVisibility(View.GONE);
            } else
                iMainPresenter.changeFragment(new Profile(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof SelectedThread) {
            if (SelectedThread.trendingIsVisible) {
                onBackPressFragment = new SelectedThread();
                onBackPressFragment.onBackPress(false, fragment.getContext());
            } else if (SelectedThread.frameIsVisible) {
                SelectedThread.frameIsVisible = false;
                SelectedThread.frameLayout.setVisibility(View.GONE);
            } else
                iMainPresenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof NewThread) {
            onBackPressFragment = new NewThread();
            switch (NewThread.THREAD_CONDITION) {
                case NewThread.EDIT_THREAD_SELECTED:
                    onBackPressFragment.onBackPress(false, fragment.getContext());
                    break;
                case NewThread.EDIT_THREAD_REPLY:
                    onBackPressFragment.onBackPress(false, fragment.getContext());
                    break;
                default:
                    onBackPressFragment.onBackPress(false, fragment.getContext());
                    break;
            }
        } else if (fragment instanceof Profile) {
            if (Profile.showcase_condition) {
                Profile.frame_add_showcase.setVisibility(View.GONE);
                Profile.showcase_condition = false;
            } else
                finish();
//                iMainPresenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof TabPromoRequest) {
            onBackPressFragment = new PromoRequest();
            // Page = 1 adalah kondisi dimana viewpager menunjukkan Fragment Status Promosi
//            if (TabPromoRequest.PAGE == 1 && StatusPromo.isBottomSheetVisible) {
//                StatusPromo.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//                StatusPromo.isBottomSheetVisible = false;
//            } else
            onBackPressFragment.onBackPress(false, fragment.getContext());
        } else if (fragment instanceof PreviewProquest) {
            onBackPressFragment = new PreviewProquest();
            onBackPressFragment.onBackPress(false, fragment.getContext());
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (fragment instanceof MainForum) {
            floatingActionButton.show();
        } else floatingActionButton.hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_main:
                floatingActionButton.hide();
                bottomNavigationView.setVisibility(View.GONE);
                iMainPresenter.changeFragment(new NewThread(), getSupportFragmentManager().beginTransaction());
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.profile:
                break;
            case R.id.promorequest:
//                changeFragment(new TabPromoRequest());
                break;
            case R.id.merchantforum:
                iMainPresenter.changeFragment(new MainForum(), fragmentTransaction);
                floatingActionButton.show();
                break;
            case R.id.bot_home:
//                changeFragment(new HomeFragment());
                break;
            case R.id.bot_account:
                iMainPresenter.changeFragment(new Profile(), getSupportFragmentManager().beginTransaction());
                break;
            case R.id.bot_edc:
                break;
            case R.id.bot_store:
                break;
            case R.id.bot_transaction:
                break;
        }
        return false;
    }
}
