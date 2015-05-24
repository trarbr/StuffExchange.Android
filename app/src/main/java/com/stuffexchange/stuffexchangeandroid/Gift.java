package com.stuffexchange.stuffexchangeandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Gift {
    public enum GiftState {
        Available(0), Offered(1), GivenAway(2);

        private int value;
        GiftState(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
    private String id;
    private User.UserIdentity user;
    private String title;
    private String description;
    private List<String> images;
    private List<Comment> comments;
    private List<String> wishers;
    private String offeredTo;
    private GiftState state;

    public String getTitle() {
        return title;
    }

    public String getCoverImage() {
        return images.get(0);
    }

    public String getDescription() {
        return description;
    }

    public Boolean hasImages() {
        if (images == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public static Gift fromJsonString(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            Gift gift = new Gift();
            gift.id = json.getString("Id");
            gift.title = json.getString("Title");
            gift.description = json.getString("Description");
            JSONArray images = json.getJSONArray("Images");
            if (images.length() > 0) {
                gift.images = new ArrayList<String>();
                for (int i=0; i < images.length(); i++) {
                    String imageId = (String) images.get(i);
                    gift.images.add(i, imageId);
                }
            } else {
                gift.images = null;
            }
            return gift;
        } catch (JSONException ex) {
            return null;
        }
    }
}

