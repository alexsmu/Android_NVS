package byuie499.auto_nvs;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marlonvilorio on 5/26/16.
 */
public class OBDData {
    private BluetoothSocket socket;
    private MyApplication app = new MyApplication();
    private RPMCommand engineRpmCommand;
    private SpeedCommand speedCommand;
    private EngineCoolantTemperatureCommand engineCoolantTemperatureCommand;
    boolean test = false;
    private int Ts = 100; //100 ms?
    private Handler mHandler = null;
    public static boolean isRunning = false;
    private Timer timer;
    public static double rpmFreq;
    public static double imperialTireRPMFreq;
    public double [] objectToSend = new double[2];
    private SettingsData settingsData = null;
    /**
     * OBDData CONSTRUCTOR : Will setup a connection with bluetooth socket,
     *  and initialize all needed variables
     *  Parameter: global_handler --> handler
     *  Parameter: samples --> number of samples
     *  Parameter: isTest --> If true, sample methods will be called instead of actual OBD data
     */
    public OBDData(Handler global_handler, Context mContext,  int period, boolean isTest){
        test = isTest;
        mHandler = global_handler;
        Ts = period;

        if (SettingsData.mContext != mContext)
            settingsData = new SettingsData(mContext);
    }

    /** Runs OBD Threads and polls for RPM Frequency and Tire RPM Frequency*/
    public void run(){
        if (!isRunning) {
            //if this is not a test actually get the information from OBDII
            isRunning = true;
            try {
                //Get GLOBAL bluetooth socket
                socket = app.getGlobalBluetoothSocket();

                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                //Initializing necessary OBDII commands
                engineRpmCommand = new RPMCommand();
                speedCommand = new SpeedCommand();
                engineCoolantTemperatureCommand = new EngineCoolantTemperatureCommand();

                engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                engineCoolantTemperatureCommand.run(socket.getInputStream(), socket.getOutputStream());

                //Thread to get necessary RPMs
                timer = new Timer();
                TimerTask accumulate = new TimerTask() {
                    @Override
                    public void run() {

                        //get motor & wheel RPM
                        rpmFreq = getRPMFrequency();
                        imperialTireRPMFreq = getImperialTireRPMFreq();

                        //Put into array to be sent to main thread
                        objectToSend[0] = rpmFreq;
                        objectToSend[1] = imperialTireRPMFreq;

                        //Send RPMs to main thread
                        Message done = mHandler.obtainMessage(3, objectToSend);
                        mHandler.sendMessage(done);
                    }
                };
                timer.schedule(accumulate, 0, Ts);
            } catch (Exception ex) {
                //Unable to communicated with OBDII error
                isRunning = false;
            }
        }
    }

    public void onPause() {
        if (isRunning) {
            isRunning = false;
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * getRPM : Will get RPM Example Output: 3000
     */
    public int getRPM() {
        try {
            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception ex) {
            //do something
        }
        return engineRpmCommand.getRPM();
    }

    /**
     * getRPMFormatted : Will get RPM Formatted Example Output: 3000RPM
     */
    public String getRPMFormatted() {
        try {
            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception ex) {
            //Do something
        }
        return engineRpmCommand.getFormattedResult();
    }

    /**
     * getRPMFrequency : Will get RPM Frequency Example Output: 7.3
     */
    public double getRPMFrequency() {
        return (double)(getRPM())/60.0;
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
        try {
            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception ex) {
            //do something
        }
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
    public double getImperialTireRPM() {
        double speedInFeet = 0;
        double tireDiameter = 0;
        double tireCircumference = 0;
        try {
            //MyApplication myApp = new MyApplication();
            tireDiameter = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio7", "0"));//myApp.getTireDiameterInches();
            tireCircumference = Math.PI * tireDiameter;

            //Convert to Feet
            tireCircumference = tireCircumference/12;
            speedInFeet = getImperialSpeed() * 88.99213;

        } catch (Exception ex) {
            //give error message
        }
       return speedInFeet / tireCircumference;
    }

    public double getImperialTireRPMFreq() {
        return getImperialTireRPM() / 60.0;
    }

    /**
     * getMetricSpeedFrequency : Will get Frequency from Metric Speed Example Output: 5.2
     */
    public double getMetricSpeedFrequency() {
        return getMetricSpeed() / 3.6;
    }

}
