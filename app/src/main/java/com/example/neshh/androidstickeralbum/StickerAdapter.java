package com.example.neshh.androidstickeralbum;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by neshH on 03-Jun-17.
 */

public class StickerAdapter extends ArrayAdapter<Sticker> {

    private Activity mContext;
    private List<Sticker> mStickers;


    public StickerAdapter (Activity context, List<Sticker> stickers) {
        super(context, R.layout.view_stickers, stickers);
        this.mContext = context;
        this.mStickers = stickers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.row, null, true);

        TextView prezimeIgracText = (TextView) listViewItem.findViewById(R.id.playerSurname);
        TextView imeIgracText = (TextView) listViewItem.findViewById(R.id.playerName);
        TextView brojSliceText = (TextView) listViewItem.findViewById(R.id.stickerNumber);
        TextView nacionalnostIgracText = (TextView) listViewItem.findViewById(R.id.playerCountry);
        ImageView stickerImageView = (ImageView) listViewItem.findViewById(R.id.stickerImage);

        Sticker sticker = mStickers.get(position);

        prezimeIgracText.setText(sticker.getMimeNaIgrac());
        imeIgracText.setText(sticker.getMprezimeNaIgrac());
        brojSliceText.setText(sticker.getMbrojNaSlice());
        nacionalnostIgracText.setText(sticker.getMdrzavaZaKojaIgra());
        Picasso.with(mContext).load(sticker.getmStickerImageUrl()).resize(130,130).into(stickerImageView);

        return listViewItem;
    }

}
