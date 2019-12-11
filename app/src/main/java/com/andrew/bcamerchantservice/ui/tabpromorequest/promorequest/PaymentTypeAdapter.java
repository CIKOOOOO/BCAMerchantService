package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.ArrayList;
import java.util.List;

public class PaymentTypeAdapter extends RecyclerView.Adapter<PaymentTypeAdapter.Holder> {

    private Context mContext;
    private List<PromoRequest.Facilities> facilitiesList;
    private onClick onClick;

    public PaymentTypeAdapter(Context mContext, onClick onClick) {
        this.mContext = mContext;
        this.onClick = onClick;
        facilitiesList = new ArrayList<>();
    }

    public void addFacilities(PromoRequest.Facilities facilities) {
        facilitiesList.add(facilities);
        notifyDataSetChanged();
    }

    public interface onClick {
        void checkboxIsChecked(int pos, boolean check);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_report_checkbox, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final PromoRequest.Facilities facilities = facilitiesList.get(pos);

        holder.checkBox.setChecked(facilities.isCheck());
        holder.checkBox.setText(facilities.getFacilities_name());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.checkboxIsChecked(pos, holder.checkBox.isChecked());
                facilities.setCheck(holder.checkBox.isChecked());
                facilitiesList.set(pos, facilities);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilitiesList.size();
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
