package com.andrew.bcamerchantservice.model;

public class PromoRequest {


    public static class PromoType {
        private String promo_type_id, promo_name, promo_image;

        public PromoType() {
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

    public static class Facilities {
        private String facilities_id, facilities_name;

        public Facilities() {
        }

        public String getFacilities_id() {
            return facilities_id;
        }

        public String getFacilities_name() {
            return facilities_name;
        }
    }
}
