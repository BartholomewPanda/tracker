package fr.bartholomew.tracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button buttonStartTracking;
    private Button buttonSendLocation;
    private EditText textHost;
    private EditText textPort;
    private EditText textRoomName;
    private TextView textPosition;
    private WifiManager wifi;
    private IntentFilter intentFilter;
    private BroadcastReceiver trackingReceiver;
    private BroadcastReceiver sendLocationReceiver;
    private int nb_sample;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textHost = (EditText)findViewById(R.id.host);
        textPort = (EditText)findViewById(R.id.port);
        textRoomName = (EditText)findViewById(R.id.roomName);
        textPosition = (TextView)findViewById(R.id.position);

        buttonStartTracking = (Button)findViewById(R.id.startTracking);
        buttonStartTracking.setOnClickListener(new StartTrackingListener());

        buttonSendLocation = (Button)findViewById(R.id.saveRoom);
        buttonSendLocation.setOnClickListener(new SendLocationListener());

        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        trackingReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                String host = textHost.getText().toString();
                int port = Integer.parseInt(textPort.getText().toString());
                TrackMe trackMe = new TrackMe(host, port, textPosition, wifi);
                nb_sample++;
                buttonStartTracking.setText("Stop tracking (" + nb_sample + ")");
                trackMe.execute();
            }
        };
        sendLocationReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent intent) {
                String host = textHost.getText().toString();
                int port = Integer.parseInt(textPort.getText().toString());
                ImHere imHere = new ImHere(host, port, textRoomName.getText().toString(), wifi);
                nb_sample++;
                buttonSendLocation.setText("Stop room sampling (" + nb_sample + ")");
                imHere.execute();
            }
        };

        nb_sample = 0;
    }

    private class StartTrackingListener implements View.OnClickListener {

        boolean isSampling;

        StartTrackingListener() {
            isSampling = false;
        }

        @Override
        public void onClick(View v) {
            if (isSampling) {
                textHost.setEnabled(true);
                textPort.setEnabled(true);
                buttonSendLocation.setEnabled(true);
                unregisterReceiver(trackingReceiver);
                isSampling = false;
                buttonStartTracking.setText("Start tracking");
            } else {
                nb_sample = 0;
                textHost.setEnabled(false);
                textPort.setEnabled(false);
                buttonSendLocation.setEnabled(false);
                registerReceiver(trackingReceiver, intentFilter);
                wifi.startScan();
                isSampling = true;
                buttonStartTracking.setText("Stop tracking");
            }
        }
    }

    private class SendLocationListener implements View.OnClickListener {

        boolean isSampling;

        SendLocationListener() {
            isSampling = false;
        }

        @Override
        public void onClick(View v) {
            if (isSampling) {
                textHost.setEnabled(true);
                textPort.setEnabled(true);
                textRoomName.setEnabled(true);
                buttonStartTracking.setEnabled(true);
                unregisterReceiver(sendLocationReceiver);
                isSampling = false;
                buttonSendLocation.setText("Save Room");
            } else {
                nb_sample = 0;
                textHost.setEnabled(false);
                textPort.setEnabled(false);
                textRoomName.setEnabled(false);
                buttonStartTracking.setEnabled(false);
                registerReceiver(sendLocationReceiver, intentFilter);
                wifi.startScan();
                isSampling = true;
                buttonSendLocation.setText("Stop room sampling");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
