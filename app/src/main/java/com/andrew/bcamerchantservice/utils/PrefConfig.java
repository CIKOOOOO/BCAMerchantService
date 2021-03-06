package com.andrew.bcamerchantservice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.Merchant;


public class PrefConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
    }

    public void insertMerchantPosition(Merchant.Position position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_position_name), position.getPosition_name());
        editor.putString(context.getString(R.string.pref_position_id), position.getPosition_id());
        editor.apply();
    }

    public void insertMerchantData(Merchant merchant) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_mid), merchant.getMid());
        editor.putInt(context.getString(R.string.pref_point), merchant.getMerchant_point());
        editor.putInt(context.getString(R.string.pref_exp), merchant.getMerchant_exp());
        editor.putString(context.getString(R.string.pref_name), merchant.getMerchant_name());
        editor.putString(context.getString(R.string.pref_location), merchant.getMerchant_location());
        editor.putString(context.getString(R.string.pref_profile_picture), merchant.getMerchant_profile_picture());
        editor.putString(context.getString(R.string.pref_email), merchant.getMerchant_email());
        editor.putString(context.getString(R.string.pref_background_picture), merchant.getMerchant_background_picture());
        editor.putString(context.getString(R.string.pref_position), merchant.getMerchant_position());
        editor.putString(context.getString(R.string.pref_phone_number), merchant.getMerchant_phone_number());
        editor.putString(context.getString(R.string.pref_address), merchant.getMerchant_address());
        editor.putString(context.getString(R.string.pref_description), merchant.getMerchant_description());
        editor.putString(context.getString(R.string.pref_owner), merchant.getMerchant_owner_name());
        editor.putString(context.getString(R.string.pref_loyalty_id), merchant.getMerchant_loyalty_rank_id());
        editor.putBoolean(context.getString(R.string.pref_is_information_hide), merchant.isInformation_hide());
        editor.putInt(context.getString(R.string.pref_mcc), merchant.getMcc_id());
        editor.apply();
    }

    public void insertPreferencesID(int pref_id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_profile_picture), pref_id);
        editor.apply();
    }

    public void insertProfilePic(String profile_picture) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_profile_picture), profile_picture);
        editor.apply();
    }

    public void insertBackgroundPic(String background_picture) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_background_picture), background_picture);
        editor.apply();
    }

    public void insertId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_id), id);
        editor.apply();
    }

    public void insertPoint(int point) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getString(R.string.pref_point), point);
        editor.apply();
    }

    public void insertAddress(String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_address), address);
        editor.apply();
    }

    public void insertEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_email), email);
        editor.apply();
    }

    public void insertPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_phone_number), phoneNumber);
        editor.apply();
    }

    public void insertOwnerName(String ownerName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_owner), ownerName);
        editor.apply();
    }

    public void insertDescription(String desc) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.pref_description), desc);
        editor.apply();
    }

    public String getLoyaltyId() {
        return sharedPreferences.getString(context.getString(R.string.pref_loyalty_id), "-1");
    }

    public int getExp() {
        return sharedPreferences.getInt(context.getString(R.string.pref_exp), 0);
    }

    public String getProfilePicture() {
        return sharedPreferences.getString(context.getString(R.string.pref_profile_picture), "");
    }

    public String getEmail() {
        return sharedPreferences.getString(context.getString(R.string.pref_email), "");
    }

    public String getBackgroundPicture() {
        return sharedPreferences.getString(context.getString(R.string.pref_background_picture), "");
    }

    public int getPoint() {
        return sharedPreferences.getInt(context.getString(R.string.pref_point), 0);
    }

    public int getPrefID() {
        return sharedPreferences.getInt(context.getString(R.string.pref_point), 0);
    }

    public int getMCC() {
        return sharedPreferences.getInt(context.getString(R.string.pref_mcc), 0);
    }


    public String getPosition() {
        return sharedPreferences.getString(context.getString(R.string.pref_position), "");
    }

    public String getMID() {
        return sharedPreferences.getString(context.getString(R.string.pref_mid), "");
    }

    public String getName() {
        return sharedPreferences.getString(context.getString(R.string.pref_name), "");
    }

    public String getLocation() {
        return sharedPreferences.getString(context.getString(R.string.pref_location), "");
    }

    public String getOwnerName() {
        return sharedPreferences.getString(context.getString(R.string.pref_owner), "");
    }

    public String getStoreAddress() {
        return sharedPreferences.getString(context.getString(R.string.pref_address), "");
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString(context.getString(R.string.pref_phone_number), "");
    }

    public String getDescription() {
        return sharedPreferences.getString(context.getString(R.string.pref_description), "");
    }

    public Boolean isInformationHide() {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_is_information_hide), false);
    }

    public Merchant.Position getMerchantPosition() {
        return new Merchant.Position(sharedPreferences.getString(context.getString(R.string.pref_position_id), "")
                , sharedPreferences.getString(context.getString(R.string.pref_position_name), ""));
    }

    public Merchant getMerchantData() {
        return new Merchant(getMID(), getName(), getLocation(), getProfilePicture(), getEmail()
                , getBackgroundPicture(), getPosition(), getPhoneNumber(), getOwnerName()
                , getStoreAddress(), getDescription(), getLoyaltyId(), getPoint(), getExp(), getMCC(), isInformationHide());
    }

}

