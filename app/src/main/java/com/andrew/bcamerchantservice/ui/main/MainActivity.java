package com.andrew.bcamerchantservice.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.ui.home.HomeFragment;
import com.andrew.bcamerchantservice.ui.loyalty.LoyaltyFragment;
import com.andrew.bcamerchantservice.ui.loyalty.point_history.PointHistoryFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.RewardsFragment;
import com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.DetailRewardsFragment;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.mainforum.favorite.FavoriteFragment;
import com.andrew.bcamerchantservice.ui.mainforum.story.StoryFragment;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.otherprofile.baseinformation.InformationProfile;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.profile.hiddenforum.HiddenForumFragment;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.MyStoreInformation;
import com.andrew.bcamerchantservice.ui.profile.mystoreinformation.catalog.CatalogFragment;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.ui.tabpromorequest.TabPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo.ActivePromo;
import com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest.DetailPromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.PromoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo.ConfirmationPromoRequest;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.logo.LogoRequestFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.product.ProductFragment;
import com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncrequest.TNCRequestFragment;
import com.andrew.bcamerchantservice.utils.BaseActivity;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        , IMainView, View.OnClickListener {

    public static BottomNavigationView bottomNavigationView;
    public static FloatingActionButton floatingActionButton;

    private IMainPresenter presenter;

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
        presenter = new MainPresenter(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation_main);
        floatingActionButton = findViewById(R.id.fab_add_main);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) floatingActionButton.getLayoutParams();

//        layoutParams2.setBehavior(new FloatingActionButtonBehavior());
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        presenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());

        int bot_nav_menu = getPrefConfig().getMerchantPosition().getPosition_id().equals("position_1") ? R.menu.bot_nav_cashier : R.menu.bot_nav_owner;
        bottomNavigationView.inflateMenu(bot_nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        floatingActionButton.setOnClickListener(this);

        bottomNavigationView.setVisibility(View.VISIBLE);
        floatingActionButton.show();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);

        MainActivity.onBackPressFragment onBackPressFragment;

        if (fragment instanceof MainForum) {
            if (MainForum.isStoryVisible) {
                onBackPressFragment = new MainForum();
                onBackPressFragment.onBackPress(false, fragment.getContext());
            } else
                presenter.changeFragment(new Profile(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof SelectedThread) {
            if (SelectedThread.trendingIsVisible) {
                onBackPressFragment = new SelectedThread();
                onBackPressFragment.onBackPress(false, fragment.getContext());
            } else if (SelectedThread.frameIsVisible) {
                SelectedThread.frameIsVisible = false;
                SelectedThread.frameLayout.setVisibility(View.GONE);
            } else
                presenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
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
        } else if (fragment instanceof FavoriteFragment) {
            presenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof Profile) {
            onBackPressFragment = new Profile();
            if (MyStoreInformation.isDescriptionClick) {
                onBackPressFragment.onBackPress(false, fragment.getContext());
            } else
                finish();
//                presenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof TabPromoRequest) {
            onBackPressFragment = new ActivePromo();
            // Page = 1 adalah kondisi dimana viewpager menunjukkan Fragment Status Promosi
//            if (TabPromoRequest.PAGE == 1 && StatusPromo.isBottomSheetVisible) {
//                StatusPromo.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//                StatusPromo.isBottomSheetVisible = false;
//            } else
            onBackPressFragment.onBackPress(false, fragment.getContext());
        }  else if (fragment instanceof OtherProfile) {
            onBackPressFragment = new OtherProfile();
            if (InformationProfile.isDescriptionClick)
                onBackPressFragment.onBackPress(false, fragment.getContext());
            else
                presenter.changeFragment(new MainForum(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof HiddenForumFragment) {
            Profile profile = new Profile();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getSupportFragmentManager();

            bundle.putInt(Profile.GET_CURRENT_ITEM_VIEW_PAGER, 1);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, profile);

            profile.setArguments(bundle);
            fragmentTransaction.commit();
        } else if (fragment instanceof CatalogFragment) {
            presenter.changeFragment(new Profile(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof LoyaltyFragment) {
            Profile profile = new Profile();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getSupportFragmentManager();

            bundle.putInt(Profile.GET_CURRENT_ITEM_VIEW_PAGER, 2);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, profile);

            profile.setArguments(bundle);
            fragmentTransaction.commit();
        } else if (fragment instanceof RewardsFragment) {
            presenter.changeFragment(new LoyaltyFragment(), getSupportFragmentManager().beginTransaction());
        }else if(fragment instanceof PointHistoryFragment){
            presenter.changeFragment(new LoyaltyFragment(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof StoryFragment) {

        } else if (fragment instanceof DetailRewardsFragment) {
            presenter.changeFragment(new RewardsFragment(), getSupportFragmentManager().beginTransaction());
        } else if (fragment instanceof PromoRequestFragment) {
            onBackPressFragment = new PromoRequestFragment();
            onBackPressFragment.onBackPress(false, this);
        } else if (fragment instanceof TNCRequestFragment) {
            onBackPressFragment = new TNCRequestFragment();
            onBackPressFragment.onBackPress(false, this);
        } else if(fragment instanceof LogoRequestFragment){
            onBackPressFragment = new LogoRequestFragment();
            onBackPressFragment.onBackPress(false, this);
        } else if(fragment instanceof ProductFragment){
            onBackPressFragment = new ProductFragment();
            onBackPressFragment.onBackPress(false, this);
        } else if(fragment instanceof DetailPromoRequestFragment){
            onBackPressFragment = new DetailPromoRequestFragment();
            onBackPressFragment.onBackPress(false, this);
        }else if(fragment instanceof ConfirmationPromoRequest){
            onBackPressFragment = new ConfirmationPromoRequest();
            onBackPressFragment.onBackPress(false, this);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (fragment instanceof MainForum && !MainForum.isStoryVisible && !MainForum.isCategoryBottomSheetVisible) {
            bottomNavigationView.setVisibility(View.VISIBLE);
            floatingActionButton.show();
        } else floatingActionButton.hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_main:
                floatingActionButton.hide();
                bottomNavigationView.setVisibility(View.GONE);
                presenter.changeFragment(new NewThread(), getSupportFragmentManager().beginTransaction());
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainActivity.floatingActionButton.hide();
        switch (menuItem.getItemId()) {
            case R.id.profile:
                break;
            case R.id.promorequest:
//                changeFragment(new TabPromoRequest());
                break;
            case R.id.merchantforum:
                presenter.changeFragment(new MainForum(), fragmentTransaction);
                floatingActionButton.show();
                break;
            case R.id.bot_account:
                presenter.changeFragment(new Profile(), fragmentTransaction);
                break;
            case R.id.bot_forum:
                presenter.changeFragment(new MainForum(), fragmentTransaction);
                break;
            case R.id.bot_submission:
                if (getPrefConfig().getMerchantPosition().getPosition_id().equals("position_2"))
                    presenter.changeFragment(new TabPromoRequest(), fragmentTransaction);
                break;
            case R.id.bot_home:
                presenter.changeFragment(new HomeFragment(), fragmentTransaction);
                break;
            case R.id.bot_transaction:
                break;
        }
        return false;
    }
}
