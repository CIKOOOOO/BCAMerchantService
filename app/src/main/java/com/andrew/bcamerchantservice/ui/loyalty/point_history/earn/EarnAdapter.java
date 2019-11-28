package com.andrew.bcamerchantservice.ui.loyalty.point_history.earn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EarnAdapter extends RecyclerView.Adapter<EarnAdapter.Holder> {

    private Context mContext;
    private List<Loyalty.Earn> earnList;
    private Map<String, Loyalty.Rewards> rewardsMap;

    public EarnAdapter(Context mContext) {
        this.mContext = mContext;
        earnList = new ArrayList<>();
        rewardsMap = new HashMap<>();
    }

    public void setEarnList(List<Loyalty.Earn> earnList) {
        this.earnList = earnList;
    }

    public void setRewardsMap(Map<String, Loyalty.Rewards> rewardsMap) {
        this.rewardsMap = rewardsMap;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        if (i == 0)
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_parent_spend, viewGroup, false);
        else
            v = LayoutInflater.from(mContext).inflate(R.layout.recycler_child_spend, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public int getItemViewType(int position) {
        return earnList.get(position).getEarn_id().equals("parent") ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Loyalty.Earn earn = earnList.get(pos);

        if (getItemViewType(pos) == 1) {
            String type = earn.getEarn_type().equals("reward") ? "Mission" : "Transaction";
            holder.text_title.setVisibility(View.GONE);

            holder.text_spend_type.setText(type);
            holder.text_point.setText(earn.getEarn_point() + " Points");

            if (pos == earnList.size() - 1) {
                holder.line.setVisibility(View.GONE);
            } else if (pos + 1 < earnList.size() - 1 && earnList.get(pos + 1).getEarn_id().equals("parent")) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }
        } else {
            try {
                String date = earn.getEarn_date().equals(Utils.getTime("dd/MM/yyyy"))
                        ? "Today" : Utils.formatDateFromDateString("dd/MM/yyyy", "EEEE, dd MMM yyyy", earn.getEarn_date());
                holder.text_parent.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return earnList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_parent, text_title, text_spend_type, text_point;
        View line;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_parent = itemView.findViewById(R.id.recycler_date_parent_spend);
            text_title = itemView.findViewById(R.id.recycler_text_title_spend);
            text_point = itemView.findViewById(R.id.recycler_text_point_spend);
            text_spend_type = itemView.findViewById(R.id.recycler_text_type_spend);
            line = itemView.findViewById(R.id.recycler_line_spend);
        }
    }
}
