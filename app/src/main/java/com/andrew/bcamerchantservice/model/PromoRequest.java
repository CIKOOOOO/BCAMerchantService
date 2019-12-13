package com.andrew.bcamerchantservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoRequest implements Parcelable {

    private String promo_request_id, promo_title, promo_start_date, promo_end_date, promo_request_date, promo_type_id, promo_location, promo_tnc;

    public PromoRequest() {
    }

    protected PromoRequest(Parcel in) {
        promo_request_id = in.readString();
        promo_title = in.readString();
        promo_start_date = in.readString();
        promo_end_date = in.readString();
        promo_request_date = in.readString();
        promo_type_id = in.readString();
        promo_location = in.readString();
        promo_tnc = in.readString();
    }

    public static final Creator<PromoRequest> CREATOR = new Creator<PromoRequest>() {
        @Override
        public PromoRequest createFromParcel(Parcel in) {
            return new PromoRequest(in);
        }

        @Override
        public PromoRequest[] newArray(int size) {
            return new PromoRequest[size];
        }
    };

    public String getPromo_request_id() {
        return promo_request_id;
    }

    public void setPromo_request_id(String promo_request_id) {
        this.promo_request_id = promo_request_id;
    }

    public String getPromo_title() {
        return promo_title;
    }

    public void setPromo_title(String promo_title) {
        this.promo_title = promo_title;
    }

    public String getPromo_start_date() {
        return promo_start_date;
    }

    public void setPromo_start_date(String promo_start_date) {
        this.promo_start_date = promo_start_date;
    }

    public String getPromo_end_date() {
        return promo_end_date;
    }

    public void setPromo_end_date(String promo_end_date) {
        this.promo_end_date = promo_end_date;
    }

    public String getPromo_request_date() {
        return promo_request_date;
    }

    public void setPromo_request_date(String promo_request_date) {
        this.promo_request_date = promo_request_date;
    }

    public String getPromo_type_id() {
        return promo_type_id;
    }

    public void setPromo_type_id(String promo_type_id) {
        this.promo_type_id = promo_type_id;
    }

    public String getPromo_location() {
        return promo_location;
    }

    public void setPromo_location(String promo_location) {
        this.promo_location = promo_location;
    }

    public String getPromo_tnc() {
        return promo_tnc;
    }

    public void setPromo_tnc(String promo_tnc) {
        this.promo_tnc = promo_tnc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(promo_request_id);
        parcel.writeString(promo_title);
        parcel.writeString(promo_start_date);
        parcel.writeString(promo_end_date);
        parcel.writeString(promo_request_date);
        parcel.writeString(promo_type_id);
        parcel.writeString(promo_location);
        parcel.writeString(promo_tnc);
    }

    public static class PromoType {
        private String promo_type_id, promo_name, promo_image;

        public PromoType() {
        }

        public void setPromo_type_id(String promo_type_id) {
            this.promo_type_id = promo_type_id;
        }

        public String getPromo_type_id() {
            return promo_type_id;
        }

        public String getPromo_name() {
            return promo_name;
        }

        public String getPromo_image() {
            return promo_image;
        }
    }

    public static class Facilities implements Parcelable {
        private String facilities_id, facilities_name;
        private boolean isCheck;

        protected Facilities(Parcel in) {
            facilities_id = in.readString();
            facilities_name = in.readString();
            isCheck = in.readByte() != 0;
        }

        public static final Creator<Facilities> CREATOR = new Creator<Facilities>() {
            @Override
            public Facilities createFromParcel(Parcel in) {
                return new Facilities(in);
            }

            @Override
            public Facilities[] newArray(int size) {
                return new Facilities[size];
            }
        };

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public Facilities() {
        }

        public String getFacilities_id() {
            return facilities_id;
        }

        public String getFacilities_name() {
            return facilities_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(facilities_id);
            parcel.writeString(facilities_name);
            parcel.writeByte((byte) (isCheck ? 1 : 0));
        }
    }

    public static class Agreement {
        private String agreement;
        private boolean isCheck;

        public Agreement(String agreement, boolean isCheck) {
            this.agreement = agreement;
            this.isCheck = isCheck;
        }

        public Agreement() {
        }

        public String getAgreement() {
            return agreement;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }
}
