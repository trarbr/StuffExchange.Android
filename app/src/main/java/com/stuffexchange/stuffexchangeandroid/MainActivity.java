package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
    private DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        String token = app.getToken();
        String userId = app.getUserId();
        Log.d(LOGTAG, "Got token: " + token);
        Log.d(LOGTAG, "Got userId: " + userId);
        dataAccess = app.getDataAccess();
        TextView greeterTextView = (TextView) findViewById(R.id.greeterTextView);
        dataAccess.GetUser(new UserGetter(greeterTextView), userId, token);
        dataAccess.GetGiftIds(new GiftIdsGetter());
    }

    private class UserGetter implements OnTaskCompleted {
        private TextView mGreeterTextView;
        public UserGetter(TextView greeterTextView) {
            mGreeterTextView = greeterTextView;
        }
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get user from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                User user = (User) o;
                Log.d(LOGTAG, "Got user " + user.getUserIdentity().getUsername());
                String username = user.getUserIdentity().getUsername();
                Resources res = getResources();
                String message = String.format(res.getString(R.string.MainGreeterLabel), username);
                mGreeterTextView.setText(message);
            }
        }
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
        private GiftIdArrayAdapter.GiftViewHolder mGiftView;
        private int mPosition;
        public GiftGetter(GiftIdArrayAdapter.GiftViewHolder giftView, int position) {
            mGiftView = giftView;
            mPosition = position;
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
                Gift gift = (Gift) o;
                ImageView giftImageView = mGiftView.giftImageView;
                giftImageView.setTag(mPosition);
                if (gift.hasImages()) {
                    String imageId = gift.getCoverImage() + "_thumb";
                    Log.d(LOGTAG, "Image Id: " + imageId);
                    dataAccess.GetImage(new CoverImageGetter(giftImageView, mPosition), imageId);
                } else {
                    giftImageView.setImageResource(R.drawable.default_image);
                }

                mGiftView.titleTextView.setText(gift.getTitle());
                mGiftView.descriptionTextView.setText(gift.getDescription());
            }
        }
    }

    private class CoverImageGetter implements OnTaskCompleted {
        ImageView mImageView;
        int mPosition;
        public CoverImageGetter(ImageView imageView, int position) {
            mImageView = imageView;
            mPosition = position;
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
                if ((int)mImageView.getTag() == mPosition) {
                    Bitmap image = (Bitmap) o;
                    mImageView.setImageBitmap(image);
                }
            }
        }
    }

    private class GiftIdArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<>();
        public GiftIdArrayAdapter(Context context, List<String> gifts) {
            super(context, R.layout.gift_list_layout, gifts);
            for (int i = 0; i < gifts.size(); i++) {
                mIdMap.put(gifts.get(i), i);
            }
        }

        public class GiftViewHolder {
            ImageView giftImageView;
            TextView titleTextView;
            TextView descriptionTextView;

            public GiftViewHolder(View v) {
                giftImageView = (ImageView) v.findViewById(R.id.giftImageView);
                titleTextView = (TextView) v.findViewById(R.id.commentUser);
                descriptionTextView = (TextView) v.findViewById(R.id.giftDescription);
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            GiftViewHolder viewHolder = null;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.gift_list_layout, parent, false);
                viewHolder = new GiftViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (GiftViewHolder) row.getTag();
            }

            String giftId = getItem(position);
            dataAccess.GetGift(new GiftGetter(viewHolder, position), giftId);
            return row;
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
