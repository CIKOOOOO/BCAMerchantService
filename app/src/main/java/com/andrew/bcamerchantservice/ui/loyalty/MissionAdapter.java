package com.andrew.bcamerchantservice.ui.loyalty;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Loyalty;
import com.andrew.bcamerchantservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.Holder> {

    private Context mContext;
    private List<Loyalty.Mission> missionList;
    private long merchant_full_transaction;
    private onItemClick onItemClick;

    public interface onItemClick {
        void onCollectMission(Loyalty.Mission mission);
    }

    public MissionAdapter(Context mContext, MissionAdapter.onItemClick onItemClick) {
        this.mContext = mContext;
        missionList = new ArrayList<>();
        this.onItemClick = onItemClick;
    }

    public void setMissionList(List<Loyalty.Mission> missionList, long merchant_full_transaction) {
        this.missionList = missionList;
        this.merchant_full_transaction = merchant_full_transaction;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_mission_loyalty, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final int pos = holder.getAdapterPosition();
        final Loyalty.Mission mission = missionList.get(i);

        holder.text_point.setText(mission.getMission_prize() + " Points");
        holder.text_time.setText("transaction by 1 " + "" + "to get");
        holder.text_transaction.setText("Rp " + Utils.priceFormat(mission.getMission_minimum_transaction()) + ",-");

        Drawable drawable = null;
        int color = 0;
        String status = "";
        boolean c = false;

        if (mission.isCollected()) {
            status = "Collected";
            drawable = mContext.getDrawable(R.drawable.rectangle_rounded_stroke_blue);
            color = mContext.getResources().getColor(R.color.blue_palette);
        } else {
            if (merchant_full_transaction >= mission.getMission_minimum_transaction()) {
                status = "Collect";
                c = true;
                drawable = mContext.getDrawable(R.drawable.background_reply_idle);
                color = mContext.getResources().getColor(R.color.white_color);
            } else {
                status = "Incomplete";
                drawable = mContext.getDrawable(R.drawable.rectangle_rounded_stroke_iron);
                color = mContext.getResources().getColor(R.color.iron_palette);
            }
        }

        holder.text_status_mission.setBackground(drawable);
        holder.text_status_mission.setTextColor(color);
        holder.text_status_mission.setText(status);

        if (c)
            /*
            * Saya gatau kenapa, it's work. so don't ever change it!!!!!!!
            * */
            holder.text_status_mission.setEnabled(true);
        else {
            holder.text_status_mission.setEnabled(false);
        }

        holder.text_status_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onCollectMission(mission);
            }
        });
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
