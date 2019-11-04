package com.andrew.bcamerchantservice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Forum implements Parcelable {
    private String fid, mid, forum_content, forum_date, forum_title, fcid, forum_thumbnail;
    private int forum_like, view_count;
    private boolean isLike;

    public Forum() {
    }

    public Forum(String fid, String mid, String forum_content, String forum_date, String forum_title
            , String fcid, String forum_thumbnail, int forum_like, int view_count, boolean isLike) {
        this.fid = fid;
        this.mid = mid;
        this.forum_content = forum_content;
        this.forum_date = forum_date;
        this.forum_title = forum_title;
        this.fcid = fcid;
        this.forum_thumbnail = forum_thumbnail;
        this.forum_like = forum_like;
        this.view_count = view_count;
        this.isLike = isLike;
    }

    protected Forum(Parcel in) {
        fid = in.readString();
        mid = in.readString();
        forum_content = in.readString();
        forum_date = in.readString();
        forum_title = in.readString();
        fcid = in.readString();
        forum_thumbnail = in.readString();
        forum_like = in.readInt();
        view_count = in.readInt();
        isLike = in.readByte() != 0;
    }

    public static final Creator<Forum> CREATOR = new Creator<Forum>() {
        @Override
        public Forum createFromParcel(Parcel in) {
            return new Forum(in);
        }

        @Override
        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };

    public String getForum_thumbnail() {
        return forum_thumbnail;
    }

    public void setForum_thumbnail(String forum_thumbnail) {
        this.forum_thumbnail = forum_thumbnail;
    }

    public String getFcid() {
        return fcid;
    }

    public void setForum_like(int forum_like) {
        this.forum_like = forum_like;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getMid() {
        return mid;
    }

    public String getFid() {
        return fid;
    }

    public String getForum_content() {
        return forum_content;
    }

    public String getForum_date() {
        return forum_date;
    }

    public String getForum_title() {
        return forum_title;
    }

    public int getForum_like() {
        return forum_like;
    }

    public int getView_count() {
        return view_count;
    }

    public boolean isLike() {
        return isLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fid);
        parcel.writeString(mid);
        parcel.writeString(forum_content);
        parcel.writeString(forum_date);
        parcel.writeString(forum_title);
        parcel.writeString(fcid);
        parcel.writeString(forum_thumbnail);
        parcel.writeInt(forum_like);
        parcel.writeInt(view_count);
        parcel.writeByte((byte) (isLike ? 1 : 0));
    }

    public static class ForumImage {
        private String fiid, image_name, image_url;

        public ForumImage(String fiid, String image_name, String image_url) {
            this.fiid = fiid;
            this.image_name = image_name;
            this.image_url = image_url;
        }

        public ForumImage() {
        }

        public String getFiid() {
            return fiid;
        }

        public String getImage_name() {
            return image_name;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    public static class ForumReply implements Parcelable {
        private String frid, mid, forum_content, forum_reply_date;
        private int forum_like_amount;
        private boolean like;

        protected ForumReply(Parcel in) {
            frid = in.readString();
            mid = in.readString();
            forum_content = in.readString();
            forum_reply_date = in.readString();
            forum_like_amount = in.readInt();
            like = in.readByte() != 0;
        }

        public static final Creator<ForumReply> CREATOR = new Creator<ForumReply>() {
            @Override
            public ForumReply createFromParcel(Parcel in) {
                return new ForumReply(in);
            }

            @Override
            public ForumReply[] newArray(int size) {
                return new ForumReply[size];
            }
        };

        public void setForum_like_amount(int forum_like_amount) {
            this.forum_like_amount = forum_like_amount;
        }

        public void setLike(boolean like) {
            this.like = like;
        }

        public ForumReply(String frid, String mid, String forum_content, String forum_reply_date, int forum_like_amount, boolean like) {
            this.frid = frid;
            this.mid = mid;
            this.forum_content = forum_content;
            this.forum_reply_date = forum_reply_date;
            this.forum_like_amount = forum_like_amount;
            this.like = like;
        }

        public ForumReply() {
        }

        public String getFrid() {
            return frid;
        }

        public String getMid() {
            return mid;
        }

        public String getForum_content() {
            return forum_content;
        }

        public String getForum_reply_date() {
            return forum_reply_date;
        }

        public int getForum_like_amount() {
            return forum_like_amount;
        }

        public boolean isLike() {
            return like;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(frid);
            parcel.writeString(mid);
            parcel.writeString(forum_content);
            parcel.writeString(forum_reply_date);
            parcel.writeInt(forum_like_amount);
            parcel.writeByte((byte) (like ? 1 : 0));
        }
    }

    public static class ForumImageReply {
        private String frid, image_name, image_url;

        public ForumImageReply(String frid, String image_name, String image_url) {
            this.frid = frid;
            this.image_name = image_name;
            this.image_url = image_url;
        }

        public ForumImageReply() {
        }

        public String getFrid() {
            return frid;
        }

        public String getImage_name() {
            return image_name;
        }

        public String getImage_url() {
            return image_url;
        }
    }

    public static class ForumLikeBy {
        private String forum_like_mid, forum_like_time;

        public ForumLikeBy() {
        }

        public ForumLikeBy(String forum_like_mid, String forum_like_time) {
            this.forum_like_mid = forum_like_mid;
            this.forum_like_time = forum_like_time;
        }

        public String getForum_like_mid() {
            return forum_like_mid;
        }

        public String getForum_like_time() {
            return forum_like_time;
        }
    }

    public static class ForumCategory implements Parcelable {
        private String fcid, category_name, category_url;

        public ForumCategory() {
        }

        public ForumCategory(String fcid, String category_name, String category_url) {
            this.fcid = fcid;
            this.category_name = category_name;
            this.category_url = category_url;
        }

        protected ForumCategory(Parcel in) {
            fcid = in.readString();
            category_name = in.readString();
            category_url = in.readString();
        }

        public static final Creator<ForumCategory> CREATOR = new Creator<ForumCategory>() {
            @Override
            public ForumCategory createFromParcel(Parcel in) {
                return new ForumCategory(in);
            }

            @Override
            public ForumCategory[] newArray(int size) {
                return new ForumCategory[size];
            }
        };

        public String getFcid() {
            return fcid;
        }

        public String getCategory_name() {
            return category_name;
        }

        public String getCategory_url() {
            return category_url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(fcid);
            parcel.writeString(category_name);
            parcel.writeString(category_url);
        }
    }

    public static class ForumHidden {
        private String fhid, fid, time;

        public ForumHidden() {
        }

        public ForumHidden(String fhid, String fid, String time) {
            this.fhid = fhid;
            this.fid = fid;
            this.time = time;
        }

        public String getFhid() {
            return fhid;
        }

        public String getFid() {
            return fid;
        }

        public String getTime() {
            return time;
        }
    }

    public static class ForumFavorite {
        private String FID, date;

        public ForumFavorite() {
        }

        public ForumFavorite(String FID, String date) {
            this.FID = FID;
            this.date = date;
        }

        public String getFID() {
            return FID;
        }

        public String getDate() {
            return date;
        }
    }
}
