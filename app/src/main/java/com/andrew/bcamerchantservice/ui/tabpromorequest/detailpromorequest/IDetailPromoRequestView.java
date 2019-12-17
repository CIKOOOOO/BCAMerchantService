package com.andrew.bcamerchantservice.ui.tabpromorequest.detailpromorequest;

import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public interface IDetailPromoRequestView {
    void onLoadPromoData(PromoRequest promoRequest);

    void onLoadPromoType(PromoRequest.PromoType promoType, PromoRequest.PromoStatus promoStatus);

    void onLoadRestData(String special_facilities, List<PromoRequest.Facilities> facilitiesList
            , List<PromoRequest.Logo> logoList, List<PromoRequest.Product> productList);
}
