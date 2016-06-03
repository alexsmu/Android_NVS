package byuie499.auto_nvs;

import android.bluetooth.BluetoothSocket;
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
    private MyApplication app;
    private RPMCommand engineRpmCommand;
    private SpeedCommand speedCommand;
    private EngineCoolantTemperatureCommand engineCoolantTemperatureCommand;
    private int testRPM = 3000;
    boolean test = false;
    private int n = 100; //100 times per minute?
    private Handler mHandler = null;
    private boolean isRunning = false;
    public static double rpmFreq;
    int val = 0;

    /**
     * OBDData CONSTRUCTOR : Will setup a connection with bluetooth socket,
     *  and initialize all needed variables
     */
    public OBDData(Handler global_handler, int samples, boolean isTest){
        test = isTest;
        mHandler = global_handler;
        n = samples;
    }

    public void run(){

        if (!test) {
            isRunning = true;
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
            } catch (Exception ex) {

                //Unable to communicated with OBDII error

            }
        } else {
            isRunning = true;
            Thread obdThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //sm.registerListener(xlo_read, accelerometer, 1000);
                    Timer timer = new Timer();
                    TimerTask accumulate = new TimerTask() {
                        @Override
                        public void run() {

                            //Switch these two lines depending of what type of test data we want
                            rpmFreq = getVaryingTestRPMFreq();
                            //rpmFreq = getTestRPMFrequency();
                            ++val;
                                Message done = mHandler.obtainMessage(9,rpmFreq);
                                mHandler.sendMessage(done);
                            }

                    };
                    timer.schedule(accumulate, 0, 1);
                    while (isRunning);
                    timer.cancel();
                    timer.purge();
                    //sm.unregisterListener(xlo_read, accelerometer);
                }
            }, "auto_nvs_fft");
            obdThread.start();

        }
    }


    public int getTestRPM() {
        return testRPM;
    }

    public float getTestRPMFrequency() {
        return (float)testRPM/60;
    }

    public float getVaryingTestRPMFreq() {

        int rpm = getTestRPM();
        switch (rpm) {
            case 3000:
                testRPM+=1000;
                return testRPM/60;
            case 4000:
                testRPM+=1000;
                return testRPM/60;
            case 5000:
                testRPM+=1000;
                return testRPM/60;
            case 6000:
                testRPM+=1000;
                return testRPM/60;
            default:
                testRPM = 3000;
                return testRPM/60;

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
