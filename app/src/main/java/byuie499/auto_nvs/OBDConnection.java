package byuie499.auto_nvs;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.RequiresPermission;
import android.webkit.ConsoleMessage;

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
    private BluetoothSocket socket;
    private MyApplication app;
    private RPMCommand engineRpmCommand;
    private SpeedCommand speedCommand;
    private EngineCoolantTemperatureCommand engineCoolantTemperatureCommand;
    private int testRPM = 3000;

    /**
     * OBDConnection CONSTRUCTOR : Will setup a connection with bluetooth socket,
     *  and initialize all needed variables
     */
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

            //Throw exception if it did not work

        }
    }

    public int getTestRPM() {
        return testRPM;
    }

    public float getTestRPMFrequency() {
        return (float)testRPM/60;
    }

    public float getVaryingTestRPM() {

        int freq = (int)getTestRPMFrequency();
        switch (freq) {
            case 52:
                return 53;
            case 53:
                return 54;
            case 54:
                return 55;
            case 55:
                return 58;
            case 58:
                return 61;
            case 61:
                return 50;
            default:
                return 52;

        }
    }


    /**
     * getRPM : Will get RPM Example Output: 3000
     */
    public int getRPM() {
        return engineRpmCommand.getRPM();
    }

    /**
     * getRPMFormatted : Will get RPM Formatted Example Output: 3000RPM
     */
    public String getRPMFormatted() {
        return engineRpmCommand.getFormattedResult();
    }

    /**
     * getRPMFrequency : Will get RPM Frequency Example Output: 7.3
     */
    public double getRPMFrequency() {
        return (double)(getRPM())/60;
    }

    /**
     * getFormattedRPMFrequency : Will get RPM Frequency Example Output: 7.3Hz
     */
    public String getFormattedRPMFrequency() {
        return getRPMFormatted() + "Hz";
    }

    /**
     * getImperialSpeed : Will get Speed in Imperial Units (MPH) Example Output: 65.0
     */
    public double getImperialSpeed() {
        return speedCommand.getImperialSpeed();
    }

    /**
     * getImperialSpeed : Will get Speed in Imperial Units Example Output: 65.0MPH
     */
    public String getImperialSpeedFormatted() { return getImperialSpeed() + "MPH";}

    /**
     * getMetricSpeed : Will get Speed in Metric Units (KM/Hr) Example Output: 32
     */
    public int getMetricSpeed() {
        return speedCommand.getMetricSpeed();
    }

    /**
     * getMetricSpeedFormatted : Will get Speed in Metric Units (KM/Hr) Example Output: 32Km/Hr
     */
    public String getMetricSpeedFormatted() {
        return getMetricSpeed()+"Km/Hr";
    }

    /**
     * getImperialSpeedFrequency : Will get Frequency from Imperial Speed Example Output: 7.3
     */
    public double getImperialSpeedFrequency() {
       return getImperialSpeed()/60;
    }

    /**
     * getMetricSpeedFrequency : Will get Frequency from Metric Speed Example Output: 5.2
     */
    public double getMetricSpeedFrequency() {
        return getMetricSpeed()/60;
    }

    /**
     * getImperialEngineCoolantTemp : Will get Engine Coolant Temperature in Farenheit Example Output: 80
     */
    public double getImperialEngineCoolantTemp() {
        return engineCoolantTemperatureCommand.getImperialUnit();
    }

}
