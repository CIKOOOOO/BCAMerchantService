package com.andrew.bcamerchantservice.ui.mainforum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.Holder> {

    private List<Report> reportList;
    private Context context;

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public ReportAdapter(List<Report> reportList, Context context) {
        this.reportList = reportList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_report_checkbox, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        final Report report = reportList.get(i);
        holder.checkBox.setText(report.getReport_name());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!report.isReport_is_checked()) {
                    report.setReport_is_checked(true);
                } else {
                    report.setReport_is_checked(false);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        Holder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.recycler_check);
            textView = itemView.findViewById(R.id.recycler_info_check);
        }
    }
}
