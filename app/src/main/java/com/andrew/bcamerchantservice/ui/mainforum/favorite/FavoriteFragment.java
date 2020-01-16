package com.andrew.bcamerchantservice.ui.mainforum.favorite;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.MainForum;
import com.andrew.bcamerchantservice.ui.otherprofile.OtherProfile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements View.OnClickListener, FavoriteAdapter.onItemClick, IFavoriteView {
    private View v;
    private Context mContext;
    private FavoriteAdapter favoriteAdapter;
    private PrefConfig prefConfig;

    private IFavoritePresenter presenter;

    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_favorite, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        ImageButton img_back = v.findViewById(R.id.img_btn_back_favorite_fragment);
        RecyclerView recycler_favorite = v.findViewById(R.id.recycler_favorite);

        presenter = new FavoritePresenter(this);

        forumList = new ArrayList<>();
        merchantMap = new HashMap<>();

        favoriteAdapter = new FavoriteAdapter(mContext, forumList, merchantMap, this);

        presenter.onGetFavoriteList(prefConfig.getMID());

        recycler_favorite.setLayoutManager(new LinearLayoutManager(mContext));
        recycler_favorite.setAdapter(favoriteAdapter);

        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_btn_back_favorite_fragment:
                changeFragment(new MainForum());
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransactions = getFragmentManager().beginTransaction();
        fragmentTransactions.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransactions.replace(R.id.main_frame, fragment);
        fragmentTransactions.commit();
    }

    @Override
    public void onClick(int pos) {
        final Forum forumThread = forumList.get(pos);
        final Merchant merchant = merchantMap.get(forumThread.getMid());
        Map<String, Object> map = new HashMap<>();
        map.put(forumThread.getFid() + "/view_count", forumThread.getView_count() + 1);
        presenter.onUpdateViewCount(map, forumThread, merchant);
    }

    @Override
    public void profileOnClick(int pos, Merchant merchant) {
        OtherProfile otherProfile = new OtherProfile();
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();

        bundle.putParcelable(OtherProfile.GET_DATA_PROFILE, merchant);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, otherProfile);

        otherProfile.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (merchant != null) {
            if (!merchantMap.containsKey(merchant.getMid())) {
                merchantMap.put(merchant.getMid(), merchant);
                favoriteAdapter.setMerchantMap(merchantMap);
                favoriteAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoadForum(List<Forum> forumList) {
        this.forumList.clear();
        this.forumList.addAll(forumList);
        favoriteAdapter.setForumList(forumList);
        favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessUpdateViewCount(Forum forum, Merchant merchant) {
        SelectedThread selectedThread = new SelectedThread();
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = getFragmentManager();

        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forum);
        bundle.putParcelable(SelectedThread.GET_MERCHANT, merchant);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
