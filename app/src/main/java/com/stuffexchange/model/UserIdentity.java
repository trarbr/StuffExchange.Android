package com.stuffexchange.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserIdentity {
    private String id;
    private String username;

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public static UserIdentity fromJson(JSONObject json) {
        try {
            UserIdentity userId = new UserIdentity();
            userId.id = json.getString("Id");
            userId.username = json.getString("Username");
            return userId;
        } catch (JSONException ex) {
            return null;
        }

    }
}
