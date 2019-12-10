package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest;

import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public interface IPromoRequestView {
    void onLoadPromoType(List<PromoRequest.PromoType> promoTypes);
}
