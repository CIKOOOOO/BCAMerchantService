package com.andrew.bcamerchantservice.ui.tabpromorequest.promorequest.tncpromorequest;

import android.content.Context;
import android.net.Uri;

import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.model.PromoRequest;

import java.util.List;

public interface ITNCPromoRequestPresenter {
    void sendPromoRequest(String MID, PromoRequest promoRequest, List<PromoRequest.Facilities> facilitiesList,
                          String specific_payment, Uri attachment, Context mContext, List<ImagePicker> logoList,
                          List<ImagePicker> productList);
}
