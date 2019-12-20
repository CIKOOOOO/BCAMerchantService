package com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest;

public interface IDetailPromoRequestPresenter {
    void loadPromoRequest(String MID, int mcc, String promo_request);

    void loadStatusPromoTypeRequest(String promo_status_id, String promo_type_id);
}
