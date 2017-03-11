package com.example.whp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.whp.R;
import com.example.whp.adapters.CustomListDevicesConnect;
import com.example.whp.models.DevicesConnect;
import com.example.whp.utils.WifiHotsPotManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ListDeviceConnect extends AppCompatActivity {
    WifiHotsPotManager wifiHotsPotManager;
    CustomListDevicesConnect adapter;
    ListView listdeviceconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whp_002);
        setTitle("List Device");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wifiHotsPotManager = new WifiHotsPotManager(getApplication());
        listdeviceconnect = (ListView) findViewById(R.id.listdeviceconnect);
        ShowClientConnect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listdevice, menu);

        return super.onCreateOptionsMenu(menu);

    }

    private void ShowClientConnect(){
        wifiHotsPotManager.getClientList(false, new WifiHotsPotManager.FinishScan() {
            @Override
            public void onFinishScan(ArrayList<DevicesConnect> clients) {
                adapter = new CustomListDevicesConnect(getApplication(),clients);
                listdeviceconnect.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getMacAddClient() {
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null ) {
                    String mac = splitted[3];
                    System.out.println("Mac : Outside If "+ mac );
                    if (mac.matches("..:..:..:..:..:..")) {
                        macCount++;
                        Toast.makeText(
                                getApplicationContext(),
                                "Mac_Count  " + macCount + "   MAC_ADDRESS  "
                                        + mac, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        } catch(Exception e) {
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.animator.slide_out_right,R.animator.slide_out_left);
                return true;
            case R.id.reload:
                ShowClientConnect();

                overridePendingTransition(0,0);
                finish();
                startActivity(getIntent());
                overridePendingTransition(0,0);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_out_right,R.animator.slide_out_left);
    }
}
