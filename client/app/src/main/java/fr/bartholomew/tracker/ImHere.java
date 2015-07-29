package fr.bartholomew.tracker;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Faust on 12/07/15.
 */
public class ImHere extends Query {
    public ImHere(String host, int port, String room, WifiManager wifi) {
        this.host = host;
        this.port = port;
        this.wifi = wifi;
        try {
            this.json = Utils.to_json(wifi.getScanResults());
            this.json.put("action", "imhere");
            this.json.put("room", room);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        wifi.startScan();
    }
}
