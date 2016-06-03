package byuie499.auto_nvs;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by marlonvilorio on 5/12/16.
 */
public class MyApplication extends Application {

    private BluetoothSocket globalBluetoothSocket = null;

    public BluetoothSocket getGlobalBluetoothSocket() {
        return globalBluetoothSocket;
    }

    public void setGlobalBluetoothSocket(BluetoothSocket blu) {
        globalBluetoothSocket = blu;
    }

}