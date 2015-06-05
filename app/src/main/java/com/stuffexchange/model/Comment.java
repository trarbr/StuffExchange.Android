package com.stuffexchange.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String id;
    private UserIdentity user;
    private Date timestamp;
    private String content;

    public String getUsername() {
        return user.getUsername();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public static Comment fromJson(JSONObject json) {
        try {
            Comment comment = new Comment();
            comment.id = json.getString("Id");
            JSONObject user = json.getJSONObject("User");
            comment.user = UserIdentity.fromJson(user);
            String timestamp = json.getString("Timestamp");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                comment.timestamp = format.parse(timestamp);
            } catch (ParseException ex) {
                Log.d("DataAccess", ex.getMessage());
                comment.timestamp = new Date(0L);
            }
            comment.content = json.getString("Content");

            return comment;
        } catch (JSONException ex) {
            return null;
        }
    }
}
