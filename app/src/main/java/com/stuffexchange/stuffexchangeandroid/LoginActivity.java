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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends ActionBarActivity {
    private static final String LOGTAG = "StuffExchange";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(LOGTAG, "started");

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncHttpLogin().execute();
            }
        });
    }

    private class AsyncHttpLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return attemptLogin();
        }
        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                Log.d(LOGTAG, token);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("Token", token);
                startActivity(intent);
            }
            else
            {
                Log.d(LOGTAG, "token was null");
                CharSequence message = "Login failed! Please try again";
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, message, duration);
                toast.show();
            }
        }
    }

    private String attemptLogin() {
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        //String auth_uri = "http://www.stuffexchange.dev/auth";
        String auth_uri = "http://10.0.2.2:3579/auth";
        // TODO: attempt a login against server
        Map<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);
        JSONObject json = new JSONObject(params);
        try {
            URL url = new URL(auth_uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(json.toString());
            writer.flush();
            String response = "Response: " + conn.getResponseCode() + " " + conn.getResponseMessage();
            Log.d(LOGTAG, response);

            StringBuilder sb = new StringBuilder();
            InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String body = sb.toString();
            Log.d(LOGTAG, "Body: " + body);
            JSONObject obj = new JSONObject(sb.toString());
            return obj.getString("Token");
        } catch (Exception ex) {
            Log.d(LOGTAG, "got exception " + ex.toString());
            ex.printStackTrace();
            return null;
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
