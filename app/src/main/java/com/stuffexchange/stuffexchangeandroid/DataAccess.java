package com.stuffexchange.stuffexchangeandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public void MakeOffer(OnTaskCompleted caller, String giftId, String userId, String token) {
        new AsyncMakeOffer(caller, giftId, userId, token).execute();
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
        if (!images.containsKey(imageId)) {
            new AsyncGetImage(caller, imageId).execute();
        }
        else {
            caller.onTaskCompleted(images.get(imageId));
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
        // try {
        //     URL url = new URL(uri);
        //     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //     conn.setReadTimeout(10000);
        //     conn.setConnectTimeout(15000);
        //     conn.setRequestMethod("GET");
        //     conn.setDoInput(true);

        //     int responseCode = conn.getResponseCode();
        //     // read responsebody into a string
        //     String responseBody = null;
        //     if (responseCode == 200) {
        //         StringBuilder sb = new StringBuilder();
        //         InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
        //         BufferedReader reader = new BufferedReader(streamReader);
        //         String line;
        //         while ((line = reader.readLine()) != null) {
        //             sb.append(line).append("\n");
        //         }
        //         responseBody = sb.toString();
        //     }
        //     return responseBody;
        // } catch (Exception ex) {
        //     Log.d(LOGTAG, ex.getMessage());
        //     return null;
        // }
    }

    private String put(String uri, Map<String, String> headers, Map<String, String> body) {
        // TODO: handle when no body
        JSONObject jsonBody = new JSONObject(body);
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    Log.d(LOGTAG, "Setting header " + header.getKey() + " to " + header.getValue());
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoOutput(true);
            // TODO: handle when no body
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonBody.toString());
            writer.flush();

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
            Log.d(LOGTAG, "got exception " + ex.toString());
            return null;
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

        @Override protected void onPostExecute(String responseBody) {
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
            // try {
            //     URL url = new URL(mUri);
            //     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //     conn.setReadTimeout(10000);
            //     conn.setConnectTimeout(15000);
            //     conn.setRequestMethod("GET");
            //     conn.setRequestProperty("Authorization", authHeader);
            //     conn.setDoInput(true);

            //     int responseCode = conn.getResponseCode();
            //     // read responsebody into a string
            //     String responseBody = null;
            //     if (responseCode == 200) {
            //         StringBuilder sb = new StringBuilder();
            //         InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
            //         BufferedReader reader = new BufferedReader(streamReader);
            //         String line;
            //         while ((line = reader.readLine()) != null) {
            //             sb.append(line).append("\n");
            //         }
            //         responseBody = sb.toString();
            //     }
            //     return responseBody;
            // } catch (Exception ex) {
            //     Log.d(LOGTAG, ex.getMessage());
            //     return null;
            // }
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
            // TODO: Check if in cache
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
            images.put(mImageId, image);
            mCaller.onTaskCompleted(image);
        }
    }
}
