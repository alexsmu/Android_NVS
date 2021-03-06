package byuie499.auto_nvs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by marlonvilorio on 5/5/16.
 */
public class BluetoothActivity extends AppCompatActivity {
    ListView pairedDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        //Set Global Bluetooth Adapter to be used by both bluetooth tabs
        ((MyApplication) getApplicationContext()).setGlobalBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());

        //Set Support action bar to change color according our connection to the bluetooth
        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Disconnected</font><small>"));
        getSupportActionBar().setTitle("Bluetooth Settings");

        //assign necessary
        pairedDevicesList = (ListView) findViewById(R.id.listView);

        //Check to see if user has Bluetooth, if bluetooth is off turn it on
        BluetoothAdapter mBluetoothAdapter = ((MyApplication) getApplicationContext()).getGlobalBluetoothAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device does not support Bluetooth",Toast.LENGTH_LONG).show();
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
        }

        //We need these for our devices
        final ArrayList pDevices = new ArrayList();
        final ArrayList devices = new ArrayList();

        //Wait till bluetooth is on
        while (true) {
            if (mBluetoothAdapter.isEnabled()){
                break;
            }
        }

        //Get paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) { //for (int i = 0; i < pairedDevices.size(); i++)
                // Add the name and address to an array adapter to show in a ListView
                pDevices.add(device.getName() + "\n" + device.getAddress()); //pairedDevices[i].add
                devices.add(device.getAddress());
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                pDevices.toArray(new String[pDevices.size()]));
        pairedDevicesList.setAdapter(adapter);

        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                String deviceAddress = devices.get(position).toString();
                ((MyApplication) getApplicationContext()).setBluetoothDeviceAddress(deviceAddress);

                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                try {

                    ((MyApplication) getApplicationContext()).setGlobalBluetoothSocket(device.createInsecureRfcommSocketToServiceRecord(uuid));
                    ((MyApplication) getApplicationContext()).getGlobalBluetoothSocket().connect();
                    Toast.makeText(getApplicationContext(), "Bluetooth Connected", Toast.LENGTH_LONG).show();
                    getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#008000' >Bluetooth Connected</font><small>"));
                } catch (Exception e) {
                    //Do Something with this exception
                    Toast.makeText(getApplicationContext(), "Error Connecting to Bluetooth Device", Toast.LENGTH_LONG).show();
                }
            }

            // }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(BluetoothActivity.this).create();
        switch (item.getItemId()) {
            case R.id.bluetooth_scan:
                alertDialog.setTitle("Bluetooth Scan");
                alertDialog.setMessage("In order to pair your OBDII with this phone:\n" +
                        "1. Go to your Bluetooth Settings.\n" +
                        "2. Pair your phone with the OBDII.\n" +
                        "3. Come back to this list, and click\n    OBDII to connect it."
                );
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            default:
                break;
        }

        return false;
    }


}