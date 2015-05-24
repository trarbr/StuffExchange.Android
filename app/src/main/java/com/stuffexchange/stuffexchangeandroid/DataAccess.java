package com.stuffexchange.stuffexchangeandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataAccess {
    private final String LOGTAG = "DataAccess";
    private final String BASE_URL = "http://10.0.2.2:3579/";

    public void GetGiftIds(OnTaskCompleted caller) {
        // TODO: start async task, oncomplete call the callback
        new AsyncGetGiftIds(caller).execute();
    }

    public void GetGift(OnTaskCompleted caller, String giftId) {
        new AsyncGetGift(caller, giftId).execute();
    }

    public void GetImage(OnTaskCompleted caller, String imageId) {
        new AsyncGetImage(caller, imageId).execute();
    }

    protected String get(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
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

        public AsyncGetImage(OnTaskCompleted caller, String imageId) {
            mCaller = caller;
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
            mCaller.onTaskCompleted(image);
        }
    }
}
