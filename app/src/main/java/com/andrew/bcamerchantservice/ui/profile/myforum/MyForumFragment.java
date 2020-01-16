package com.andrew.bcamerchantservice.ui.profile.myforum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.ui.mainforum.ThreadAdapter;
import com.andrew.bcamerchantservice.ui.newthread.NewThread;
import com.andrew.bcamerchantservice.ui.profile.myforum.hiddenforum.HiddenForumFragment;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyForumFragment extends Fragment implements IMyForumView, ThreadAdapter.onItemClick, View.OnClickListener {

    private View v;
    private Context mContext;
    private ThreadAdapter threadAdapter;
    private PrefConfig prefConfig;

    private IMyForumPresenter presenter;

    private List<Forum> forumList;
    private Map<String, Merchant> merchantMap;

    public MyForumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_my_forum, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);

        RecyclerView recycler_forum = v.findViewById(R.id.recycler_my_forum);
        TextView text_hidden = v.findViewById(R.id.text_hidden_my_forum);

        merchantMap = new HashMap<>();
        forumList = new ArrayList<>();

        presenter = new MyForumPresenter(this);

        recycler_forum.setLayoutManager(new LinearLayoutManager(mContext));

        merchantMap.put(prefConfig.getMID(), prefConfig.getMerchantData());

        threadAdapter = new ThreadAdapter(mContext, forumList, merchantMap, this);
        recycler_forum.setAdapter(threadAdapter);

        presenter.loadForum(prefConfig.getMID());

        text_hidden.setOnClickListener(this);
    }

    @Override
    public void onLoadResult(List<Forum> forums) {
        forumList = forums;
        threadAdapter.setForumList(forumList);
        threadAdapter.notifyDataSetChanged();
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

    @Override
    public void onSuccessDeleteThread(int pos) {
        Toast.makeText(mContext, "Hapus Forum Berhasil!", Toast.LENGTH_SHORT).show();
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
    public void onDelete(final int pos, final Forum forum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menghapus thread berjudul " + forum.getForum_title() + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        presenter.onDelete(forum.getFid(), pos);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void profileOnClick(int pos, Merchant merchant) {

    }

    @Override
    public void onHide(String FID, String forum_title, int pos) {

    }

    @Override
    public void onShowReport(Merchant merchant, Forum forum) {

    }

    @Override
    public void onEditThread(Forum forum) {
        NewThread newThread = new NewThread();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(NewThread.EDIT_THREAD, forum);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, newThread);

        newThread.setArguments(bundle);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_hidden_my_forum) {
            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.main_frame, new HiddenForumFragment());

            fragmentTransaction.commit();
        }
    }
}
