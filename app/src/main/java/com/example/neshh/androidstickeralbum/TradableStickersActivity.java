package com.example.neshh.androidstickeralbum;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.neshh.androidstickeralbum.R.id.stickerListEditView;

/**
 * Created by neshH on 05-Jun-17.
 */

public class TradableStickersActivity extends AppCompatActivity {

    private List<Sticker> stickers;
    private DatabaseReference databaseStickers;
    private ListView tradableStickerListEditView;
    private Boolean checkIfTradable = null;
    private String dataEmail;
    private String userEmail;
    private String mailto;
    private String subject;
    private String message;

    private FirebaseAuth firebaseAuth;
//    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tradable_stickers);

        tradableStickerListEditView = (ListView) findViewById(R.id.tradableStickerListView);
        databaseStickers = FirebaseDatabase.getInstance().getReference("stickers");
        stickers = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        tradableStickerListEditView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick (AdapterView< ? > adapterView, View view, int i, long l){
                // send mail
                Sticker sticker = stickers.get(i);

                mailto = sticker.getmUserEmail().toString();
                subject = "Razmena na slicina";
                message = "Bi sakal razmena na sliceto : " + sticker.getMprezimeNaIgrac() + sticker.getMimeNaIgrac() + sticker.getMbrojNaSlice() + ". Ako ste zainteresiran/na ve molam iskontaktirajte me";

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{mailto});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);

                  //need this to prompts email client only  
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Choose an Email client :"));

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseStickers = FirebaseDatabase.getInstance().getReference("stickers");
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

                    dataEmail = sticker.getmUserEmail();
                    userEmail = user.getEmail();
                    checkIfTradable = sticker.misTradable;

                    if (dataEmail.equals(userEmail)) {
                        //adding sticker to the list
                        continue;
                    }
                    else if (checkIfTradable){

                        stickers.add(sticker);

                    }
                }

                //creating adapter
                TradableStickerAdapter mStickerAdapter = new TradableStickerAdapter(TradableStickersActivity.this, stickers);
                //attaching adapter to the list view
                tradableStickerListEditView.setAdapter(mStickerAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
