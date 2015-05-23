package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

        gifts.add(new Gift("Gyngehest",
                "Min datter er blevet for stor, nogen der mangler??", R.drawable.gyngehest_small));
        gifts.add(new Gift("Fiat Bravo",
                "Mine kone synes det er noget bras, men den kører så fuglene synger!",
                R.drawable.fiat_small));
        gifts.add(new Gift("Nike Lunar flyknit1+ multicolor",
                "Super fede, men jeg kan ikke passe dem", R.drawable.nike_small));
        gifts.add(new Gift("Fischer Price gåbil",
                "Med lyd og to funktioner. Kan både bruges til at sidde på og til at gå efter. Kan køre på to lydeffekter - ABC og musik", R.drawable.walkcar_small));
        gifts.add(new Gift("Juniorseng",
                "En super god seng til junior. Madras medfølger ikke!", R.drawable.seng_small));
        gifts.add(new Gift("Messis trøje",
                "Bliv verdens bedste! Jeg er for tyk :(", R.drawable.messi_small));

        final ListView giftsListView = (ListView)findViewById(R.id.giftsListView);
        final GiftArrayAdapter adapter = new GiftArrayAdapter(MainActivity.this, gifts);
        giftsListView.setAdapter(adapter);
        giftsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Gift gift = (Gift) parent.getItemAtPosition(position);
                view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        gifts.remove(gift);
                        adapter.notifyDataSetChanged();
                        view.setAlpha(1);
                    }
                });
            }
        });

        Button activateButton = (Button)findViewById(R.id.activateButton);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncHttpActivate().execute();
            }
        });
    }

    private class AsyncHttpActivate extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return attemptActivate();
        }
        @Override
        protected void onPostExecute(String result) {
            Log.d(LOGTAG, "Reponse on activate: " + result);
            // TODO: toast!
            if (result != null) {
                CharSequence message = result.equals("200") ? "Activated!" : "Bad request!";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private String attemptActivate() {
        String user_uri = "http://10.0.2.2:3579/user";
        try {
            URL url = new URL(user_uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Token " + this.token);
            conn.setRequestProperty("Accept", "application/vnd.stuffexchange.activate+json");
            int responseCode = conn.getResponseCode();
            return Integer.toString(responseCode);
        } catch (Exception ex) {
            // TODO: handle
            return null;
        }
    }

    private class Gift {
        private String name;
        private String description;
        private int iconId;
        public Gift(String name, String description, int iconId) {
            this.name = name;
            this.description = description;
            this.iconId = iconId;
        }

        public String getTitle() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getIconId() {
            return iconId;
        }
    }

    private class GiftArrayAdapter extends ArrayAdapter<Gift> {
        HashMap<Gift, Integer> mIdMap = new HashMap<>();
        public GiftArrayAdapter(Context context, List<Gift> gifts) {
            super(context, R.layout.gift_layout, gifts);
            for (int i = 0; i < gifts.size(); i++) {
                mIdMap.put(gifts.get(i), i);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View giftView = convertView != null ? convertView :
                    getLayoutInflater().inflate(R.layout.gift_layout, parent, false);

            Gift gift = getItem(position);

            ImageView giftImageView = (ImageView) giftView.findViewById(R.id.giftView);
            giftImageView.setImageResource(gift.getIconId());
            TextView titleTextView = (TextView)giftView.findViewById(R.id.giftTitle);
            titleTextView.setText(gift.getTitle());
            TextView descriptionTextView = (TextView)giftView.findViewById(R.id.giftDescription);
            descriptionTextView.setText(gift.getDescription());

            return giftView;
        }

        @Override
        public long getItemId(int position) {
            Gift gift = getItem(position);
            return mIdMap.get(gift);
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
