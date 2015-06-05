package com.stuffexchange.model;

import android.util.Log;

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
    private UserIdentity user;
    private String title;
    private String description;
    private List<String> images;
    private List<Comment> comments;
    private List<UserIdentity> wishers;
    private UserIdentity offeredTo;
    private GiftState state;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImage() {
        return images.get(0);
    }

    public List<String> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return state.name();
    }

    public UserIdentity getOfferedTo() {
        return offeredTo;
    }

    public UserIdentity getUser() {
        return user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Boolean isOffered() {
        return offeredTo != null;
    }

    public List<UserIdentity> getWishers() {
        return wishers;
    }

    public GiftState getGiftState() {
        return state;
    }

    public Boolean hasWishers() {
        return (wishers != null && wishers.size() > 0);
    }

    public Boolean hasComments() {
        return (comments != null && comments.size() > 0);
    }

    public Boolean hasImages() {
        if (images != null && images.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static Gift fromJsonString(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            Gift gift = new Gift();
            gift.id = json.getString("Id");
            gift.title = json.getString("Title");
            gift.description = json.getString("Description");
            JSONObject user = json.getJSONObject("User");
            gift.user = UserIdentity.fromJson(user);
            gift.images = new ArrayList<>();
            if (json.has("Images")) {
                JSONArray images = json.getJSONArray("Images");
                for (int i=0; i < images.length(); i++) {
                    String imageId = (String) images.get(i);
                    gift.images.add(i, imageId);
                }
            }
            gift.comments = new ArrayList<>();
            if (json.has("Comments")) {
                JSONArray comments = json.getJSONArray("Comments");
                for (int i=0; i < comments.length(); i++) {
                    JSONObject commentJson = (JSONObject) comments.get(i);
                    Comment comment = Comment.fromJson(commentJson);
                    gift.comments.add(comment);
                }
            }
            gift.wishers = new ArrayList<>();
            if (json.has("Wishers")) {
                JSONArray wishers = json.getJSONArray("Wishers");
                for (int i=0; i < wishers.length(); i++) {
                    JSONObject wisherJson = (JSONObject) wishers.get(i);
                    UserIdentity wisher = UserIdentity.fromJson(wisherJson);
                    gift.wishers.add(wisher);
                }
            }
            String offeredTo = json.getString("OfferedTo");
            if (offeredTo.equals("null")) {
                gift.offeredTo = null;
            } else {
                JSONObject some = new JSONObject(offeredTo);
                JSONArray fields = some.getJSONArray("Fields");
                JSONObject offeredToJson = fields.getJSONObject(0);
                gift.offeredTo = UserIdentity.fromJson(offeredToJson);
            }
            int state = json.getInt("State");
            gift.state = GiftState.values()[state];

            return gift;
        } catch (JSONException ex) {
            Log.d("DataAccess", ex.getMessage());
            return null;
        }
    }
}

