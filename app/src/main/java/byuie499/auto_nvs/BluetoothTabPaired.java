package byuie499.auto_nvs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class BluetoothTabPaired extends Fragment {


    private View view = null;
    private ArrayAdapter<String> listAdapter;
    ListView pairedDevicesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //assign neccesarry
        view = inflater.inflate(R.layout.bluetooth_tab_paired, container, false);
        pairedDevicesList = (ListView) view.findViewById(R.id.listView);


        //Check to see if user has Bluetooth, if bluetooth is off turn it on
        BluetoothAdapter mBluetoothAdapter = ((MyApplication) getActivity().getApplicationContext()).getGlobalBluetoothAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(),"Device does not support Bluetooth",Toast.LENGTH_LONG).show();
            return view;
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

        /***********This code is for testing in the emulator*******************/
        pDevices.add("OBDII Test" + "\n" + "00:00:00:00");
        devices.add("00:00:00:00");

        /*********************************************************************/

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_singlechoice,
                pDevices.toArray(new String[pDevices.size()]));
        pairedDevicesList.setAdapter(adapter);

        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                String deviceAddress = devices.get(position).toString();
                ((MyApplication) getActivity().getApplicationContext()).setBluetoothDeviceAddress(deviceAddress);

                /***********This code is for testing in the emulator*******************/
                if (deviceAddress == "00:00:00:00") {
                    Toast.makeText(getActivity().getApplicationContext(),"Bluetooth Connected",Toast.LENGTH_LONG).show();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#008000' >Bluetooth Connected</font><small>"));
                }
                else {
                    /*********************************************************************/

                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                    try {

                        ((MyApplication) getActivity().getApplicationContext()).setGlobalBluetoothSocket(device.createInsecureRfcommSocketToServiceRecord(uuid));
                        ((MyApplication) getActivity().getApplicationContext()).getGlobalBluetoothSocket().connect();
                        Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Connected", Toast.LENGTH_LONG).show();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#008000' >Bluetooth Connected</font><small>"));
                    } catch (Exception e) {
                        //Do Something with this exception
                        Toast.makeText(getActivity().getApplicationContext(), "Error Connecting to Bluetooth Device", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        return view;

    }
}