package com.andrew.bcamerchantservice.ui.loyalty.rewards.detailrewards.termcondition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;

public class TermConditionAdapter extends RecyclerView.Adapter<TermConditionAdapter.Holder> {

    private String[] tncList;
    private Context mContext;

    public TermConditionAdapter(String[] tncList, Context mContext) {
        this.tncList = tncList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_term_condition, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.text_tnc.setText(i + 1 + ". " + tncList[i]);
    }

    @Override
    public int getItemCount() {
        return tncList.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_tnc;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_tnc = itemView.findViewById(R.id.recycler_text_term_condition);
        }
    }
}
