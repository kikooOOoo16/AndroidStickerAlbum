package com.example.neshh.androidstickeralbum;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import  com.example.neshh.androidstickeralbum.Notifications;

/**
 * Created by neshH on 05-Jun-17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Send any registration to your app's servers.
    }
}
