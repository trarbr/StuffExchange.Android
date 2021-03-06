package com.stuffexchange.app;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stuffexchange.dataAccess.DataAccess;
import com.stuffexchange.dataAccess.OnTaskCompleted;
import com.stuffexchange.model.Gift;
import com.stuffexchange.model.User;

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
            Context context = getApplicationContext();
            if (o == null) {
                String message = "Could not get gifts from server";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
            else {
                final List<String> giftIds = (List<String>) o;
                final ListView giftsListView = (ListView)findViewById(R.id.giftsListView);
                final GiftIdArrayAdapter adapter = new GiftIdArrayAdapter(context, dataAccess, giftIds);
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
