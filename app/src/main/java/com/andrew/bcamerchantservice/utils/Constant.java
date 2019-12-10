package com.andrew.bcamerchantservice.utils;

import com.andrew.bcamerchantservice.R;
import com.andrew.bcamerchantservice.model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final long DELAY = 1000;
    public static final long DELAY_THREAD = 1000;
    public static final long DURATION_STORY = 5000;
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
    public static final String DB_REFERENCE_MERCHANT_MISSION = "merchant_mission";
    public static final String DB_REFERENCE_MERCHANT_INCOME = "merchant_income";
    public static final String DB_REFERENCE_MERCHANT_REWARDS = "merchant_rewards";
    public static final String DB_REFERENCE_MERCHANT_POSITION = "merchant_position";
    public static final String DB_REFERENCE_MERCHANT_FACILITIES = "merchant_facilities";
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
    public static final String DB_REFERENCE_POINT_HISTORY = "point_history";
    public static final String DB_REFERENCE_POINT_HISTORY_EARN = "earn";
    public static final String DB_REFERENCE_POINT_HISTORY_SPEND = "spend";
    public static final String DB_REFERENCE_REWARDS = "rewards";
    public static final String DB_REFERENCE_PROMO_REQUEST = "promo_request";
    public static final String DB_REFERENCE_PROMO_REQUEST_TYPE = "promo_type";
    public static final String DB_REFERENCE_FACILITIES = "facilities";
    public static final String SOLID_COLOR = "https://firebasestorage.googleapis.com/v0/b/bca-merchant-service-apps.appspot.com/o/forum_thumbnail%2Fwhite_palette.png?alt=media&token=4584f0df-4ff6-466e-9386-6850c051dc21";
    public static final String FULL_DATE_FORMAT = "EEE MMM dd hh:mm:ss 'GMT'Z yyyy";
    public static final String INFORMATION_PROMO_REQUEST = "Harus ada benefit yang diberikan oleh pihak merchant.##Benefit yang dimaksud bisa berupa penawaran spesial atau diskon.##Cost diskon di tanggung sepenuhnya oleh merchant.##Periode kerja sama minimal 6 bulan.##BCA akan memberikan kontraprestasi berupa media.##Proses pengajuan kerjasama kurang lebih 14 hari kerja setelah mendapat persetujuan dari management BCA.##Merchant sudah menggunakan EDC BCA.";

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
