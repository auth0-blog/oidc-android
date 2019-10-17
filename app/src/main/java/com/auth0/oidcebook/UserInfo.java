package com.auth0.oidcebook;

public class UserInfo {
    private static final UserInfo SINGLETON = new UserInfo();

    public static UserInfo getInstance() {
        return SINGLETON;
    }

    private String name;
    private String picture;
    private String accessToken;

    public void setUserInfo(String name, String picture, String accessToken) {
        this.name = name;
        this.picture = picture;
        this.accessToken = accessToken;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
