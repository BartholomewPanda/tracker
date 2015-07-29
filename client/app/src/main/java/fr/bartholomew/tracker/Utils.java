package fr.bartholomew.tracker;

import android.net.wifi.ScanResult;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Faust on 12/07/15.
 */
public class Utils {
    static public JSONObject to_json(List<ScanResult> wifiScanList) {
        JSONObject jsonQuery = new JSONObject();
        JSONObject jsonScan = new JSONObject();
        try {
            for(int i = 0; i < wifiScanList.size(); i++){
                jsonQuery.put(wifiScanList.get(i).BSSID, wifiScanList.get(i).level);
            }
            jsonScan.put("scan", jsonQuery);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonScan;
    }
}
