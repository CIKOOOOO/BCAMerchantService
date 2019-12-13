package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncpromorequest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public class AgreementAdapter extends RecyclerView.Adapter<AgreementAdapter.Holder> {

    private Context mContext;
    private List<PromoRequest.Agreement> agreementList;
    private onClick onClick;

    public AgreementAdapter(Context mContext, List<PromoRequest.Agreement> agreementList, AgreementAdapter.onClick onClick) {
        this.mContext = mContext;
        this.agreementList = agreementList;
        this.onClick = onClick;
    }

    public void setAgreementList(List<PromoRequest.Agreement> agreementList) {
        this.agreementList = agreementList;
    }

    public interface onClick {
        void onCheckboxClick(int pos, boolean c);
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
        PromoRequest.Agreement agreement = agreementList.get(pos);
        holder.checkBox.setText(agreement.getAgreement());
        holder.checkBox.setChecked(agreement.isCheck());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onCheckboxClick(pos, holder.checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return agreementList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        Holder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.recycler_check);
        }
    }
}
