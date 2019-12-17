package com.andrew.bcamerchantservice.ui.tabpromorequest.promostatus;

import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public interface IPromoStatusView {

    void loadPromoType(PromoRequest.PromoType promoType, PromoRequest.PromoStatus promoStatus);

    void loadData(List<PromoRequest> promoRequests);

}
