package com.auth0.oidcebook;

import android.view.View;

public class SignInButtonListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        OIDCUtils.showAuthorizationPage(v.getContext());
    }
}
