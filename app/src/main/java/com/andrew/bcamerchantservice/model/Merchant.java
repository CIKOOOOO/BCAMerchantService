package com.andrew.bcamerchantservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchant implements Parcelable {

    private String mid, merchant_name, merchant_location, merchant_profile_picture, merchant_email, merchant_background_picture, merchant_position, merchant_phone_number, merchant_owner_name, merchant_address, merchant_description, merchant_loyalty_rank_id;
    private int merchant_point, merchant_exp;

    public Merchant(String mid, String merchant_name, String merchant_location, String merchant_profile_picture, String merchant_email, String merchant_background_picture, String merchant_position, String merchant_phone_number, String merchant_owner_name, String merchant_address, String merchant_description, String merchant_loyalty_rank_id, int merchant_point, int merchant_exp) {
        this.mid = mid;
        this.merchant_name = merchant_name;
        this.merchant_location = merchant_location;
        this.merchant_profile_picture = merchant_profile_picture;
        this.merchant_email = merchant_email;
        this.merchant_background_picture = merchant_background_picture;
        this.merchant_position = merchant_position;
        this.merchant_phone_number = merchant_phone_number;
        this.merchant_owner_name = merchant_owner_name;
        this.merchant_address = merchant_address;
        this.merchant_description = merchant_description;
        this.merchant_loyalty_rank_id = merchant_loyalty_rank_id;
        this.merchant_point = merchant_point;
        this.merchant_exp = merchant_exp;
    }

    public Merchant() {
    }

    protected Merchant(Parcel in) {
        mid = in.readString();
        merchant_name = in.readString();
        merchant_location = in.readString();
        merchant_profile_picture = in.readString();
        merchant_email = in.readString();
        merchant_background_picture = in.readString();
        merchant_position = in.readString();
        merchant_phone_number = in.readString();
        merchant_owner_name = in.readString();
        merchant_address = in.readString();
        merchant_description = in.readString();
        merchant_loyalty_rank_id = in.readString();
        merchant_point = in.readInt();
        merchant_exp = in.readInt();
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

    public String getMid() {
        return mid;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public String getMerchant_location() {
        return merchant_location;
    }

    public String getMerchant_profile_picture() {
        return merchant_profile_picture;
    }

    public String getMerchant_email() {
        return merchant_email;
    }

    public String getMerchant_background_picture() {
        return merchant_background_picture;
    }

    public String getMerchant_position() {
        return merchant_position;
    }

    public int getMerchant_point() {
        return merchant_point;
    }

    public int getMerchant_exp() {
        return merchant_exp;
    }

    public String getMerchant_phone_number() {
        return merchant_phone_number;
    }

    public String getMerchant_owner_name() {
        return merchant_owner_name;
    }

    public String getMerchant_address() {
        return merchant_address;
    }

    public String getMerchant_description() {
        return merchant_description;
    }

    public String getMerchant_loyalty_rank_id() {
        return merchant_loyalty_rank_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mid);
        parcel.writeString(merchant_name);
        parcel.writeString(merchant_location);
        parcel.writeString(merchant_profile_picture);
        parcel.writeString(merchant_email);
        parcel.writeString(merchant_background_picture);
        parcel.writeString(merchant_position);
        parcel.writeString(merchant_phone_number);
        parcel.writeString(merchant_owner_name);
        parcel.writeString(merchant_address);
        parcel.writeString(merchant_description);
        parcel.writeString(merchant_loyalty_rank_id);
        parcel.writeInt(merchant_point);
        parcel.writeInt(merchant_exp);
    }


    public static class MerchantStory {
        private String story_picture, sid, story_date, mid;

        public MerchantStory() {
        }

        public MerchantStory(String story_picture, String sid, String mid, String story_date) {
            this.story_picture = story_picture;
            this.sid = sid;
            this.mid = mid;
            this.story_date = story_date;
        }

        public String getStory_date() {
            return story_date;
        }

        public String getSid() {
            return sid;
        }

        public String getStory_picture() {
            return story_picture;
        }

        public String getMid() {
            return mid;
        }
    }

    public static class MerchantCatalog {
        private String cid, catalog_name, catalog_description, catalog_image, catalog_date;
        private int catalog_price;

        public MerchantCatalog(String cid, String catalog_name, String catalog_description, String catalog_image, String catalog_date, int catalog_price) {
            this.cid = cid;
            this.catalog_name = catalog_name;
            this.catalog_description = catalog_description;
            this.catalog_image = catalog_image;
            this.catalog_date = catalog_date;
            this.catalog_price = catalog_price;
        }

        public MerchantCatalog() {
        }

        public String getCid() {
            return cid;
        }

        public String getCatalog_name() {
            return catalog_name;
        }

        public String getCatalog_description() {
            return catalog_description;
        }

        public String getCatalog_image() {
            return catalog_image;
        }

        public int getCatalog_price() {
            return catalog_price;
        }

        public String getCatalog_date() {
            return catalog_date;
        }
    }

    public static class Income {
        private String income_date, income_id;
        private long income_amount;

        public Income() {
        }

        public String getIncome_date() {
            return income_date;
        }

        public String getIncome_id() {
            return income_id;
        }

        public long getIncome_amount() {
            return income_amount;
        }
    }

    public static class Mission {
        private String merchant_mission_id, mission_id, merchant_collect_mission_date;

        public Mission(String merchant_mission_id, String mission_id, String merchant_collect_mission_date) {
            this.merchant_mission_id = merchant_mission_id;
            this.mission_id = mission_id;
            this.merchant_collect_mission_date = merchant_collect_mission_date;
        }

        public Mission() {
        }

        public String getMerchant_mission_id() {
            return merchant_mission_id;
        }

        public String getMission_id() {
            return mission_id;
        }

        public String getMerchant_collect_mission_date() {
            return merchant_collect_mission_date;
        }
    }

    public static class Rewards implements Parcelable {
        private String merchant_rewards_id, rewards_id, merchant_rewards_code, merchant_rewards_date_collect, merchant_collect_mission_date, merchant_voucher_valid_date;
        private boolean rewards_is_used;

        public Rewards(String merchant_rewards_id, String rewards_id, String merchant_rewards_code, String merchant_rewards_date_collect, String merchant_collect_mission_date, String merchant_voucher_valid_date, boolean rewards_is_used) {
            this.merchant_rewards_id = merchant_rewards_id;
            this.rewards_id = rewards_id;
            this.merchant_rewards_code = merchant_rewards_code;
            this.merchant_rewards_date_collect = merchant_rewards_date_collect;
            this.merchant_collect_mission_date = merchant_collect_mission_date;
            this.merchant_voucher_valid_date = merchant_voucher_valid_date;
            this.rewards_is_used = rewards_is_used;
        }

        public Rewards() {
        }


        protected Rewards(Parcel in) {
            merchant_rewards_id = in.readString();
            rewards_id = in.readString();
            merchant_rewards_code = in.readString();
            merchant_rewards_date_collect = in.readString();
            merchant_collect_mission_date = in.readString();
            merchant_voucher_valid_date = in.readString();
            rewards_is_used = in.readByte() != 0;
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

        public void setMerchant_voucher_valid_date(String merchant_voucher_valid_date) {
            this.merchant_voucher_valid_date = merchant_voucher_valid_date;
        }

        public void setMerchant_rewards_date_collect(String merchant_rewards_date_collect) {
            this.merchant_rewards_date_collect = merchant_rewards_date_collect;
        }

        public void setRewards_is_used(boolean rewards_is_used) {
            this.rewards_is_used = rewards_is_used;
        }

        public String getMerchant_voucher_valid_date() {
            return merchant_voucher_valid_date;
        }

        public String getMerchant_rewards_id() {
            return merchant_rewards_id;
        }

        public String getRewards_id() {
            return rewards_id;
        }

        public String getMerchant_rewards_code() {
            return merchant_rewards_code;
        }

        public String getMerchant_rewards_date_collect() {
            return merchant_rewards_date_collect;
        }

        public String getMerchant_collect_mission_date() {
            return merchant_collect_mission_date;
        }

        public boolean isRewards_is_used() {
            return rewards_is_used;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(merchant_rewards_id);
            parcel.writeString(rewards_id);
            parcel.writeString(merchant_rewards_code);
            parcel.writeString(merchant_rewards_date_collect);
            parcel.writeString(merchant_collect_mission_date);
            parcel.writeString(merchant_voucher_valid_date);
            parcel.writeByte((byte) (rewards_is_used ? 1 : 0));
        }
    }
}
