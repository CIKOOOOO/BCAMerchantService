package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.utils.Constant;

public class InformationTextAdapter extends RecyclerView.Adapter<InformationTextAdapter.Holder> {

    private Context mContext;
    private String[] information_list;

    public InformationTextAdapter(Context mContext) {
        this.mContext = mContext;
        information_list = new String[0];
    }

    public void setInformation_list(String[] information_list) {
        this.information_list = information_list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_information, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.text_number.setText(i + 1 + ".");
        holder.text_information.setText(information_list[i]);
    }

    @Override
    public int getItemCount() {
        return information_list.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_number, text_information;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_number = itemView.findViewById(R.id.recycler_text_number_information);
            text_information = itemView.findViewById(R.id.recycler_text_information);
        }
    }
}
