package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final String LOGTAG = "StuffExchange";
    private String token;
    private DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String token = getIntent().getStringExtra("Token");
        Log.d(LOGTAG, "Got token: " + token);
        this.token = token;

        dataAccess = new DataAccess();
        dataAccess.GetGiftIds(new GiftIdsGetter());
    }

    private class GiftIdsGetter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get gifts from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                final List<String> giftIds = (List<String>) o;
                final ListView giftsListView = (ListView)findViewById(R.id.giftsListView);
                final GiftIdArrayAdapter adapter = new GiftIdArrayAdapter(MainActivity.this, giftIds);
                giftsListView.setAdapter(adapter);
                giftsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), GiftActivity.class);
                        String giftId = giftIds.get(position);
                        intent.putExtra("GiftId", giftId);
                        Log.d(LOGTAG, "GiftId: " + giftId);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private class GiftGetter implements OnTaskCompleted {
        private View mGiftView;
        public GiftGetter(View giftView) {
            mGiftView = giftView;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get gift from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            } else {
                // populate the view
                Gift gift = (Gift) o;
                ImageView giftImageView = (ImageView) mGiftView.findViewById(R.id.giftImageView);
                if (gift.hasImages()) {
                    String imageId = gift.getCoverImage() + "_thumb";
                    dataAccess.GetImage(new CoverImageGetter(giftImageView), imageId);
                }
                TextView titleTextView = (TextView) mGiftView.findViewById(R.id.giftTitle);
                titleTextView.setText(gift.getTitle());
                TextView descriptionTextView = (TextView) mGiftView.findViewById(R.id.giftDescription);
                descriptionTextView.setText(gift.getDescription());
            }
        }
    }

    private class CoverImageGetter implements OnTaskCompleted {
        ImageView mImageView;
        public CoverImageGetter(ImageView imageView) {
            mImageView = imageView;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get image from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                Bitmap image = (Bitmap) o;
                // TODO: Add to cache
                // TODO: handle fast scrolling (don't set the image if someone else did)
                mImageView.setImageBitmap(image);
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
            dataAccess.GetGift(new GiftGetter(giftView), giftId);

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
