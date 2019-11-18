package com.andrew.bcamerchantservice.utils;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final long DELAY = 1000;
    public static final long DELAY_THREAD = 1000;
    public static final long DURATION_STORY = 7000;
    public static final int PERMISSION_READ_GALLERY_EXTERNAL = 100;
    public static final int PERMISSION_WRITE_EXTERNAL = 101;
    public static final int PERMISSION_CAMERA_TAKER = 102;
    public static final int PERMISSION_READ_FILE_EXTERNAL = 103;
    public static final int ACTIVITY_CHOOSE_IMAGE = 104;
    public static final int ACTIVITY_CHOOSE_FILE = 105;
    public static final int ACTIVITY_TAKE_IMAGE = 106;

    public static final String DB_REFERENCE_TRANSACTION_REQUEST_PROMO = "transaction_request_promo";
    public static final String DB_REFERENCE_MERCHANT_PROFILE = "merchant_profile";
    public static final String DB_REFERENCE_MERCHANT_STORY = "merchant_story";
    public static final String DB_REFERENCE_MERCHANT_CATALOG = "merchant_catalog";
    public static final String DB_REFERENCE_FORUM = "forum";
    public static final String DB_REFERENCE_FORUM_REPLY = "forum_reply";
    public static final String DB_REFERENCE_FORUM_IMAGE = "forum_image";
    public static final String DB_REFERENCE_FORUM_IMAGE_REPLY = "forum_image_reply";
    public static final String DB_REFERENCE_FORUM_CATEGORY = "forum_category";
    public static final String DB_REFERENCE_FORUM_THUMBNAIL = "forum_thumbnail";
    public static final String DB_REFERENCE_FORUM_HIDDEN = "forum_hidden";
    public static final String DB_REFERENCE_FORUM_FAVORITE = "forum_favorite";
    public static final String DB_REFERENCE_FORUM_REPORT = "forum_report";
    public static final String DB_REFERENCE_FORUM_REPORT_REPLY = "forum_report_reply";
    public static final String DB_REFERENCE_FORUM_REPORTER = "forum_reporter";
    public static final String DB_REFERENCE_FORUM_REPORT_LIST = "forum_report_list";
    public static final String DB_REFERENCE_LOYALTY = "loyalty";
    public static final String DB_REFERENCE_LOYALTY_TYPE = "loyalty_rank_type";
    public static final String DB_REFERENCE_MISSION = "mission";
    public static final String SOLID_COLOR = "https://firebasestorage.googleapis.com/v0/b/bca-merchant-service-apps.appspot.com/o/forum_thumbnail%2Fwhite_palette.png?alt=media&token=4584f0df-4ff6-466e-9386-6850c051dc21";

    public static final int MAX_ALPHA = 220;

    // START PROFILE FRAGMENT

    private static final int[] icon = {R.drawable.ic_card_giftcard, R.drawable.ic_group_people
            , R.drawable.ic_people_setting, R.drawable.ic_store_add, R.drawable.ic_request_black
            , R.drawable.ic_forum_black, R.drawable.ic_help_center, R.drawable.ic_phone, R.drawable.ic_logout};

    private static final String[] parent = {"Merchant Loyalti", "Kelola Anggota", "Pengaturan Profile", "Tambah Cabang"
            , "Ajukan Promo", "Merchant Forum", "Pusat Bantuan", "Tentang Aplikasi", "Keluar"};

    private static final String[] child = {"Lihat loyalti Anda", "Lihat & atur anggota Anda", "Lihat & atur email dan password Anda"
            , "Ajukan penambahan cabang yang belum", "Ajukan promosi kerja sama Anda", "Diskusi dengan sesama merchant"
            , "Lihat solusi terbaik dan hubungi Kami", "Pastikan Anda menggunakan versi terbaru"
            , "Keluar akun"};

    //END PROFILE FRAGMENT

    // START ADS IMAGE TEMPLATE

    public static final int[] iconTemplateAds = {R.drawable.ic_promo_box1, R.drawable.ic_promo_box2, R.drawable.ic_promo_box3
            , R.color.orchid_palette, R.color.vivid_violet_palette
            , R.color.fruit_salad_palette, R.color.ripe_lemon_palette, R.color.selective_yellow_palette
            , R.color.west_coast_palette, R.color.cerulean_palette, R.color.tamarillo_palette, R.drawable.ic_rainbow};

    public static final int[] imgTemplateAds = {R.drawable.img_promo_box1, R.drawable.img_promo_box2, R.drawable.img_promo_box3
            , R.color.orchid_palette, R.color.vivid_violet_palette
            , R.color.fruit_salad_palette, R.color.ripe_lemon_palette, R.color.selective_yellow_palette
            , R.color.west_coast_palette, R.color.cerulean_palette, R.color.tamarillo_palette, R.drawable.ic_rainbow};

    // END ADS


    public static List<ProfileModel> getProfileModels() {
        List<ProfileModel> profileModels = new ArrayList<>();
        int i = 0;
        for (String parent : parent) {
            profileModels.add(new ProfileModel(parent, child[i], icon[i]));
            i++;
        }
        return profileModels;
    }
}
