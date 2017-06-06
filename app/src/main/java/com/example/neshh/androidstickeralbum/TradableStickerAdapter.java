package com.example.neshh.androidstickeralbum;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by neshH on 05-Jun-17.
 */

public class TradableStickerAdapter extends ArrayAdapter<Sticker> {

    private Activity mContext;
    private List<Sticker> mStickers;


    public TradableStickerAdapter (Activity context, List<Sticker> stickers) {
        super(context, R.layout.tradable_stickers, stickers);
        this.mContext = context;
        this.mStickers = stickers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.tradable_row, null, true);

        TextView prezimeIgracText = (TextView) listViewItem.findViewById(R.id.tradablePlayerSurname);
        TextView imeIgracText = (TextView) listViewItem.findViewById(R.id.tradablePlayerName);
        TextView brojSliceText = (TextView) listViewItem.findViewById(R.id.tradableStickerNumber);
        TextView nacionalnostIgracText = (TextView) listViewItem.findViewById(R.id.tradablePlayerCountry);
        TextView emailUser = (TextView) listViewItem.findViewById(R.id.tradableEmailOwner);
        ImageView tradableStickerImage = (ImageView) listViewItem.findViewById(R.id.tradableStickerImage);
        Sticker sticker = mStickers.get(position);

        prezimeIgracText.setText(sticker.getMimeNaIgrac());
        imeIgracText.setText(sticker.getMprezimeNaIgrac());
        brojSliceText.setText(sticker.getMbrojNaSlice());
        nacionalnostIgracText.setText(sticker.getMdrzavaZaKojaIgra());
        emailUser.setText("Сопственик : " + sticker.getmUserEmail());
        Picasso.with(mContext).load(sticker.getmStickerImageUrl()).resize(130,130).into(tradableStickerImage);

        return listViewItem;
    }

}
