package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.confirmationpromo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public class PaymentTypeAdapter extends RecyclerView.Adapter<PaymentTypeAdapter.Holder> {

    private Context mContext;
    private List<PromoRequest.Facilities> facilitiesList;

    public PaymentTypeAdapter(Context mContext, List<PromoRequest.Facilities> facilitiesList) {
        this.mContext = mContext;
        this.facilitiesList = facilitiesList;
    }

    public void setFacilitiesList(List<PromoRequest.Facilities> facilitiesList) {
        this.facilitiesList = facilitiesList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_information, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        PromoRequest.Facilities facilities = facilitiesList.get(i);
        holder.text_number.setText("\u2022");
        holder.text_payment_name.setText(facilities.getFacilities_name());
    }

    @Override
    public int getItemCount() {
        return facilitiesList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView text_number, text_payment_name;

        public Holder(@NonNull View itemView) {
            super(itemView);
            text_number = itemView.findViewById(R.id.recycler_text_number_information);
            text_payment_name = itemView.findViewById(R.id.recycler_text_information);
        }
    }
}
