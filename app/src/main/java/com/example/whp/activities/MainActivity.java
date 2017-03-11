package com.example.whp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whp.R;
import com.example.whp.utils.WifiHotsPotManager;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String Security[] = {"none", "WPA2 PSK"};
    final int[] a = {0, 1};
    boolean isPressed;
    TextView txtSSID,txtSecurity,txtPassword;
    Button btnListDevice;
    ImageView imageView;

    WifiHotsPotManager wifiHotsPotManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whp_001);
        wifiHotsPotManager = new WifiHotsPotManager(getApplication());
        Init();
    }

    private void Init(){
        txtSSID = (TextView) findViewById(R.id.NetWorkSSID);
        txtPassword = (TextView) findViewById(R.id.Password);
        txtSecurity = (TextView) findViewById(R.id.Security);
        imageView = (ImageView) findViewById(R.id.turnon_off);
        btnListDevice = (Button) findViewById(R.id.listdevice);
        if(wifiHotsPotManager.isWifiApEnabled() == false){
            imageView.setImageResource(R.drawable.off);
        }else {
            imageView.setImageResource(R.drawable.on);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getInfoHotspot();
    }


    public  void getInfoHotspot() {
        WifiManager wifimanager = (WifiManager) MainActivity.this.getSystemService(this.WIFI_SERVICE);
        Method[] methods = wifimanager.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("getWifiApConfiguration")) {
                WifiConfiguration config = null;
                try {
                    config = (WifiConfiguration) m.invoke(wifimanager);
                    String keymgmt = "";
                    if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE))
                        keymgmt += " NONE";

                    if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP))
                        keymgmt += " WPA_EAP";

                    if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK))
                        keymgmt += " WPA_PSK";
                    txtSSID.setText(config.SSID);
                    txtPassword.setText(config.preSharedKey);
                    txtSecurity.setText(keymgmt);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu, menu);
            return super.onCreateOptionsMenu(menu);

        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Dialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Dialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText SSID = (EditText) dialogView.findViewById(R.id.editText);
        final EditText PASSWORD = (EditText) dialogView.findViewById(R.id.editText2);
        final Spinner SPINER = (Spinner) dialogView.findViewById(R.id.security);
        final CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    PASSWORD.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    PASSWORD.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Security);
        SPINER.setAdapter(adapter);
        SPINER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                a[0] = SPINER.getSelectedItemPosition();
                if (a[0] == 0) {
                    PASSWORD.setEnabled(false);
                } else {
                    PASSWORD.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (SPINER.getSelectedItemPosition() == 0) {
            PASSWORD.setEnabled(false);
        }
        WifiManager wifimanager = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        Method[] methods = wifimanager.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("getWifiApConfiguration")) {
                WifiConfiguration config = null;
                try {
                    config = (WifiConfiguration) m.invoke(wifimanager);
                    if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
                        SPINER.setSelection(0);
                    }
                    if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                        SPINER.setSelection(1);
                    }
                    SSID.setText(config.SSID);
                    PASSWORD.setText(config.preSharedKey);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        dialogBuilder.setTitle("Set up Wi-Fi hotspot");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                 String NewSSID = SSID.getText().toString();
                 String newPass = PASSWORD.getText().toString();
                if (a[0] == 0) {
                    PASSWORD.setVisibility(View.GONE);
                    WifiConfiguration configuration = new WifiConfiguration();
                    configuration.SSID = NewSSID;
                    wifiHotsPotManager.setWifiApConfiguration(configuration);
                } else {
                    WifiConfiguration configuration = new WifiConfiguration();
                    configuration.SSID = NewSSID;
                    configuration.preSharedKey = newPass;
                    configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    if (PASSWORD.length() < 8) {
                        Toast.makeText(getApplication(), "The Password must have at least 8 characters", Toast.LENGTH_SHORT).show();
                        Dialog();
                    } else {
                        if( wifiHotsPotManager.setWifiApConfiguration(configuration)){
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(R.animator.slide_right,R.animator.slide_right);

                            Toast.makeText(getApplication(), "Success", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplication(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        dialogBuilder.setCancelable(false);
        AlertDialog b = dialogBuilder.create();
        b.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        b.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.turnon_off:
                if (isPressed) {
                    imageView.setImageResource(R.drawable.on);
                    if(wifiHotsPotManager.setEnableWifi(null,true)){
                        Toast.makeText(getApplication(),"Created successfully",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Initialization failed", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    imageView.setImageResource(R.drawable.off);
                    if( wifiHotsPotManager.setEnableWifi(null,false)){
                        Toast.makeText(getApplication(),"Closes successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Close Failure", Toast.LENGTH_SHORT).show();
                    }
                }
                isPressed = !isPressed;
                break;
            case R.id.listdevice:
                Intent intent = new Intent(getApplicationContext(),ListDeviceConnect.class);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_in_right,R.animator.slide_in_left);
                break;
        }
    }

}
