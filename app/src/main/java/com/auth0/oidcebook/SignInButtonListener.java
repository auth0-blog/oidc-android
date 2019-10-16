package com.auth0.oidcebook;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.util.Base64;
import android.view.View;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SignInButtonListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        showAuthorizationPage(v.getContext());
    }

    private String createCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    private String createCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes, 0, bytes.length);
            byte[] digest = md.digest();
            return Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createAuthorizationURL() {
        String oidcProvider = "oidc-handbook.auth0.com";
        String audience = "https://to-dos.somedomain.com";
        String scope = "openid profile read:to-dos";
        String responseType = "code";
        String clientID = "4pdR2Lj6ZcqHPWtgztj4fCQZLjM7FgpT";
        String codeChallengeMethod = "S256";
        String packageName = "com.auth0.oidcebook";
        String redirectURI = "demo://" + oidcProvider + "/android/" + packageName + "/callback";

        String codeVerifier = createCodeVerifier();
        String codeChallenge = createCodeChallenge(codeVerifier);

        return "https://" + oidcProvider + "/authorize?" +
                "?audience=" + audience +
                "&scope=" + scope +
                "&response_type=" + responseType +
                "&client_id=" + clientID +
                "&code_challenge=" + codeChallenge +
                "&code_challenge_method=" + codeChallengeMethod +
                "&redirect_uri=" + redirectURI;
    }

    private void showAuthorizationPage(Context context) {
        String authorizationURL = createAuthorizationURL();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(authorizationURL));
    }
}
