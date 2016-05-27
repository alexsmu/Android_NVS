package byuie499.auto_nvs;

import android.bluetooth.BluetoothSocket;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

/**
 * Created by marlonvilorio on 5/26/16.
 */
public class OBDConnection {
    BluetoothSocket socket;
    MyApplication app;
    RPMCommand engineRpmCommand;
    SpeedCommand speedCommand;
    EngineCoolantTemperatureCommand engineCoolantTemperatureCommand;

    public OBDConnection(){

        engineRpmCommand = new RPMCommand();
        speedCommand = new SpeedCommand();
        engineCoolantTemperatureCommand = new EngineCoolantTemperatureCommand();

        try {

            app = new MyApplication();
            socket = app.getGlobalBluetoothSocket();
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
        }
        catch (Exception ex) {

        }
    }


}
