package com.andrew.bcamerchantservice.ui.loyalty;

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

import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.Holder> {

    private Context mContext;
    private List<Loyalty.Mission> missionList;

    public MissionAdapter(Context mContext, List<Loyalty.Mission> missionList) {
        this.mContext = mContext;
        this.missionList = missionList;
    }

    public void setMissionList(List<Loyalty.Mission> missionList) {
        this.missionList = missionList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_mission_loyalty, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Loyalty.Mission mission = missionList.get(pos);

        holder.text_point.setText(mission.getMission_prize() + " Points");
        holder.text_time.setText("transaction by 1 " + "" + "to get");
        holder.text_transaction.setText("Rp " + Utils.priceFormat(mission.getMission_minimum_transaction()) + ",-");
    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_transaction, text_time, text_point, text_status_mission;

        Holder(@NonNull View itemView) {
            super(itemView);
            text_point = itemView.findViewById(R.id.recycler_point_loyalty);
            text_transaction = itemView.findViewById(R.id.recycler_min_transaction_loyalty);
            text_time = itemView.findViewById(R.id.recycler_text_time_limit_loyalty);
            text_status_mission = itemView.findViewById(R.id.recycler_status_mission_loyalty);
        }
    }
}
