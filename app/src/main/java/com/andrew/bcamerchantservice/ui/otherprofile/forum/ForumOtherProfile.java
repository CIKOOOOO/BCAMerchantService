package com.andrew.bcamerchantservice.ui.otherprofile.forum;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.andrew.bcamerchantservice.ui.mainforum.ThreadAdapter;
import com.andrew.bcamerchantservice.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForumOtherProfile extends Fragment implements ThreadAdapter.onItemClick, IForumOtherProfileView {

    public static final String GETTING_MERCHANT_DATA = "getting_merchant_data";

    private View v;
    private Context mContext;

    private ThreadAdapter threadAdapter;

    private IForumOtherPresenter presenter;

    private List<Forum> forumList;
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
        presenter = new ForumOtherProfilePresenter(this);

        RecyclerView recyclerView = v.findViewById(R.id.recycler_forum_other_profile);

        forumList = new ArrayList<>();
        merchantMap = new HashMap<>();

        threadAdapter = new ThreadAdapter(mContext, forumList, merchantMap, this);

        if (bundle != null) {
            if (bundle.getString(GETTING_MERCHANT_DATA) != null) {
                String key = bundle.getString(GETTING_MERCHANT_DATA);
                presenter.onLoadForum(key);
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(threadAdapter);
    }

    @Override
    public void onClick(int pos) {

    }

    @Override
    public void onDelete(int pos, Forum forum) {

    }

    @Override
    public void profileOnClick(int pos, Merchant merchant) {

    }

    @Override
    public void onHide(String FID) {

    }

    @Override
    public void onShowReport(Merchant merchant, Forum forum) {
//        final List<Report> reportList = new ArrayList<>(Constant.getReport());

//        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
//        final View codeView = LayoutInflater.from(mContext).inflate(R.layout.custom_report, null);
//        TextView name = codeView.findViewById(R.id.report_name);
//        TextView thread = codeView.findViewById(R.id.report_title);
//        final TextView error = codeView.findViewById(R.id.show_error_content_report);
//        final EditText content = codeView.findViewById(R.id.etOther_Report);
//        Button send = codeView.findViewById(R.id.btnSubmit_Report);
//        Button cancel = codeView.findViewById(R.id.btnCancel_Report);
//        RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);
//        final CheckBox checkBox = codeView.findViewById(R.id.check_other);
//        final ReportAdapter reportAdapter = new ReportAdapter(reportList, codeView.getContext());
//
//        recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));
//
//        codeBuilder.setView(codeView);
//        final AlertDialog codeAlert = codeBuilder.create();
//
//        name.setText(": " + merchant.getMerchant_name());
//        thread.setText(": " + forum.getForum_title());
//
//        recyclerView.setAdapter(reportAdapter);
//
//        content.setEnabled(false);
//
//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!check) {
//                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
//                    check = true;
//                    content.setEnabled(true);
//                } else {
//                    content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
//                    check = false;
//                    content.setEnabled(false);
//                }
//            }
//        });
//
//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                error.setVisibility(View.GONE);
//                if (check) {
//                    if (content.getText().toString().isEmpty())
//                        error.setVisibility(View.VISIBLE);
//                    else {
//                        Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
//                                , Toast.LENGTH_SHORT).show();
//                        codeAlert.dismiss();
//                    }
//                } else if (isChecked(reportList)) {
//                    Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
//                            , Toast.LENGTH_SHORT).show();
//                    codeAlert.dismiss();
//                } else {
//                    error.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                codeAlert.dismiss();
//            }
//        });
//
//        codeAlert.show();
    }

    @Override
    public void onGettingData(List<Forum> forumList) {
        this.forumList.clear();
        this.forumList.addAll(forumList);
        threadAdapter.setForumList(forumList);
        threadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMerchantProfile(Merchant merchant) {
        if (!merchantMap.containsKey(merchant.getMid())) {
            merchantMap.put(merchant.getMid(), merchant);
            threadAdapter.setMerchantMap(merchantMap);
            threadAdapter.notifyDataSetChanged();
        }
    }

    private boolean isChecked(List<Report> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isReport_is_checked()) return true;
        }
        return false;
    }
}
