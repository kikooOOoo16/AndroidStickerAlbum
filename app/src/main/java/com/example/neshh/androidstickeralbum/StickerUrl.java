package com.example.neshh.androidstickeralbum;

/**
 * Created by neshH on 06-Jun-17.
 */

public class StickerUrl {

    public String mStickerPlayerName;
    public String mStickerUrl;
    public String mStickerUrlid;

    public StickerUrl(){

    }

    public StickerUrl(String stickerUrl, String stickerUrlid, String stickerPlayerName) {

        this.mStickerUrl = stickerUrl;
        this.mStickerUrlid = stickerUrlid;
        this.mStickerPlayerName = stickerPlayerName;
    }

    public String getmStickerUrl() {
        return mStickerUrl;
    }

    public String getmStickerUrlid() {
        return mStickerUrlid;
    }

    public String getmStickerPlayerName() {
        return mStickerPlayerName;
    }
}
