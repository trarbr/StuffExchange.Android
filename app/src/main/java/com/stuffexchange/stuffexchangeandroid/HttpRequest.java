package com.stuffexchange.stuffexchangeandroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Map;

public class HttpRequest {
    public enum PayloadType {
        JSON, FORM_DATA, URLENCODED
    }
    // pass in request parameters, return response
    // android raw http request?
    // pass in body, a Map that will be converted to json
    // it should handle parsing to and from json

    // you must tell post if you want to use form-encoding or what
    // pass in files as files
    private final String boundary = "*****";
    private final String twoHyphens = "--";
    private final String crlf = "\r\n";
    private String mMethod;
    private String mUri;
    private Map<String, String> mParams;
    private PayloadType mPayloadType;
    private Map<String, String> mPayload;
    private Map<String, String> mHeaders;

    // TODO: consider factory method instead
    public HttpRequest(
            String method, // GET, POST, etc
            String uri, // URL
            Map<String, String> params, // URL params
            PayloadType payloadType, // type of payload
            Map<String, String> payload, // payload to be sent as json (unless, multipart) - can be determined from Content-Type header
            Map<String, String> headers) {
        mMethod = method;
        mUri = uri;
        mPayloadType = payloadType;
        mPayload = payload;
        mParams = params;
        mHeaders = headers;
    }

    public void Execute() {
        // instantiate AsyncTask, and execute that, passing self as param
        // execute the request
        try {
            if (mMethod.equals("GET")) {
                get(mUri, mParams, mHeaders);
            } else if (mMethod.equals("POST")) {
                post(mUri, mParams, mHeaders);
            }
        } catch (Exception ex) {

        }

    }

    private void execute() {

    }

    public void get(
            String uri,
            Map<String, String> params,
            Map<String, String> headers)
            throws IOException {
        // do stuff here
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // TODO: if params, write to outputstream
        conn.setDoOutput(false);
    }

    private HttpResponse post(
            String uri,
            Map<String, String> params,
            Map<String, String> headers)
            throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        if (mPayloadType == PayloadType.JSON) {
            // turn payload into json
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            JSONObject payload = new JSONObject(mPayload);
            writer.write(payload.toString());
            writer.flush();
        } else if (mPayloadType == PayloadType.URLENCODED) {
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            String payload = encode(mPayload);
            writer.write(payload);
            writer.flush();
            // turn payload into urlencoded string
        } else if (mPayloadType == PayloadType.FORM_DATA) {
            // make sure to send as multipart
            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);

            writer.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            writer.writeBytes(
                    "Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"" + this.crlf);
            Bitmap b = null;
            ByteBuffer buffer = ByteBuffer.allocate(100);
            b.copyPixelsToBuffer(buffer);
            byte[] bytes = buffer.array();
            int x = bytes.length;

            writer.write(bytes);
            writer.writeBytes(crlf);
            writer.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);
            writer.flush();
        }

        // TODO: I guess determining the body depends on Content-Type!
        StringBuilder sb = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(conn.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String responseBody = sb.toString();
        return new HttpResponse(conn.getResponseCode(), responseBody);
    }

    private String encode(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        for (String key : map.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            String value = map.get(key);

            try {
                sb.append(key != null ? URLEncoder.encode(key, "UTF-8") : "");
                sb.append("=");
                sb.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("No UTF-8 support!", e);
            }
        }

        return sb.toString();
    }


    private class AsyncHttpRequest extends AsyncTask<HttpRequest, Void, String> {
        @Override
        protected String doInBackground(HttpRequest... requests) {
            // or you pass a httprequest in here, it calls execute on it
            for (HttpRequest r : requests) {
                r.execute();
            }
            return null;
        }
    }
}

