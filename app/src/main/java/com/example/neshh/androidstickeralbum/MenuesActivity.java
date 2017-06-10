package com.example.neshh.androidstickeralbum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by neshH on 05-Jun-17.
 */

public class MenuesActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button addStickerButton;
    private Button viewMyStickersButton;
    private Button editMyStickersButton;
    private Button tradableStickersButton;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    public static final int DETAIL_REQUEST = 1;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        addStickerButton = (Button) findViewById(R.id.addNewStickerButton);
        viewMyStickersButton = (Button) findViewById(R.id.myStickersButton);
        editMyStickersButton = (Button) findViewById(R.id.editStickersButton);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        tradableStickersButton = (Button) findViewById(R.id.tradableStickersButton);

        //if the user is not logged in
        //that means current user will return null
        if(firebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LogInActivity.class));
        }

        //getting current user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail.setText("Welcome "+user.getEmail());
        buttonLogout.setOnClickListener(this);

        addStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),AddStickerActivity.class);
                i.putExtra("Key for sending", "some data");
                startActivityForResult(i, DETAIL_REQUEST);
                //startActivity(i);
            }
        });

        viewMyStickersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (v.getContext(),ViewStickersActivity.class);
                startActivity(i);
            }
        });

        editMyStickersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (v.getContext(),EditStickersActivity.class);
                startActivity(i);
            }
        });

        tradableStickersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (v.getContext(),TradableStickersActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View view) {
        //if logout is pressed
        if(view == buttonLogout){
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LogInActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == DETAIL_REQUEST ) {
            if (data.hasExtra("returnKey")) {
                Integer stickerID = data.getExtras().getInt("returnKey");
                Toast.makeText(getApplicationContext(), stickerID.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }

}

