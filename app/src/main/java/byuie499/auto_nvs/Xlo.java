package byuie499.auto_nvs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Xlo {
    public static Sensor accelerometer = null; // sensor
    public static SensorManager sm = null;     // manager
    public static double xc = 0;  // current value as read from the sensor
    public static double yc = 0;
    public static double zc = 0;
    public static double[] xAcc; // accumulator for values
    public static double[] yAcc;
    public static double[] zAcc;
    public static double[] xAcc0; // accumulator for values
    public static double[] yAcc0;
    public static double[] zAcc0;
    public static boolean isRunning = false; // continue thread flag
    public static boolean isEnabled = true;  // vibration selected flag
    private int val = 0; // index for accumulator
    private int N = 0;   // number of samples to accumulate before overwriting
    private int accum = 1; // divisions of buffer to wait before sending message
    private Handler mHandler = null; // thread handler for message
    private Timer timer;

    private TimerTask accumulate = null;

    public Xlo(Activity mMain, Handler global_handler, int samples, int dvsr){
        mHandler = global_handler;
        N = samples;
        accum = dvsr;
        xAcc = new double[samples];
        yAcc = new double[samples];
        zAcc = new double[samples];
        xAcc0 = new double[samples];
        yAcc0 = new double[samples];
        zAcc0 = new double[samples];
        sm = (SensorManager) mMain.getSystemService(Activity.SENSOR_SERVICE);

        /*Check for Linear Acceleration Sensor*/
        /*If no Linear Accelerometer is present assign Accelerometer*/
        if (sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            Log.d("Linear Accelerometer: ", "present");
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        else {
            Log.d("Linear Accelerometer: ", "not present");
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    public void run() {
        if (isEnabled & !isRunning) {
            isRunning = true;
            sm.registerListener(xlo_read, //listener
                    accelerometer, //sensor
                    1000); // period in us (NOT PRECISE, USUALLY FASTER)
            timer = new Timer();
            accumulate = new TimerTask() {
                @Override
                public synchronized void run() {
                    xAcc0[val] = xc; // store current sensor values
                    yAcc0[val] = yc;
                    zAcc0[val] = zc;
                    val = (val + 1) % N; // increment index
                    if (val % (N / accum) == 0) { // send message to main thread
                        for (int i = 0; i < N; ++i) {
                            xAcc[i] = xAcc0[i]; // copy current sensor values
                            yAcc[i] = yAcc0[i];
                            zAcc[i] = zAcc0[i];
                        }
                        Message done = mHandler.obtainMessage(3);
                        mHandler.sendMessage(done);
                    }
                }
            };
            timer.schedule(accumulate, // timer task
                    0, // delay
                    1); // period in ms
        }
    }

    public void onPause() {
        if (isRunning) {
            timer.cancel();
            timer.purge();
            sm.unregisterListener(xlo_read, accelerometer); // release listener
            isRunning = false;
        }
    }

    public SensorEventListener xlo_read = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            xc = event.values[0];
            yc = event.values[1];
            zc = event.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
