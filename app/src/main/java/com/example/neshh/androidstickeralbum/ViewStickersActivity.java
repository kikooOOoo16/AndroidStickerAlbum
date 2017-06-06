package com.example.neshh.androidstickeralbum;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

/**
 * Created by neshH on 03-Jun-17.
 */

public class ViewStickersActivity extends AppCompatActivity {

    private List<Sticker> stickers;
    private List<StickerUrl> stickerImageUrls;
    private DatabaseReference databaseStickers;
    private DatabaseReference databaseStickerImageUrls;
    private ListView listViewStickers;
    private String dataEmail;
    private String userEmail;
    private String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/sticker-album-app.appspot.com/o/missing_player.png?alt=media&token=80aeac52-4a40-42e0-98c7-608a05148372";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_stickers);

        firebaseAuth = FirebaseAuth.getInstance();

        listViewStickers = (ListView) findViewById(R.id.stickerListView);
        databaseStickers = FirebaseDatabase.getInstance().getReference("stickers");
        databaseStickerImageUrls = FirebaseDatabase.getInstance().getReference("stickerUrls");
        stickers = new ArrayList<>();
        stickerImageUrls = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseStickerImageUrls.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                stickerImageUrls.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    StickerUrl stickerImageUrlss = postSnapshot.getValue(StickerUrl.class);
                    stickerImageUrls.add(stickerImageUrlss);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //attaching value event listener
        databaseStickers.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            //clearing the previous sticker list
            stickers.clear();
            //iterating through all the nodes
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                //getting sticker
                Sticker sticker = postSnapshot.getValue(Sticker.class);
                StickerUrl stickerImageUrlsObject = new StickerUrl();

                dataEmail = sticker.getmUserEmail();
                userEmail = user.getEmail();

                if (dataEmail.equals(userEmail) /*&& stickerImageUrlsObject.getmStickerPlayerName().equals(sticker.getMimeNaIgrac())*/) {
                    //adding sticker to the list
                    String url = stickerImageUrlsObject.getmStickerUrl();
                    sticker.setmStickerImageUrl(defaultImageUrl);
                    stickers.add(sticker);
                }
                else {
                    continue;
                }
            }
            //creating adapter
            StickerAdapter mStickerAdapter = new StickerAdapter(ViewStickersActivity.this, stickers);
            //attaching adapter to the list view
            listViewStickers.setAdapter(mStickerAdapter);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}