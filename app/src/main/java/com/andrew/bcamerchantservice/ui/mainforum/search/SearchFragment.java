package com.andrew.bcamerchantservice.ui.mainforum.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.mainforum.favorite.FavoriteFragment;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.PrefConfig;
import com.andrew.bcamerchantservice.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements TextWatcher, View.OnClickListener, ISearchView
        , SearchAdapter.onClickListener, SearchProfileAdapter.onClick, SearchCategoryAdapter.onClick {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private SearchAdapter searchAdapter;
    private SearchProfileAdapter searchProfileAdapter;
    private CoordinatorLayout custom_loading;
    private SearchCategoryAdapter searchCategoryAdapter;
    private TextView text_dummy_forum, text_dummy_merchant;
    private RecyclerView recycler_category, recycler_forum_search, recycler_search_profile;

    private ISearchPresenter presenter;

    private List<Forum> forumList;
    private List<Merchant> merchantList;
    private Map<String, Merchant> merchantMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        presenter = new SearchPresenter(this);

        EditText edit_search = v.findViewById(R.id.edit_search_fragment);
        ImageButton image_back = v.findViewById(R.id.image_button_back_search_fragment);
        LinearLayout linear_favorite = v.findViewById(R.id.ll_dummy_00_search_fragment);
        recycler_forum_search = v.findViewById(R.id.recycler_search_result_search_fragment);
        recycler_search_profile = v.findViewById(R.id.recycler_profile_search);

        recycler_category = v.findViewById(R.id.recycler_category_search);
        custom_loading = v.findViewById(R.id.custom_loading_search);
        text_dummy_forum = v.findViewById(R.id.text_dummy_00_search);
        text_dummy_merchant = v.findViewById(R.id.text_dummy_01_search);

        merchantMap = new HashMap<>();
        forumList = new ArrayList<>();
        merchantList = new ArrayList<>();

        searchAdapter = new SearchAdapter(mContext, forumList, merchantMap, this);
        searchProfileAdapter = new SearchProfileAdapter(mContext, this);
        searchCategoryAdapter = new SearchCategoryAdapter(Constant.CATEGORY_SEARCH, mContext, this);

        edit_search.requestFocus();

        recycler_category.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recycler_search_profile.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_forum_search.setLayoutManager(new LinearLayoutManager(mContext));

        recycler_search_profile.setAdapter(searchProfileAdapter);
        recycler_forum_search.setAdapter(searchAdapter);
        recycler_category.setAdapter(searchCategoryAdapter);

        image_back.setOnClickListener(this);
        linear_favorite.setOnClickListener(this);

        edit_search.addTextChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI(v.findViewById(R.id.coordinator_search));
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Utils.hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        custom_loading.setVisibility(View.VISIBLE);
        presenter.onSearch(charSequence.toString(), prefConfig.getMID());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
        fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        switch (view.getId()) {
            case R.id.image_button_back_search_fragment:
                fragmentTransactions.replace(R.id.main_frame, new MainForum());
                break;

            case R.id.ll_dummy_00_search_fragment:
                fragmentTransactions.replace(R.id.main_frame, new FavoriteFragment());
                break;
        }
        fragmentTransactions.commit();
    }

    @Override
    public void onLoadSearchResult(List<Forum> forumList, List<Merchant> merchantList) {
        custom_loading.setVisibility(View.GONE);
        this.forumList = forumList;
        this.merchantList = merchantList;

        int visible_forum = forumList.size() == 0 ? View.GONE : View.VISIBLE;
        int visible_merchant = merchantList.size() == 0 ? View.GONE : View.VISIBLE;

        text_dummy_forum.setVisibility(visible_forum);
        text_dummy_merchant.setVisibility(visible_merchant);

        if (forumList.size() > 0 || merchantList.size() > 0) {
            recycler_category.setVisibility(View.VISIBLE);
        } else
            recycler_category.setVisibility(View.GONE);

        ((LinearLayout) v.findViewById(R.id.linear_result_search)).setVisibility(View.VISIBLE);
        searchProfileAdapter.setMerchantList(merchantList);
        searchProfileAdapter.notifyDataSetChanged();
        searchAdapter.setForumList(forumList);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            searchAdapter.setMerchantMap(merchantMap);
            searchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchClick(Forum forum, Merchant merchant) {
        Bundle bundle = new Bundle();
        SelectedThread selectedThread = new SelectedThread();

        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);
        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
        selectedThread.setArguments(bundle);

        FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
        fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransactions.replace(R.id.main_frame, selectedThread);
        fragmentTransactions.commit();
    }

    @Override
    public void profileOnClick(Merchant merchant) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle bundle = new Bundle();
        if (!merchant.getMid().equals(prefConfig.getMID())) {
            OtherProfile otherProfile = new OtherProfile();
            bundle.putParcelable(OtherProfile.GET_DATA_PROFILE, merchant);
            otherProfile.setArguments(bundle);
            fragmentTransaction.replace(R.id.main_frame, otherProfile);
        } else {
            fragmentTransaction.replace(R.id.main_frame, new Profile());
        }
        fragmentTransaction.commit();
    }

    @Override
    public void categoryOnClick(int pos) {
        int visible_forum = forumList.size() == 0 ? View.GONE : View.VISIBLE;
        int visible_merchant = merchantList.size() == 0 ? View.GONE : View.VISIBLE;
        switch (pos) {
            case 0:
                text_dummy_forum.setVisibility(visible_forum);
                text_dummy_merchant.setVisibility(visible_merchant);

                recycler_forum_search.setVisibility(visible_forum);
                recycler_search_profile.setVisibility(visible_merchant);
                break;
            case 1:
                text_dummy_forum.setVisibility(visible_forum);
                recycler_forum_search.setVisibility(visible_forum);
                text_dummy_merchant.setVisibility(View.GONE);
                recycler_search_profile.setVisibility(View.GONE);
                break;
            case 2:
                text_dummy_forum.setVisibility(View.GONE);
                recycler_forum_search.setVisibility(View.GONE);
                text_dummy_merchant.setVisibility(visible_merchant);
                recycler_search_profile.setVisibility(visible_merchant);
                break;
        }
    }
}
