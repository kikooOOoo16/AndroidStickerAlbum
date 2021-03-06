package com.example.neshh.androidstickeralbum;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neshH on 05-Jun-17.
 */

public class EditStickersActivity extends AppCompatActivity {


    private List<Sticker> stickers;
    private DatabaseReference databaseStickers;
    private ListView stickerListEditView;
    private String dataEmail;
    private String userEmail;
    private String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/sticker-album-app.appspot.com/o/missing_player.png?alt=media&token=80aeac52-4a40-42e0-98c7-608a05148372";

    private FirebaseAuth firebaseAuth;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_stickers);
            stickerListEditView = (ListView) findViewById(R.id.stickerListEditView);
            databaseStickers = FirebaseDatabase.getInstance().getReference("stickers");
            stickers = new ArrayList<>();

            firebaseAuth = FirebaseAuth.getInstance();

            stickerListEditView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
            {
                @Override
                public boolean onItemLongClick (AdapterView< ? > adapterView, View view, int i, long l){
                    Sticker sticker = stickers.get(i);
                    showUpdateDeleteDialog(sticker.getmId(), sticker.getMdrzavaZaKojaIgra(), sticker.getMimeNaIgrac(), sticker.getMprezimeNaIgrac(), sticker.getMbrojNaSlice(), sticker.getMisTradable(), sticker.getmStickerImageUrl());
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

                    if (dataEmail.equals(userEmail)) {
                        //adding sticker to the list
                        stickers.add(sticker);
                    }
                    else {
                        continue;
                    }
                }

                //creating adapter
                StickerAdapter mStickerAdapter = new StickerAdapter(EditStickersActivity.this, stickers);
                //attaching adapter to the list view
                stickerListEditView.setAdapter(mStickerAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDeleteDialog(final String id, String newDrzavaZaKojaIgra, String newImeNaIgrac, String newPrezimeNaIgrac, String newBrojNaSlice, Boolean isTradable, final String imageDownloadUrl) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_stickers_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextSurname = (EditText) dialogView.findViewById(R.id.editTextSurname);
        final EditText editTextCountry = (EditText) dialogView.findViewById(R.id.editTextCountry);
        final EditText editTextStickerNumber = (EditText) dialogView.findViewById(R.id.editTextStickerNumber);
        final Spinner spinnerIsTradable = (Spinner) dialogView.findViewById(R.id.spinnerIsTradable);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateSticker);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteSticker);

        dialogBuilder.setTitle(newImeNaIgrac);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                String name = editTextName.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();
                String country = editTextCountry.getText().toString().trim();
                String stickerId = editTextStickerNumber.getText().toString().trim();
                String isTradable = spinnerIsTradable.getSelectedItem().toString();
                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(surname) || !TextUtils.isEmpty(country) || !TextUtils.isEmpty(stickerId) ) {
//                    Send notification
                    if(Boolean.valueOf(isTradable)) {
                        sendNotifications(name, surname, String.valueOf(user.getEmail()));
                    }
                    updateSticker(id, country, name, surname,  stickerId, Boolean.valueOf(isTradable), String.valueOf(user.getEmail()), imageDownloadUrl);
                    b.dismiss();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Сите полиња мора да бидат пополнети.", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                deleteSticker(id);
                b.dismiss();
            }
        });
    }

    private boolean updateSticker(String id, String newDrzavaZaKojaIgra, String newImeNaIgrac, String newPrezimeNaIgrac, String newBrojNaSlice, Boolean isTradable, String userEmail, String imageDownloadUrl) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //getting the specified sticker reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("stickers").child(id);
        //updating sticker
        Sticker sticker = new Sticker(id, newDrzavaZaKojaIgra, newImeNaIgrac, newPrezimeNaIgrac, newBrojNaSlice ,isTradable, String.valueOf(user.getEmail()), imageDownloadUrl);
        dR.setValue(sticker);
        Toast.makeText(getApplicationContext(), "Сличето е ажурирано", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteSticker(String id) {
        //getting the specified sticker reference
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("stickers").child(id);

        //removing sticker
        dR.removeValue();

        Toast.makeText(getApplicationContext(), "Сличето е избришано", Toast.LENGTH_LONG).show();

        return true;
    }


    RequestQueue queue;
    public void sendNotifications(String stickerName, String stickerSurname, String userMail) {
        try {
            queue = Volley.newRequestQueue(EditStickersActivity.this);
            JsonObject notificationData = new JsonObject();
            notificationData.addProperty("body", "Стикерот " + stickerName + " " + stickerSurname +" е додаден за размена од корисникот " + userMail);
            notificationData.addProperty("title", "TradableSticker");
            notificationData.addProperty("sound", "default");
            notificationData.addProperty("priority", "high");
            JsonObject params = new JsonObject();
            params.add ("notification", notificationData);
            params.addProperty("to", "/topics/stickerTrade");
            JsonObjectRequest req = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", new JSONObject(params.toString()),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Response", response.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ", error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap();
                    params.put("Content-Type", "application/json");
                    params.put("authorization", "key=" + "AAAAMi9GBPM:APA91bGnqv8FHU1_cYOvtDskM3S-ohf3hwxNP41pORqFUMoAHy_lJbS25Ne5UPDG9VorddKCYPr_W98kSLcKs88dwweAEgfuYR5o_CcYMeM_bg80vc3e7ImHxF1oJkzxp1g_PfiDRebq");
                    return params;
                }
            };
            queue.add(req);
        } catch (Exception e) {
            Log.d("Notification Error", e.getMessage());
        }
    }

}
