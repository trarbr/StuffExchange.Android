package com.stuffexchange.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MakeOfferActivity extends ActionBarActivity {
    private String giftId;
    private String token;
    private DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);
        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        dataAccess = app.getDataAccess();
        token = app.getToken();
        giftId = getIntent().getStringExtra("GiftId");
        dataAccess.GetGift(new WishersGetter(), giftId);
    }

    private class WishersGetter implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o == null) {
                String message = "Could not get wishers from server";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            } else {
                Gift gift = (Gift) o;
                final List<UserIdentity> wishers = gift.getWishers();
                ListView wishersListView = (ListView) findViewById(R.id.wishersListView);
                UsersArrayAdapter adapter = new UsersArrayAdapter(MakeOfferActivity.this, wishers);
                wishersListView.setAdapter(adapter);
                wishersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        UserIdentity userIdentity = wishers.get(position);
                        String userId = userIdentity.getId();
                        makeOffer(userId);
                    }
                });
            }
        }
    }

    private void makeOffer(String userId) {
        dataAccess.MakeOffer(new OfferMaker(), giftId, userId, token);
    }

    private class OfferMaker implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null && (Boolean) o) {
                Intent intent = new Intent(getApplicationContext(), GiftActivity.class);
                intent.putExtra("GiftId", giftId);
                startActivity(intent);
            }
            else {
                String message = "Could not make offer";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private class UsersArrayAdapter extends ArrayAdapter<UserIdentity> {
        public UsersArrayAdapter(Context context, List<UserIdentity> wishers) {
            super(context, R.layout.user_layout, wishers);
        }
        private class UserViewHolder {
            TextView usernameTextView;
            public UserViewHolder(View view) {
                usernameTextView = (TextView) view.findViewById(R.id.username);
            }
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            UserViewHolder viewHolder = null;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.user_layout, parent, false);
                viewHolder = new UserViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (UserViewHolder) row.getTag();
            }

            UserIdentity userId = getItem(position);
            String username = userId.getUsername();
            viewHolder.usernameTextView.setText(username);
            return row;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_offer, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
