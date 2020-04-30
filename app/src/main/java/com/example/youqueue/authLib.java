package com.example.youqueue;

import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.squareup.okhttp.OkHttpClient;
//import com.spotify.sdk.android.authentication.sample.R;

//import okhttp3.OkHttpClient;


public class authLib extends AppCompatActivity {

    public static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    public static final String REDIRECT_URI = "com.youqueue://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;

    private static final int REQUEST_CODE = 1337;

    AuthorizationRequest request =
            new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                    .setShowDialog(false)
                    .setScopes(new String[]{"user-read-private"})
                    .setCampaign("your-campaign-token")
                    .build();


    //AuthorizationClient client = new AuthorizationClient();
    //client.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE , request);

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    mAccessToken = response.getAccessToken();
                    System.out.println("Access token: " + mAccessToken);
                    break;

                // Auth flow returned an error
                case ERROR:
                    System.out.println("Error encountered in Auth");
                    mAccessToken = response.getError();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
