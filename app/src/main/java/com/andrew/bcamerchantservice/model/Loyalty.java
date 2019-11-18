package com.andrew.bcamerchantservice.model;

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
}
