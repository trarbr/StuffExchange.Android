package com.stuffexchange.stuffexchangeandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAccess {
    private final String LOGTAG = "DataAccess";
    private final String BASE_URL = "http://10.0.2.2:3579/";
    private Map<String, Bitmap> images;

    public DataAccess() {
        images = new HashMap<>();
    }

    public void AddImage(OnTaskCompleted caller, String giftId, Uri imageUri, String token) {
        new AsyncAddImage(caller, giftId, imageUri, token).execute();
    }

    public void AddGift(OnTaskCompleted caller, String title, String description, String token) {
        new AsyncAddGift(caller, title, description, token).execute();
    }

    public void MakeOffer(OnTaskCompleted caller, String giftId, String userId, String token) {
        new AsyncMakeOffer(caller, giftId, userId, token).execute();
    }

    public void MakeWish(OnTaskCompleted caller, String giftId, String token) {
        new AsyncMakeWish(caller, giftId, token).execute();

    }

    public void UnmakeWish(OnTaskCompleted caller, String giftId, String token) {
        new AsyncUnmakeWish(caller, giftId, token).execute();
    }

    public void AcceptOffer(OnTaskCompleted caller, String giftId, String token) {
        new AsyncAcceptOffer(caller, giftId, token).execute();
    }

    public void GetUser(OnTaskCompleted caller, String userId, String token) {
        new AsyncGetUser(caller, userId, token).execute();

    }
    public void GetGiftIds(OnTaskCompleted caller) {
        new AsyncGetGiftIds(caller).execute();
    }
    public void GetGift(OnTaskCompleted caller, String giftId) {
        new AsyncGetGift(caller, giftId).execute();
    }
    public void GetImage(OnTaskCompleted caller, String imageId) {
        String uri = BASE_URL + "images/" + imageId + ".jpg";
        if (!images.containsKey(uri)) {
            new AsyncGetImage(caller, imageId).execute();
        }
        else {
            caller.onTaskCompleted(images.get(uri));
        }
    }

    private String get(String uri, Map<String, String> headers) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            // read responsebody into a string
            String responseBody = null;
            if (responseCode == 200) {
                StringBuilder sb = new StringBuilder();
                InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                responseBody = sb.toString();
            }
            return responseBody;
        } catch (Exception ex) {
            Log.d(LOGTAG, ex.getMessage());
            return null;
        }
    }
    private String get(String uri) {
        return get(uri, null);
    }

    private String put(String uri, Map<String, String> headers, Map<String, String> requestBody) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            if (requestBody != null) {
                conn.setDoOutput(true);
                JSONObject jsonBody = new JSONObject(requestBody);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            // read responsebody into a string
            Log.d(LOGTAG, "PUT response code: " + responseCode);
            String responseBody = null;
            if (responseCode == 200) {
                StringBuilder sb = new StringBuilder();
                InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                responseBody = sb.toString();
            }
            return responseBody;
        } catch (Exception ex) {
            Log.d(LOGTAG, "got exception " + ex.toString());
            return null;
        }
    }

    private String post(String uri, Map<String, String> headers, Map<String, String> requestBody) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            if (requestBody != null) {
                conn.setDoOutput(true);
                JSONObject jsonBody = new JSONObject(requestBody);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            // read responsebody into a string
            Log.d(LOGTAG, "POST response code: " + responseCode);
            String responseBody = null;
            if (responseCode == 200) {
                StringBuilder sb = new StringBuilder();
                InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                responseBody = sb.toString();
            }
            return responseBody;
        } catch (Exception ex) {
            Log.d(LOGTAG, "got exception " + ex.getMessage());
            return null;
        }
    }

    private class AsyncAddImage extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> headers;
        private Uri imageUri;

        public AsyncAddImage(OnTaskCompleted caller, String giftId, Uri imageUri, String token) {
            this.caller = caller;
            this.imageUri = imageUri;
            uri = BASE_URL + "gifts/" + giftId;
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
        }

        @Override
        protected String doInBackground(Void... params) {
            String boundary = "*****";
            String twoHyphens = "--";
            String crlf = "\r\n";

            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append(twoHyphens).append(boundary).append(crlf);
            bodyBuilder.append("Content-Disposition: file; name=\"image\"; filename=\"image.jpg\"").append(crlf);
            bodyBuilder.append("Content-Type: image/jpeg").append(crlf);
            bodyBuilder.append(crlf);

            StringBuilder end = new StringBuilder();
            end.append(twoHyphens).append(boundary).append(twoHyphens).append(crlf);

            int maxBufferSize = 8192;
            int bytesRead;
            byte[] imageBuffer = new byte[maxBufferSize];

            try {
                File imageFile = new File(imageUri.getPath());
                FileInputStream imageStream = new FileInputStream(imageFile);

                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + boundary);
                if (headers != null) {
                    for (Map.Entry<String, String> header : headers.entrySet()) {
                        conn.setRequestProperty(header.getKey(), header.getValue());
                    }
                }
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoOutput(true);

                DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
                writer.writeBytes(bodyBuilder.toString());
                while ((bytesRead = imageStream.read(imageBuffer, 0, maxBufferSize)) > 0) {
                    writer.write(imageBuffer, 0, bytesRead);
                }
                writer.writeBytes(end.toString());
                writer.flush();

                int responseCode = conn.getResponseCode();
                Log.d(LOGTAG, "AddImage response code: " + responseCode);
                String responseBody = null;
                if (responseCode == 200) {
                    StringBuilder sb = new StringBuilder();
                    InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    responseBody = sb.toString();
                    Log.d(LOGTAG, responseBody);
                }
                return responseBody;
            } catch (Exception ex) {
                Log.d(LOGTAG, ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                caller.onTaskCompleted(false);
            } else {
                caller.onTaskCompleted(true);
            }
        }
    }

    private class AsyncAddGift extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> headers;
        private Map<String, String> body;

        public AsyncAddGift(OnTaskCompleted caller, String title, String description, String token) {
            this.caller = caller;
            this.uri = BASE_URL + "gifts/";
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
            headers.put("Content-Type", "application/vnd.stuffexchange.AddGift+json");
            body = new HashMap<>();
            body.put("Title", title);
            body.put("Description", description);
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.post(uri, headers, body);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "AddGift response is null");
                caller.onTaskCompleted(null);
            } else {
                String giftId = null;
                try {
                    JSONObject result = new JSONObject(responseBody);
                    JSONArray fields = result.getJSONArray("Fields");
                    JSONObject gift = fields.getJSONObject(0);
                    giftId = gift.getString("Id");
                } catch (JSONException ex) {
                    Log.d(LOGTAG, ex.getMessage());
                }
                caller.onTaskCompleted(giftId);
            }
        }
    }

    private class AsyncAcceptOffer extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> headers;

        public AsyncAcceptOffer(OnTaskCompleted caller, String giftId, String token) {
            this.caller = caller;
            this.uri = BASE_URL + "gifts/"  + giftId;
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
            headers.put("Content-Type", "application/vnd.stuffexchange.AcceptOffer+json");
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.put(uri, headers, null);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "AcceptOffer response is null");
                caller.onTaskCompleted(false);
            } else {
                caller.onTaskCompleted(true);
            }
        }
    }

    private class AsyncUnmakeWish extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> headers;

        public AsyncUnmakeWish(OnTaskCompleted caller, String giftId, String token) {
            this.caller = caller;
            this.uri = BASE_URL + "gifts/" + giftId;
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
            headers.put("Content-Type", "application/vnd.stuffexchange.UnmakeWish+json");
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.put(uri, headers, null);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "UnmakeWish response is null");
                caller.onTaskCompleted(false);
            } else {
                caller.onTaskCompleted(true);
            }
        }
    }
    private class AsyncMakeWish extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> headers;

        public AsyncMakeWish(OnTaskCompleted caller, String giftId, String token) {
            this.caller = caller;
            this.uri = BASE_URL + "gifts/" + giftId;
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
            headers.put("Content-Type", "application/vnd.stuffexchange.MakeWish+json");
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.put(uri, headers, null);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "MakeWish response is null");
                caller.onTaskCompleted(false);
            } else {
                caller.onTaskCompleted(true);
            }
        }
    }

    private class AsyncMakeOffer extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted caller;
        private String uri;
        private Map<String, String> body;
        private Map<String, String> headers;

        public AsyncMakeOffer(OnTaskCompleted caller, String giftId, String userId, String token) {
            this.caller = caller;
            this.uri = BASE_URL + "gifts/" + giftId;
            headers = new HashMap<>();
            String authHeader =  "Token " + token;
            headers.put("Authorization", authHeader);
            headers.put("Content-Type", "application/vnd.stuffexchange.MakeOffer+json");
            // put userId in body
            body = new HashMap<>();
            body.put("User", userId);
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.put(uri, headers, body);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "MakeOffer response is null");
                caller.onTaskCompleted(false);
            } else {
                caller.onTaskCompleted(true);
            }
        }
    }

    private class AsyncGetUser extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted mCaller;
        private String authHeader;
        private String mUri;

        public AsyncGetUser(OnTaskCompleted caller, String userId, String token) {
            mCaller = caller;
            authHeader =  "Token " + token;
            mUri = BASE_URL + "users/" + userId;
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", authHeader);
            return DataAccess.this.get(mUri, headers);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                Log.d(LOGTAG, "User is null");
                mCaller.onTaskCompleted(null);
            } else {
                User user = User.fromJsonString(responseBody);
                mCaller.onTaskCompleted(user);
            }
        }
    }

    private class AsyncGetGiftIds extends AsyncTask<Void, Void, String> {
        private OnTaskCompleted mCaller;

        public AsyncGetGiftIds(OnTaskCompleted caller) {
            mCaller = caller;
        }

        @Override
        protected String doInBackground(Void... params) {
            String gifts_uri = BASE_URL + "gifts";
            return DataAccess.this.get(gifts_uri);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                mCaller.onTaskCompleted(null);
            } else {
                // convert to list of Gifts
                Gson gson = new Gson();
                String[] giftIdArray = gson.fromJson(responseBody, String[].class);
                final List<String> giftIds = new ArrayList<>(Arrays.asList(giftIdArray));
                mCaller.onTaskCompleted(giftIds);
            }
        }

    }

    private class AsyncGetGift extends AsyncTask<Void, Void, String> {
        String mUri;
        OnTaskCompleted mCaller;

        public AsyncGetGift(OnTaskCompleted caller, String giftId) {
            mUri = BASE_URL + "gifts/" + giftId;
            mCaller = caller;
        }

        @Override
        protected String doInBackground(Void... params) {
            return DataAccess.this.get(mUri);
        }

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                mCaller.onTaskCompleted(null);
            } else {
                Gift gift = Gift.fromJsonString(responseBody);
                mCaller.onTaskCompleted(gift);
            }
        }
    }

    private class AsyncGetImage extends AsyncTask<Void, Void, Bitmap> {
        OnTaskCompleted mCaller;
        String mUri;
        String mImageId;

        public AsyncGetImage(OnTaskCompleted caller, String imageId) {
            mCaller = caller;
            mImageId = imageId;
            mUri = BASE_URL + "images/" + imageId + ".jpg";
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Log.d(LOGTAG, "Gettimg image " + mUri);
            try {
                URL url = new URL(mUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                Log.d(LOGTAG, "Image get response code: " + responseCode);
                if (responseCode == 200) {
                    InputStream imageStream = conn.getInputStream();
                    Log.d(LOGTAG, "Decoding image");
                    Bitmap image = BitmapFactory.decodeStream(imageStream);
                    // TODO: Should probably close the input stream on finally
                    Log.d(LOGTAG, "Decoding finished");
                    imageStream.close();
                    return image;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Log.d(LOGTAG, ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            Log.d(LOGTAG, "Getting image task completed!");
            images.put(mUri, image);
            mCaller.onTaskCompleted(image);
        }
    }
}
