package com.stuffexchange.app;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpResponse {
    private int responseCode;
    private String responseBody;

    public int getResponseCode() {
        return responseCode;
    }
    public JSONObject getResponseBodyAsJson() throws JSONException {
        return new JSONObject(responseBody);
    }

    public HttpResponse(int statusCode, String body) {
        // no args constructor
        responseCode = statusCode;
        responseBody = body;
    }

}
