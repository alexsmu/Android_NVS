package byuie499.auto_nvs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by marlonvilorio on 5/5/16.
 */
public class BluetoothTabScan extends Fragment {

    private View view = null;
    ListView discoveredDevicesList;
    ToggleButton scanButton;
    BluetoothAdapter mBluetoothAdapter;
    final ArrayList pDevices = new ArrayList();
    ArrayAdapter btArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                  //  showToast("Enabled");

                   // showEnabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //mDeviceList = new ArrayList<BluetoothDevice>();

               // mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
               // mProgressDlg.dismiss();

               // Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);

               // newIntent.putParcelableArrayListExtra("device.list", mDeviceList);

               // startActivity(newIntent);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
               // BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

               // mDeviceList.add(device);

                //showToast("Found device " + device.getName());
            }
/*            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
            }*/
        }};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bluetooth_tab_scan, container, false);

        scanButton = (ToggleButton) view.findViewById(R.id.scanButton);
        discoveredDevicesList = (ListView) view.findViewById(R.id.devicesfound);
        mBluetoothAdapter = ((MyApplication) getActivity().getApplicationContext()).getGlobalBluetoothAdapter();
        btArrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.select_dialog_singlechoice);
        discoveredDevicesList.setAdapter(btArrayAdapter);

        //Check to see if user has Bluetooth, if bluetooth is off turn it on
       // BluetoothAdapter mBluetoothAdapter = ((MyApplication) getActivity().getApplicationContext()).getGlobalBluetoothAdapter();
        CheckBlueToothState();


/*        scanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // your click actions go here
                if(scanButton.isChecked()) {
                    btArrayAdapter.clear();
                    mBluetoothAdapter.startDiscovery();
                }
                else
                {
                    scanButton.setChecked(false);
                }


            }


        });*/

        scanButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if (isChecked) {
                   // mAdapter.clear();
                   // getActivity().registerReceiver(bReciever, filter);
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                    }
                    mBluetoothAdapter.startDiscovery();
                } else {
                  //  getActivity().unregisterReceiver(bReciever);
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        });


        getActivity().registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState();
        }
    }

    public void CheckBlueToothState() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(),"Device does not support Bluetooth",Toast.LENGTH_LONG).show();
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
        }
    }




    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(ActionFoundReceiver);

        super.onDestroy();
    }
}
