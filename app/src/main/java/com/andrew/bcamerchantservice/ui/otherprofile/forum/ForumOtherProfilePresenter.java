package com.andrew.bcamerchantservice.ui.otherprofile.forum;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.Merchant;
import com.andrew.bcamerchantservice.model.Report;
import com.andrew.bcamerchantservice.utils.Constant;
import com.andrew.bcamerchantservice.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForumOtherProfilePresenter implements IForumOtherPresenter {
    private DatabaseReference dbRef;
    private IForumOtherProfileView view;

    public ForumOtherProfilePresenter(IForumOtherProfileView view) {
        this.view = view;
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onLoadForum(final String MID) {
        dbRef.child(Constant.DB_REFERENCE_FORUM)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Forum> forumList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("mid").getValue().toString().equals(MID)) {
                                forumList.add(0, snapshot.getValue(Forum.class));
                                dbRef.child(Constant.DB_REFERENCE_MERCHANT_PROFILE + "/" + MID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                view.onMerchantProfile(dataSnapshot.getValue(Merchant.class));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        view.onGettingData(forumList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void loadReportList() {
        dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT_LIST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Report> reportList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.e("asd", snapshot.getValue().toString());
                            reportList.add(snapshot.getValue(Report.class));
                        }
                        view.onSuccessLoadReport(reportList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onSendReport(String content, final List<Report> reportList, final String FID, final String MID) {
        boolean isReportListAvailable = false;
        final String key = dbRef.child(Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + MID).push().getKey();
        Map<String, String> map = new HashMap<>();
        map.put("report_id", key);
        map.put("report_date", Utils.getTime("dd/MM/yyyy HH:mm"));
        map.put("report_mid", MID);

        map.put("report_content", content);

        for (Report report : reportList) {
            if (report.isReport_is_checked()) {
                isReportListAvailable = true;
                break;
            }
        }
        final String path = Constant.DB_REFERENCE_FORUM_REPORT + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_REPORTER + "/" + MID + "/" + key;

        if (isReportListAvailable)
            dbRef.child(path)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i = 0; i < reportList.size(); i++) {
                                Report report = reportList.get(i);
                                if (report.isReport_is_checked()) {
                                    dbRef.child(path + "/" + Constant.DB_REFERENCE_FORUM_REPORT_LIST + "/" + report.getFrlid())
                                            .setValue(report);
                                }

                                if (i == reportList.size() - 1)
                                    view.onSuccessSendReport();
                            }
                        }
                    });
        else
            dbRef.child(path)
                    .setValue(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.onSuccessSendReport();
                        }
                    });
    }

    @Override
    public void onHide(String FID, String MID) {
        String key = dbRef.push().getKey();
        final Forum.ForumHidden forumHidden = new Forum.ForumHidden(key, FID, Utils.getTime("dd/MM/yyyy HH:mm"));
        dbRef.child(Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID + "/" + key)
                .setValue(forumHidden)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        dbRef.child(Constant.DB_REFERENCE_FORUM + "/" + FID + "/" + Constant.DB_REFERENCE_FORUM_HIDDEN + "/" + MID)
                .setValue(forumHidden)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    @Override
    public void onUpdateViewCount(Map<String, Object> map, final Forum forum, final Merchant merchant) {
        dbRef.child(Constant.DB_REFERENCE_FORUM).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.onSuccessUpdateViewCount(forum, merchant);
                    }
                });
    }
}
