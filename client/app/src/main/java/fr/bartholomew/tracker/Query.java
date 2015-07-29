package fr.bartholomew.tracker;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Faust on 12/07/15.
 */
public abstract class Query extends AsyncTask<Void, Void, String> {

    protected JSONObject json;
    protected String host;
    protected int port;
    protected WifiManager wifi;

    @Override
    protected String doInBackground(Void... params) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress addr = InetAddress.getByName(host);
            DatagramPacket packet;
            byte[] data = json.toString().getBytes();
            byte[] receiveData = new byte[1024];
            socket.setSoTimeout(1000);

            Log.d("tracker", "send data");
            packet = new DatagramPacket(data, data.length, addr, port);
            socket.send(packet);

            Log.d("tracker", "recv data");
            packet = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(packet);

            Log.d("tracker", "return data");
            return new String(packet.getData(), 0, packet.getLength());
        } catch (Exception e) {
            Log.e("tracker", "exception", e);
            e.printStackTrace();
        }
        return null;
    }
}
