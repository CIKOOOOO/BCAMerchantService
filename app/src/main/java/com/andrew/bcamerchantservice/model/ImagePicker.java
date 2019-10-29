package com.andrew.bcamerchantservice.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ImagePicker implements Parcelable {
    private Bitmap image_bitmap;
    private String title,type;
    private int img_static;

    public ImagePicker(int img_static, String type) {
        this.img_static = img_static;
        this.type = type;
    }

    protected ImagePicker(Parcel in) {
        image_bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        title = in.readString();
        type = in.readString();
        img_static = in.readInt();
    }

    public static final Creator<ImagePicker> CREATOR = new Creator<ImagePicker>() {
        @Override
        public ImagePicker createFromParcel(Parcel in) {
            return new ImagePicker(in);
        }

        @Override
        public ImagePicker[] newArray(int size) {
            return new ImagePicker[size];
        }
    };

    public int getImg_static() {
        return img_static;
    }

    public ImagePicker(Bitmap image_bitmap, String type, String title) {
        this.image_bitmap = image_bitmap;
        this.title = title;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage_bitmap() {
        return image_bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(image_bitmap, i);
        parcel.writeString(title);
        parcel.writeString(type);
        parcel.writeInt(img_static);
    }
}
