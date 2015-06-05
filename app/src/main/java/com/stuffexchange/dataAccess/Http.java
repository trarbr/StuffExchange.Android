package com.stuffexchange.dataAccess;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    public static String postImage(String uri, Map<String, String> headers, Uri imageUri) {
        String boundary = "*****";
        String twoHyphens = "--";
        String crlf = "\r\n";

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append(twoHyphens).append(boundary).append(crlf);
        bodyBuilder.append("Content-Disposition: file; name=\"image\"; filename=\"image.jpg\"").append(crlf);
        bodyBuilder.append("Content-Type: image/jpeg").append(crlf);
        bodyBuilder.append(crlf);

        StringBuilder end = new StringBuilder();
        end.append(twoHyphens).append(boundary).append(twoHyphens).append(crlf);

        int maxBufferSize = 8192;
        int bytesRead;
        byte[] imageBuffer = new byte[maxBufferSize];

        try {
            File imageFile = new File(imageUri.getPath());
            FileInputStream imageStream = new FileInputStream(imageFile);

            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.writeBytes(bodyBuilder.toString());
            while ((bytesRead = imageStream.read(imageBuffer, 0, maxBufferSize)) > 0) {
                writer.write(imageBuffer, 0, bytesRead);
            }
            writer.writeBytes(end.toString());
            writer.flush();

            int responseCode = conn.getResponseCode();
            Log.d(LOGTAG, "postImage response code: " + responseCode);
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
}
