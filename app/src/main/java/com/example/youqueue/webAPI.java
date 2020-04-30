package com.example.youqueue;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class webAPI extends AsyncTask<String, Void, String> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    private static final String CLIENT_ID = "d19dfd48fcd54626a0f8ff696ada3b9e";
    private static final String REDIRECT_URI = "com.youqueue://callback";
    String url_auth =
            "https://accounts.spotify.com/authorize?"
                    + "client_id=" + CLIENT_ID + "&"
                    + "response_type=code&"
                    + "redirect_uri=" + REDIRECT_URI + "&"
                    + "scope=user-read-private%20user-read-email&"
                    + "state=34fFs29kd09";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {


        URL url = null;
        try {
            url = new URL(url_auth);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //httpURLConnection = (HttpURLConnection) url.openConnection();
        //  HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        try {
            httpURLConnection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        httpURLConnection.setRequestProperty("Connection", "keep-alive");
        httpURLConnection.setRequestProperty("Content-Type",
                "application/json");
        httpURLConnection.setRequestProperty("UseCookieContainer", "True");
        httpURLConnection.setChunkedStreamingMode(0);
        try {
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object messageStatus = null;
        int respCode = 0;
        if (httpURLConnection != null) {
            try {
                respCode = httpURLConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Response code: " + respCode);
        }
        if (respCode == 200) {
            InputStream responseStream = null;
            try {
                responseStream = httpURLConnection.getInputStream();
                URI responseURI = new URI(responseStream.toString());
                String query = responseURI.getQuery();
                System.out.println("Query: " + query);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            System.out.println(responseStream);
        }

        return null;




    }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
        }
    }
