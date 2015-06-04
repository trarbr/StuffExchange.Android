package com.stuffexchange.stuffexchangeandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddGiftActivity extends ActionBarActivity {
    private final String LOGTAG = "StuffExchange";
    private DataAccess dataAccess;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);
        StuffExchangeApplication app = (StuffExchangeApplication) getApplication();
        token = app.getToken();
        dataAccess = app.getDataAccess();

        final Button addGiftButton = (Button) findViewById(R.id.addGiftButton);
        addGiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "Add gift button clicked");
                addGift();
            }
        });
    }

    public void addGift() {
        // grab title and description, send to dataAccess with token
        EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
        String title = titleEditText.getText().toString();
        EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        String description = descriptionEditText.getText().toString();
        dataAccess.AddGift(new GiftAdder(), title, description, this.token);
    }

    private class GiftAdder implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null) {
                Log.d(LOGTAG, "Gift was added");
                String giftId = (String) o;
                Intent intent = new Intent(getApplicationContext(), EditGiftActivity.class);
                intent.putExtra("GiftId", giftId);
                startActivity(intent);
            } else {
                String message = "Failed to add gift";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_gift, menu);
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
