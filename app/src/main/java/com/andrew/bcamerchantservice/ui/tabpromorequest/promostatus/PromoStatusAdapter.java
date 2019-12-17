package com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoStatusAdapter extends RecyclerView.Adapter<PromoStatusAdapter.Holder> {
    private Context mContext;
    private List<PromoRequest> promoRequestList;
    private Map<String, PromoRequest.PromoType> promoTypeMap;
    private Map<String, PromoRequest.PromoStatus> promoStatusMap;
    private onClick onClick;

    public PromoStatusAdapter(Context mContext, onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        promoRequestList = new ArrayList<>();
        promoTypeMap = new HashMap<>();
        promoStatusMap = new HashMap<>();
    }

    public interface onClick {
        void onPromoClick(String promo_request_id);
    }

    public void setPromoStatusMap(Map<String, PromoRequest.PromoStatus> promoStatusMap) {
        this.promoStatusMap = promoStatusMap;
    }

    public void setPromoRequestList(List<PromoRequest> promoRequestList) {
        this.promoRequestList = promoRequestList;
    }

    public void setPromoTypeMap(Map<String, PromoRequest.PromoType> promoTypeMap) {
        this.promoTypeMap = promoTypeMap;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_promo_request, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final PromoRequest promoRequest = promoRequestList.get(pos);

        holder.text_promo_period_time.setText("Periode: " + promoRequest.getPromo_start_date()
                + " - " + promoRequest.getPromo_end_date());
        holder.text_promo_title.setText(promoRequest.getPromo_title());

        if (promoTypeMap.get(promoRequest.getPromo_type_id()) != null) {
            PromoRequest.PromoType promoType = promoTypeMap.get(promoRequest.getPromo_type_id());
            if (promoType != null)
                holder.text_promo_type.setText(promoType.getPromo_name());
        }

        if (promoStatusMap.get(promoRequest.getPromo_status()) != null) {
            PromoRequest.PromoStatus promoStatus = promoStatusMap.get(promoRequest.getPromo_status());
            if (promoStatus != null)
                holder.text_promo_status.setText(promoStatus.getPromo_status_name());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onPromoClick(promoRequest.getPromo_request_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return promoRequestList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_promo_type, text_promo_title, text_promo_status, text_promo_period_time;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_promo_type = itemView.findViewById(R.id.recycler_promo_type);
            text_promo_title = itemView.findViewById(R.id.recycler_promo_title);
            text_promo_status = itemView.findViewById(R.id.recycler_promo_status);
            text_promo_period_time = itemView.findViewById(R.id.recycler_promo_period);
        }
    }
}
