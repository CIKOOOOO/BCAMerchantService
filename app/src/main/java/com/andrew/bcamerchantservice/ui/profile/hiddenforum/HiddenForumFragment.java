package com.andrew.bcamerchantservice.ui.profile.hiddenforum;


import android.content.Context;
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
import com.andrew.bcamerchantservice.ui.profile.Profile;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HiddenForumFragment extends Fragment implements IHiddenForumView, HiddenForumAdapter.forumOnClick, View.OnClickListener {

    private View v;
    private Context mContext;
    private PrefConfig prefConfig;
    private HiddenForumAdapter hiddenForumAdapter;

    private IHiddenForumPresenter presenter;

    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;
    private Map<String, String> hiddenMap;

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
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_hidden = v.findViewById(R.id.recycler_hidden_forum);
        ImageButton img_btn_back = v.findViewById(R.id.image_button_back_hidden_forum);

        presenter = new HiddenForumPresenter(this);

        forumList = new ArrayList<>();
        merchantMap = new HashMap<>();
        hiddenMap = new HashMap<>();

        hiddenForumAdapter = new HiddenForumAdapter(mContext, forumList, merchantMap, this);

        recycler_hidden.setLayoutManager(new LinearLayoutManager(mContext));

        presenter.loadHiddenForum(prefConfig.getMID());

        recycler_hidden.setAdapter(hiddenForumAdapter);

        img_btn_back.setOnClickListener(this);
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            hiddenForumAdapter.setMerchantMap(merchantMap);
            hiddenForumAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadHiddenForum(List<Forum> forumList, String FHID) {
        if(forumList.size() > 0){
            if (!hiddenMap.containsKey(forumList.get(0).getFid()))
                hiddenMap.put(forumList.get(0).getFid(), FHID);
        }
        this.forumList = forumList;
        hiddenForumAdapter.setForumList(forumList);
        hiddenForumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessUnHideForum() {
        Toast.makeText(mContext, "Success to unhide thread!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(Forum forum, Merchant merchant) {
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

    @Override
    public void onUnHide(String FID) {
        String FHID = "";
        if (hiddenMap.get(FID) != null)
            FHID = hiddenMap.get(FID);

        presenter.onRemoveHideForum(prefConfig.getMID(), FHID, FID);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_button_back_hidden_forum) {
            Profile profile = new Profile();
            Bundle bundle = new Bundle();
            FragmentManager fragmentManager = getFragmentManager();

            bundle.putInt(Profile.GET_CURRENT_ITEM_VIEW_PAGER, 1);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, profile);

            profile.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }
}
