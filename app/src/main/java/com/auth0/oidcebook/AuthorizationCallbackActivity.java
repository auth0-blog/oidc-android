package com.auth0.oidcebook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.squareup.picasso.Picasso;

import java.util.Set;

public class AuthorizationCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this;
        setContentView(R.layout.activity_authorization_callback);

        Intent intent = getIntent();

        Uri data = intent.getData();
        if (data == null) return;

        String authorizationCode = data.getQueryParameter("code");
        if (authorizationCode == null) return;

        RequestQueue requestQueue = OIDCUtils.fetchTokens(this, authorizationCode);
        requestQueue.addRequestFinishedListener(request -> {
            if (request.hasHadResponseDelivered()) {
                ImageView imageView = this.findViewById(R.id.user_profile_picture);
                Picasso.with(this).load(UserInfo.getInstance().getPicture()).into(imageView);

                TextView textView = this.findViewById(R.id.user_name);
                textView.setText(UserInfo.getInstance().getName());
            }
        });
    }
}
