package com.example.neshh.androidstickeralbum;
import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;
/**
 * Created by neshH on 02-Jun-17.
 */

@IgnoreExtraProperties
public class Sticker {

    public String mUserEmail;
    public String mId;
    public String mdrzavaZaKojaIgra;
    public String mimeNaIgrac;
    public String mprezimeNaIgrac;
    public String mbrojNaSlice;
    public Boolean misTradable = false;
    public String mStickerImageUrl;


    public Sticker () {

    }

    public Sticker (String id, String newDrzavaZaKojaIgra, String newImeNaIgrac, String newPrezimeNaIgrac, String newBrojNaSlice, Boolean isTradable, String userEmail, String stickerImageUrl) {
        this.mdrzavaZaKojaIgra = newDrzavaZaKojaIgra;
        this.mimeNaIgrac = newImeNaIgrac;
        this.mprezimeNaIgrac = newPrezimeNaIgrac;
        this.mbrojNaSlice = newBrojNaSlice;
        this.mId = id;
        this.misTradable = isTradable;
        this.mUserEmail = userEmail;
        this.mStickerImageUrl = stickerImageUrl;
    }

    public void setmStickerImageUrl(String mStickerImageUrl) {
        this.mStickerImageUrl = mStickerImageUrl;
    }

    public String getmStickerImageUrl() {
        return mStickerImageUrl;
    }

    public Boolean getMisTradable() {
        return misTradable;
    }

    public String getmUserEmail() {
        return mUserEmail;
    }

    public String getMdrzavaZaKojaIgra() {
        return mdrzavaZaKojaIgra;
    }

    public String getMimeNaIgrac() {
        return mimeNaIgrac;
    }

    public String getMprezimeNaIgrac() {
        return mprezimeNaIgrac;
    }

    public String getMbrojNaSlice() {
        return mbrojNaSlice;
    }

    public String getmId() {
        return mId;
    }
}
