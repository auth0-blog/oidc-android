package com.auth0.oidcebook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = this.findViewById(R.id.sign_in);
        signInButton.setOnClickListener(new SignInButtonListener());
    }
}
