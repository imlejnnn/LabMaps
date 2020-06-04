package com.example.maps;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class IPGeoCoder {


    public interface Callback {
        void onIpFound(GeoPoint point);
        void onIpError();
    }

    private Callback _callback = null;

    public IPGeoCoder(Callback callback){
        this._callback = callback;
    }

    public void SearchObject(final String ip) {
        final ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        String urlString = String.format("http://www.datasciencetoolkit.org/ip2coordinates/%s", ip);
        final Callback cb = this._callback;
        new AsyncNetworking() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jObject = new JSONObject(result);
                    JSONObject response = jObject.getJSONObject(ip);
                    if(response != null) {
                        double lat = Double.parseDouble(response.getString("latitude").trim());
                        double lon = Double.parseDouble(response.getString("longitude").trim());
                        cb.onIpFound(new GeoPoint(lat, lon));
                    }
                    else {
                        cb.onIpError();
                    }
                } catch (JSONException e) {
                    cb.onIpError();
                }
            }

        }.execute(urlString);
    }

    class AsyncNetworking extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            int status = 0;
            try {
                status = con.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Reader streamReader = null;
            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                try {
                    streamReader = new InputStreamReader(con.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Scanner s = new Scanner(streamReader).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return result;
        }
    }
}
