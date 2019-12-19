package com.andrew.bcamerchantservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoRequest implements Parcelable {

    private String promo_request_id, promo_title, promo_start_date, promo_end_date, promo_request_date, promo_type_id, promo_location, promo_tnc, promo_status, promo_correction_menu, promo_correction_reason;

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
        promo_status = in.readString();
        promo_correction_menu = in.readString();
        promo_correction_reason = in.readString();
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

    public String getPromo_correction_menu() {
        return promo_correction_menu;
    }

    public String getPromo_correction_reason() {
        return promo_correction_reason;
    }

    public void setPromo_status(String promo_status) {
        this.promo_status = promo_status;
    }

    public String getPromo_status() {
        return promo_status;
    }

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
        parcel.writeString(promo_status);
        parcel.writeString(promo_correction_menu);
        parcel.writeString(promo_correction_reason);
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
        private boolean check;

        protected Facilities(Parcel in) {
            facilities_id = in.readString();
            facilities_name = in.readString();
            check = in.readByte() != 0;
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
            return check;
        }

        public void setCheck(boolean check) {
            check = check;
        }

        public Facilities() {
        }

        public void setFacilities_id(String facilities_id) {
            this.facilities_id = facilities_id;
        }

        public void setFacilities_name(String facilities_name) {
            this.facilities_name = facilities_name;
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
            parcel.writeByte((byte) (check ? 1 : 0));
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

    public static class Logo implements Parcelable{
        private String merchant_logo_id, merchant_logo_url;

        public Logo() {
        }

        public Logo(String merchant_logo_id, String merchant_logo_url) {
            this.merchant_logo_id = merchant_logo_id;
            this.merchant_logo_url = merchant_logo_url;
        }

        protected Logo(Parcel in) {
            merchant_logo_id = in.readString();
            merchant_logo_url = in.readString();
        }

        public static final Creator<Logo> CREATOR = new Creator<Logo>() {
            @Override
            public Logo createFromParcel(Parcel in) {
                return new Logo(in);
            }

            @Override
            public Logo[] newArray(int size) {
                return new Logo[size];
            }
        };

        public String getMerchant_logo_id() {
            return merchant_logo_id;
        }

        public String getMerchant_logo_url() {
            return merchant_logo_url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(merchant_logo_id);
            parcel.writeString(merchant_logo_url);
        }
    }

    public static class Product implements Parcelable{
        private String merchant_product_id, merchant_product_url;

        public Product(String merchant_product_id, String merchant_product_url) {
            this.merchant_product_id = merchant_product_id;
            this.merchant_product_url = merchant_product_url;
        }

        public Product() {
        }

        protected Product(Parcel in) {
            merchant_product_id = in.readString();
            merchant_product_url = in.readString();
        }

        public static final Creator<Product> CREATOR = new Creator<Product>() {
            @Override
            public Product createFromParcel(Parcel in) {
                return new Product(in);
            }

            @Override
            public Product[] newArray(int size) {
                return new Product[size];
            }
        };

        public String getMerchant_product_id() {
            return merchant_product_id;
        }

        public String getMerchant_product_url() {
            return merchant_product_url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(merchant_product_id);
            parcel.writeString(merchant_product_url);
        }
    }

    public static class PromoStatus implements Parcelable {
        private String promo_status_id, promo_status_name;

        public PromoStatus() {
        }

        protected PromoStatus(Parcel in) {
            promo_status_id = in.readString();
            promo_status_name = in.readString();
        }

        public static final Creator<PromoStatus> CREATOR = new Creator<PromoStatus>() {
            @Override
            public PromoStatus createFromParcel(Parcel in) {
                return new PromoStatus(in);
            }

            @Override
            public PromoStatus[] newArray(int size) {
                return new PromoStatus[size];
            }
        };

        public String getPromo_status_id() {
            return promo_status_id;
        }

        public String getPromo_status_name() {
            return promo_status_name;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(promo_status_id);
            parcel.writeString(promo_status_name);
        }
    }
}
