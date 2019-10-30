package com.andrew.bcamerchantservice.ui.newthread.examplethread;

import android.graphics.Bitmap;

import com.andrew.bcamerchantservice.model.Forum;
import com.andrew.bcamerchantservice.model.ImagePicker;
import com.andrew.bcamerchantservice.utils.PrefConfig;

import java.util.List;

public interface IExampleThreadPresenter {
    void onSendNewThread(final Forum forum, final PrefConfig prefConfig, Bitmap thumbnail);

    void onSendNewThread(final Forum forum, final PrefConfig prefConfig, final List<ImagePicker> imageList, Bitmap thumbnail);
}
