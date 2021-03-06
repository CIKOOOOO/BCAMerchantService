package com.andrew.bcamerchantservice.ui.otherprofile.forum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.ui.mainforum.ReportAdapter;
import com.andrew.bcamerchantservice.ui.selectedthread.SelectedThread;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumOtherProfile extends Fragment implements OtherProfileAdapter.onItemClick, IForumOtherProfileView {

    public static final String GETTING_MERCHANT_DATA = "getting_merchant_data";

    private View v;
    private Context mContext;

    private OtherProfileAdapter otherProfileAdapter;
    private AlertDialog codeAlert;
    private PrefConfig prefConfig;
    private ReportAdapter reportAdapter;

    private IForumOtherPresenter presenter;

    private List<Forum> forumList;
    private List<Report> reportList;
    private Map<String, Merchant> merchantMap;

    private boolean check;

    public ForumOtherProfile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_forum_other_profile, container, false);
        initVar();
        return v;
    }

    private void initVar() {
        Bundle bundle = getArguments();

        check = false;

        mContext = v.getContext();
        prefConfig = new PrefConfig(mContext);
        presenter = new ForumOtherProfilePresenter(this);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_forum_other_profile);

        forumList = new ArrayList<>();
        reportList = new ArrayList<>();
        merchantMap = new HashMap<>();

        otherProfileAdapter = new OtherProfileAdapter(mContext, forumList, merchantMap, this);
        reportAdapter = new ReportAdapter(reportList, mContext);

        if (bundle != null) {
            if (bundle.getString(GETTING_MERCHANT_DATA) != null) {
                String key = bundle.getString(GETTING_MERCHANT_DATA);
                presenter.onLoadForum(key, prefConfig.getMID());
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(otherProfileAdapter);
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
    public void onDelete(int pos, Forum forum) {

    }

    @Override
    public void onHide(final String FID, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Apa Anda yakin untuk menyembunyikan forum berjudul " + title + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int a) {
                        presenter.onHide(FID, prefConfig.getMID());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    @Override
    public void onShowReport(Merchant merchant, final Forum forum) {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        final View codeView = LayoutInflater.from(mContext).inflate(R.layout.custom_report, null);
        TextView name = codeView.findViewById(R.id.report_name);
        TextView thread = codeView.findViewById(R.id.report_title);
        final TextView error = codeView.findViewById(R.id.show_error_content_report);
        final EditText content = codeView.findViewById(R.id.etOther_Report);
        Button send = codeView.findViewById(R.id.btnSubmit_Report);
        Button cancel = codeView.findViewById(R.id.btnCancel_Report);
        RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);
        final CheckBox checkBox = codeView.findViewById(R.id.check_other);

        recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

        codeBuilder.setView(codeView);
        if (codeView.getParent() == null)
            codeAlert = codeBuilder.create();

        name.setText(": " + merchant.getMerchant_name());
        thread.setText(": " + forum.getForum_title());

        recyclerView.setAdapter(reportAdapter);

        presenter.loadReportList();

        check = false;
        content.setEnabled(false);
        checkBox.setChecked(false);
        content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!check) {
                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
                    check = true;
                    content.setEnabled(true);
                } else {
                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
                    check = false;
                    content.setEnabled(false);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setVisibility(View.GONE);
                if (check) {
                    if (content.getText().toString().isEmpty())
                        error.setVisibility(View.VISIBLE);
                    else {
                        presenter.onSendReport(content.getText().toString(), reportList, forum.getFid(), prefConfig.getMID());
                    }
                } else if (isChecked(reportList)) {
                    presenter.onSendReport(content.getText().toString(), reportList, forum.getFid(), prefConfig.getMID());
                } else {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeAlert.dismiss();
            }
        });

        codeAlert.show();
    }

    @Override
    public void onGettingData(List<Forum> forumList) {
        this.forumList.clear();
        this.forumList.addAll(forumList);
        otherProfileAdapter.setForumList(forumList);
        otherProfileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            otherProfileAdapter.setMerchantMap(merchantMap);
            otherProfileAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccessSendReport() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.report_sent)
                , Toast.LENGTH_SHORT).show();
        codeAlert.dismiss();
    }

    @Override
    public void onSuccessLoadReport(List<Report> reportList) {
        this.reportList = reportList;
        reportAdapter.setReportList(reportList);
        reportAdapter.notifyDataSetChanged();
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

    private boolean isChecked(List<Report> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isReport_is_checked()) return true;
        }
        return false;
    }
}
