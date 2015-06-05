package com.stuffexchange.app;

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

import com.stuffexchange.dataAccess.OnTaskCompleted;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity {
    private static final String LOGTAG = "StuffExchange";
    private StuffExchangeApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(LOGTAG, "started");
        app = (StuffExchangeApplication) getApplication();

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        EditText usernameEditText = (EditText)findViewById(R.id.emailEditText);
        String username = usernameEditText.getText().toString();
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        app.getDataAccess().Login(new OnLogin(), username, password);
    }

    private class OnLogin implements OnTaskCompleted {
        @Override
        public void onTaskCompleted(Object o) {
            if (o != null) {
                try {
                    JSONObject response = (JSONObject) o;
                    String token = response.getString("Token");
                    String userId = response.getString("UserId");
                    app.setToken(token);
                    app.setUserId(userId);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } catch (JSONException ex) {
                    Log.d(LOGTAG, "Login JSON response malformed", ex);
                    CharSequence message = "Login failed! Server response malformed!";
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, message, duration);
                    toast.show();
                }
            } else {
                CharSequence message = "Login failed! Please try again";
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
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
