package com.auth0.oidcebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class OIDCUtils {
    private static String createCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }

    private static String createCodeChallenge(String codeVerifier) {
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

    private static String createAuthorizationURL(final String codeChallenge) {
        String oidcProvider = "oidc-handbook.auth0.com";
        String audience = "https://to-dos.somedomain.com";
        String scope = "openid%20profile%20read:to-dos%20create:to-dos";
        String responseType = "code";
        String clientID = "4pdR2Lj6ZcqHPWtgztj4fCQZLjM7FgpT";
        String codeChallengeMethod = "S256";
        String packageName = "com.auth0.oidcebook";
        String redirectURI = "oidc-sample://" + oidcProvider + "/android/" + packageName + "/callback";

        return "https://" + oidcProvider + "/authorize?" +
                "?audience=" + audience +
                "&scope=" + scope +
                "&response_type=" + responseType +
                "&client_id=" + clientID +
                "&code_challenge=" + codeChallenge +
                "&code_challenge_method=" + codeChallengeMethod +
                "&redirect_uri=" + redirectURI;
    }

    private static void storeCodeVerifier(final Context context, final String codeVerifier) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE
        );
        sharedPreferences.edit()
                .putString(context.getString(R.string.code_verifier), codeVerifier)
                .apply();
    }

    private static String readCodeVerifier(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_name),
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(context.getString(R.string.code_verifier), null);
    }

    private static String verifyIDToken(String idToken) {
        try {
            DecodedJWT jwt = JWT.decode(idToken);
            return jwt.getPayload();
        } catch (JWTDecodeException e){
            throw new RuntimeException(e);
        }
    }

    private static JSONObject parseJson(final String jsonObject) {
        try {
            return new JSONObject(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static DecodedJWT decodeIDToken(final JSONObject parsedResponse) {
        try {
            String idToken = parsedResponse.getString("id_token");
            return JWT.decode(idToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void showAuthorizationPage(final Context context) {
        String codeVerifier = createCodeVerifier();
        String codeChallenge = createCodeChallenge(codeVerifier);

        // store the code verifier for later
        storeCodeVerifier(context, codeVerifier);

        String authorizationURL = createAuthorizationURL(codeChallenge);

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(authorizationURL));
    }

    public static void fetchTokens(final Context context, final String authorizationCode) {
        final String oidcProvider = "oidc-handbook.auth0.com";
        final String clientID = "4pdR2Lj6ZcqHPWtgztj4fCQZLjM7FgpT";
        final String packageName = "com.auth0.oidcebook";
        final String redirectURI = "oidc-sample://" + oidcProvider + "/android/" + packageName + "/callback";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://" + oidcProvider + "/oauth/token";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject parsedResponse = parseJson(response);
                        DecodedJWT jwt = decodeIDToken(parsedResponse);
                        String welcome = "Hello, " + jwt.getClaim("name").asString() + "! Glad to see you here.";
                        Toast.makeText(context, welcome, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "authorization_code");
                params.put("client_id", clientID);
                params.put("code_verifier", readCodeVerifier(context));
                params.put("code", authorizationCode);
                params.put("redirect_uri", redirectURI);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
