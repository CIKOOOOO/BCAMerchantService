package com.andrew.bcamerchantservice.ui.profile;

import java.io.ByteArrayOutputStream;

public interface IProfilePresenter {

    void loadStory(String MID);

    void onUploadImage(final String MID, ByteArrayOutputStream baos);

    void onUploadImage(final String name, final String MID, final String child, byte[] byteData);
}
