package com.andrew.bcamerchantservice.ui.tabpromorequest.activepromo;

import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public interface IActivePromoView {
    void loadPromoType(PromoRequest.PromoType promoType, PromoRequest.PromoStatus promoStatus);

    void loadData(List<PromoRequest> promoRequests);
}
