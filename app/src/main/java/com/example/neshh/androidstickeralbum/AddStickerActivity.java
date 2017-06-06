package com.example.neshh.androidstickeralbum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.R.attr.bitmap;

/**
 * Created by neshH on 02-Jun-17.
 */

public class AddStickerActivity extends AppCompatActivity {

    private List<Sticker> stickers;
    private Spinner nacionalnostSpinner = null;
    private Button takeStickerPicture = null;
    private Button addNewStickerButton = null;
    private EditText imeIgrac = null;
    private EditText prezimeIgrac = null;
    private EditText brojNaSlice = null;
    private ImageView stickerPicture = null;
    private Bitmap mSavedImage = null;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseStickers;
    private DatabaseReference databaseStickerImageUrls;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Integer stickerCount;
    private Integer count = 0;
    private String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/sticker-album-app.appspot.com/o/missing_player.png?alt=media&token=80aeac52-4a40-42e0-98c7-608a05148372";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stickers);

        nacionalnostSpinner = (Spinner) findViewById(R.id.spinnerDrzavi);
        imeIgrac = (EditText) findViewById(R.id.playerName);
        prezimeIgrac = (EditText) findViewById(R.id.playerSurname);
        brojNaSlice = (EditText) findViewById(R.id.stickerID);
        addNewStickerButton = (Button) findViewById(R.id.addStickerButton);
        takeStickerPicture = (Button) findViewById(R.id.takeStickerPicture);
        stickerPicture = (ImageView) findViewById(R.id.stickerImage);

        databaseStickerImageUrls = FirebaseDatabase.getInstance().getReference("stickerUrls");
        databaseStickers = FirebaseDatabase.getInstance().getReference("stickers");
        stickers = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        takeStickerPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        addNewStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent(v.getContext(), MainActivity.class);

                stickerCount = addNewSticker();

                returnIntent.putExtra("returnKey", stickerCount );
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView newImeIgrac = (TextView) findViewById(R.id.playerName);


         if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

             //get the camera image
             Bundle extras = data.getExtras();
             Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             byte[] databaos = baos.toByteArray();

             stickerPicture.setImageBitmap(imageBitmap);
             setmSavedImage(imageBitmap);

             mStorage = FirebaseStorage.getInstance().getReference();

             StorageReference imagesRef = mStorage.child(newImeIgrac.getText().toString().trim());

             mProgressDialog.setMessage("Uploading image ...");
             mProgressDialog.show();

             UploadTask uploadTask = imagesRef.putBytes(databaos);
             uploadTask.addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception exception) {
                     Toast.makeText(AddStickerActivity.this, "Sending failed", Toast.LENGTH_SHORT).show();
                 }
             }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //    stickerDownloadUrl = taskSnapshot.getDownloadUrl().toString();
                     mProgressDialog.dismiss();
                     @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                     String imeIgracText = imeIgrac.getText().toString().trim();
                     String id = databaseStickerImageUrls.push().getKey();
                     StickerUrl stickerUrl = new StickerUrl(id, downloadUrl.toString(), imeIgracText);
                     databaseStickerImageUrls.child(id).setValue(stickerUrl);
                 }
             } );
         }

    }

    //Adding new stickers to fireBase database
    private Integer addNewSticker() {

            String nacionalnostString = nacionalnostSpinner.getSelectedItem().toString();
            String imeIgracString = imeIgrac.getText().toString().trim();
            String prezimeIgracString = prezimeIgrac.getText().toString().trim();
            String brojNaSliceString = brojNaSlice.getText().toString().trim();
            Boolean isTradable = false;
            Bitmap savedStickerImage;

        FirebaseUser user = firebaseAuth.getCurrentUser();
        savedStickerImage = getmSavedImage();

            if (savedStickerImage != null || !TextUtils.isEmpty(imeIgracString) || !TextUtils.isEmpty(prezimeIgracString) || !TextUtils.isEmpty(brojNaSliceString) ) {

                String id = databaseStickers.push().getKey();
                Sticker newSticker = new Sticker(id, nacionalnostString, imeIgracString, prezimeIgracString, brojNaSliceString, isTradable, String.valueOf(user.getEmail()), defaultImageUrl);
                databaseStickers.child(id).setValue(newSticker);

                Toast.makeText(this,"Новото сличе е успешно додадено.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Сите полиња мора да бидат пополнети.", Toast.LENGTH_LONG).show();
            }
        return count;
    }

    private void dispatchTakePictureIntent() {

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
             startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//         }
    }

    public Bitmap getmSavedImage() {
        return mSavedImage;
    }

    public void setmSavedImage(Bitmap mSavedImage) {
        this.mSavedImage = mSavedImage;
    }
}

