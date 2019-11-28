package com.andrew.bcamerchantservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Loyalty {
    private String loyalty_id, loyalty_benefit, loyalty_logo, loyalty_name;
    private int loyalty_exp;

    public Loyalty() {
    }

    public Loyalty(String loyalty_id, String loyalty_benefit, String loyalty_logo, String loyalty_name, int loyalty_exp) {
        this.loyalty_id = loyalty_id;
        this.loyalty_benefit = loyalty_benefit;
        this.loyalty_logo = loyalty_logo;
        this.loyalty_name = loyalty_name;
        this.loyalty_exp = loyalty_exp;
    }

    public String getLoyalty_id() {
        return loyalty_id;
    }

    public String getLoyalty_benefit() {
        return loyalty_benefit;
    }

    public String getLoyalty_logo() {
        return loyalty_logo;
    }

    public String getLoyalty_name() {
        return loyalty_name;
    }

    public int getLoyalty_exp() {
        return loyalty_exp;
    }

    public static class Mission{
        private String mission_id;
        private long mission_minimum_transaction;
        private int mission_prize;
        private boolean isCollected;

        public Mission() {
        }

        public boolean isCollected() {
            return isCollected;
        }

        public void setCollected(boolean collected) {
            isCollected = collected;
        }

        public String getMission_id() {
            return mission_id;
        }

        public long getMission_minimum_transaction() {
            return mission_minimum_transaction;
        }

        public int getMission_prize() {
            return mission_prize;
        }
    }

    public static class Rewards implements Parcelable {
        private String rewards_id,
                rewards_name,
                rewards_thumbnail,
                rewards_image,
                rewards_tnc,
                rewards_description,
                rewards_tutorial;

        private int rewards_point;

        public Rewards() {
        }

        protected Rewards(Parcel in) {
            rewards_id = in.readString();
            rewards_name = in.readString();
            rewards_thumbnail = in.readString();
            rewards_image = in.readString();
            rewards_tnc = in.readString();
            rewards_description = in.readString();
            rewards_tutorial = in.readString();
            rewards_point = in.readInt();
        }

        public static final Creator<Rewards> CREATOR = new Creator<Rewards>() {
            @Override
            public Rewards createFromParcel(Parcel in) {
                return new Rewards(in);
            }

            @Override
            public Rewards[] newArray(int size) {
                return new Rewards[size];
            }
        };

        public String getRewards_id() {
            return rewards_id;
        }

        public String getRewards_name() {
            return rewards_name;
        }

        public String getRewards_thumbnail() {
            return rewards_thumbnail;
        }

        public String getRewards_image() {
            return rewards_image;
        }

        public String getRewards_tnc() {
            return rewards_tnc;
        }

        public String getRewards_description() {
            return rewards_description;
        }

        public String getRewards_tutorial() {
            return rewards_tutorial;
        }

        public int getRewards_point() {
            return rewards_point;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(rewards_id);
            parcel.writeString(rewards_name);
            parcel.writeString(rewards_thumbnail);
            parcel.writeString(rewards_image);
            parcel.writeString(rewards_tnc);
            parcel.writeString(rewards_description);
            parcel.writeString(rewards_tutorial);
            parcel.writeInt(rewards_point);
        }
    }

    public static class Spend {
        private String spend_id, rewards_id, spend_date;
        private int spend_point;

        public Spend() {
        }

        public Spend(String spend_id, String rewards_id, String spend_date, int spend_point) {
            this.spend_id = spend_id;
            this.rewards_id = rewards_id;
            this.spend_date = spend_date;
            this.spend_point = spend_point;
        }

        public void setSpend_id(String spend_id) {
            this.spend_id = spend_id;
        }

        public String getSpend_id() {
            return spend_id;
        }

        public String getRewards_id() {
            return rewards_id;
        }

        public String getSpend_date() {
            return spend_date;
        }

        public int getSpend_point() {
            return spend_point;
        }
    }

    public static class Earn {
        private String earn_id, earn_date, earn_type;
        private int earn_point;

        public Earn() {
        }

        public Earn(String earn_id, String earn_date, String earn_type, int earn_point) {
            this.earn_id = earn_id;
            this.earn_date = earn_date;
            this.earn_type = earn_type;
            this.earn_point = earn_point;
        }

        public String getEarn_id() {
            return earn_id;
        }

        public String getEarn_date() {
            return earn_date;
        }

        public String getEarn_type() {
            return earn_type;
        }

        public int getEarn_point() {
            return earn_point;
        }
    }
}
