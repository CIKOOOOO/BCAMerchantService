package com.andrew.bcamerchantservice.ui.loyalty.point_history.spend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;

import java.util.ArrayList;
import java.util.List;

public class SpendAdapter extends RecyclerView.Adapter<SpendAdapter.Holder> {

    private Context mContext;
    private List<Loyalty.Spend> spendList;

    public SpendAdapter(Context mContext) {
        this.mContext = mContext;
        spendList = new ArrayList<>();
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
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        if (getItemViewType(i) == 1) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return spendList.size();
    }

    class Holder extends RecyclerView.ViewHolder {


        Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
