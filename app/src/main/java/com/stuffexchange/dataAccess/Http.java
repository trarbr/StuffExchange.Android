package com.stuffexchange.dataAccess;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

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

    public static String post(String uri, Map<String, String> headers, Map<String, String> requestBody) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            if (requestBody != null) {
                conn.setDoOutput(true);
                JSONObject jsonBody = new JSONObject(requestBody);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            // read responsebody into a string
            Log.d(LOGTAG, "POST response code: " + responseCode);
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
            Log.d(LOGTAG, "got exception " + ex.getMessage());
            return null;
        }
    }

    public static String put(String uri, Map<String, String> headers, Map<String, String> requestBody) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            if (requestBody != null) {
                conn.setDoOutput(true);
                JSONObject jsonBody = new JSONObject(requestBody);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonBody.toString());
                writer.flush();
            }

            int responseCode = conn.getResponseCode();
            // read responsebody into a string
            Log.d(LOGTAG, "PUT response code: " + responseCode);
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
            Log.d(LOGTAG, "got exception " + ex.toString());
            return null;
        }
    }
}
