package com.stuffexchange.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private UserIdentity userIdentity;
    private List<String> gifts;
    private List<String> wishList;
    private List<String> offers;

    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    public List<String> getGifts() {
        return gifts;
    }

    public List<String> getWishList() {
        return wishList;
    }

    public List<String> getOffers() {
        return offers;
    }

    public static User fromJsonString(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            User user = new User();
            JSONObject jsonId = json.getJSONObject("UserIdentity");
            user.userIdentity = UserIdentity.fromJson(jsonId);
            user.gifts = new ArrayList<>();
            if (json.has("Gifts")) {
                JSONArray gifts = json.getJSONArray("Gifts");
                for (int i=0; i < gifts.length(); i++) {
                    String giftId = (String) gifts.get(i);
                    user.gifts.add(i, giftId);
                }
            }
            user.wishList = new ArrayList<>();
            if (json.has("Wishlist")) {
                JSONArray wishList = json.getJSONArray("Wishlist");
                for (int i=0; i < wishList.length(); i++) {
                    String giftId = (String) wishList.get(i);
                    user.wishList.add(giftId);
                }
            }
            user.offers = new ArrayList<>();
            if (json.has("Offers")) {
                JSONArray offers = json.getJSONArray("Offers");
                for (int i=0; i < offers.length(); i++) {
                    String giftId = (String) offers.get(i);
                    user.offers.add(i, giftId);
                }
            }
            return user;
        } catch (JSONException ex) {
            Log.d("DataAccess", "Json exception " + ex.getMessage());
            return null;
        }

    }
}

