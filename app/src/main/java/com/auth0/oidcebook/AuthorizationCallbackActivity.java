package com.auth0.oidcebook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Set;

public class AuthorizationCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_callback);

        Intent intent = getIntent();
        Uri data = intent.getData();
        OIDCUtils.fetchTokens(this, data.getQueryParameter("code"));
    }
}
