package com.andrew.bcamerchantservice.ui.mainforum.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements TextWatcher, View.OnClickListener, ISearchView, SearchAdapter.onClickListener {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private SearchAdapter searchAdapter;

    private ISearchPresenter presenter;

    private List<Forum> forumList;
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
        RecyclerView recycler_search = v.findViewById(R.id.recycler_search_result_search_fragment);

        merchantMap = new HashMap<>();
        forumList = new ArrayList<>();

        searchAdapter = new SearchAdapter(mContext, forumList, merchantMap, this);

        edit_search.requestFocus();
        recycler_search.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_search.setAdapter(searchAdapter);

        image_back.setOnClickListener(this);
        linear_favorite.setOnClickListener(this);


        edit_search.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
                fragmentTransactions.replace(R.id.main_frame, new MainForum());
                break;
        }
        fragmentTransactions.commit();
    }

    @Override
    public void onLoadSearchResult(List<Forum> forumList) {
        this.forumList.clear();
        this.forumList.addAll(forumList);
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
}
