package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String LOGTAG = "StuffExchange";
    private String token;
    private List<Gift> gifts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String token = getIntent().getStringExtra("Token");
        Log.d(LOGTAG, "Got token: " + token);
        this.token = token;

        // TODO: get gifts from server
        // send a get request...
        // get the json response
        // use each json thing to populate the list of gifts
        new AsyncGetGiftIds().execute();
    }

    private class AsyncGetGiftIds extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String gifts_uri = "http://10.0.2.2:3579/gifts";
            try {
                URL url = new URL(gifts_uri);
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

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                String message = "Could not get gifts from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                // convert to list of Gifts
                Gson gson = new Gson();
                String[] giftIdArray = gson.fromJson(responseBody, String[].class);
                final List<String> giftIds = new ArrayList<>(Arrays.asList(giftIdArray));
                final ListView giftsListView = (ListView)findViewById(R.id.giftsListView);
                final GiftIdArrayAdapter adapter = new GiftIdArrayAdapter(MainActivity.this, giftIds);
                giftsListView.setAdapter(adapter);
                giftsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO: start activity
                        Intent intent = new Intent(getApplicationContext(), GiftActivity.class);
                        String giftId = giftIds.get(position);
                        intent.putExtra("GiftId", giftId);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private class AsyncSetImage extends AsyncTask<Void, Void, Bitmap> {
        String mUri;
        ImageView mImageView;

        public AsyncSetImage(String imageId, ImageView imageView) {
            mUri = "http://10.0.2.2:3579/images/" + imageId + "_thumb,jpg";
            mImageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO: Check if in cache
            try {
                URL url = new URL(mUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream imageStream = conn.getInputStream();
                    Bitmap image = BitmapFactory.decodeStream(imageStream);
                    // TODO: Should probably close the input stream on finally
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
            // TODO: Add to cache
            // TODO: handle fast scrolling (don't set the image if someone else did)
            mImageView.setImageBitmap(image);
        }
    }

    private class AsyncSetGift extends AsyncTask<Void, Void, String> {
        String mUri;
        View mGiftView;

        public AsyncSetGift(String giftId, View giftView) {
            mUri = "http://10.0.2.2:3579/gifts/" + giftId;
            mGiftView = giftView;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(mUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
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

        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody == null) {
                String message = "Could not get gift from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            } else {
                Gift gift = Gift.fromJsonString(responseBody);
                if (gift == null) {
                    return;
                }
                // TODO: Add to memory cache
                ImageView giftImageView = (ImageView) mGiftView.findViewById(R.id.giftImageView);
                if (gift.hasImages()) {
                    String imageUri = gift.getCoverImage();
                    String thumbUri = "http://10.0.2.2:3579/images/" + imageUri + "_thumb.jpg";
                    AsyncSetImage imageSetter = new AsyncSetImage(thumbUri, giftImageView);
                    imageSetter.execute();
                }
                TextView titleTextView = (TextView) mGiftView.findViewById(R.id.giftTitle);
                titleTextView.setText(gift.getTitle());
                TextView descriptionTextView = (TextView) mGiftView.findViewById(R.id.giftDescription);
                descriptionTextView.setText(gift.getDescription());
            }
        }
    }

    private class GiftIdArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<>();
        public GiftIdArrayAdapter(Context context, List<String> gifts) {
            super(context, R.layout.gift_layout, gifts);
            for (int i = 0; i < gifts.size(); i++) {
                mIdMap.put(gifts.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View giftView = convertView != null ? convertView :
                    getLayoutInflater().inflate(R.layout.gift_layout, parent, false);

            String giftId = getItem(position);

            // TODO Download Gift
            AsyncSetGift giftSetter = new AsyncSetGift(giftId, giftView);
            giftSetter.execute();

            return giftView;
        }

        @Override
        public long getItemId(int position) {
            String giftId = getItem(position);
            return mIdMap.get(giftId);
        }

        // TODO: Why use stable ids?
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.new_gift) {
            Intent intent = new Intent(getApplicationContext(), AddGiftActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
