package com.stuffexchange.dataAccess;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by troels on 05-06-2015.
 */
public class Http {
    private static String LOGTAG = "DataAccess";

    public static String get(String uri, Map<String, String> headers) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
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

    public static String get(String uri) {
        return get(uri, null);
    }
}
