package fr.bartholomew.tracker;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.TypedValue;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;


/**
 * Created by Faust on 12/07/15.
 */
public class TrackMe extends Query {

    private TextView position;

    public TrackMe(String host, int port, TextView position, WifiManager wifi) {
        this.position = position;
        this.host = host;
        this.port = port;
        this.wifi = wifi;
        try {
            this.json = Utils.to_json(wifi.getScanResults());
            this.json.put("action", "trackme");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            position.setTextColor(Color.parseColor("#FF8000"));
            position.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            position.setText("You are in: " + obj.get("predicted").toString());
            wifi.startScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
