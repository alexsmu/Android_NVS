package byuie499.auto_nvs;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by marlonvilorio on 5/12/16.
 */
public class MyApplication extends Application {

    private BluetoothSocket globalBluetoothSocket;
    private String bluetoothDeviceAddress;
    private String bluetoothDeviceName;

    public void setBluetoothDeviceAddress(String bluetoothDeviceAddress) {
        this.bluetoothDeviceAddress = bluetoothDeviceAddress;
    }


    public String getBluetoothDeviceAddress() {
        return bluetoothDeviceAddress;
    }



    public BluetoothSocket getGlobalBluetoothSocket() {
        return globalBluetoothSocket;
    }

    public void setGlobalBluetoothSocket(BluetoothSocket blu) {
        globalBluetoothSocket = blu;
    }

    public void setBluetoothDeviceName(String name) {bluetoothDeviceName = name;}

    public String getBluetoothDeviceName() {return bluetoothDeviceName;}



}