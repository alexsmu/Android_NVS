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
import android.os.SystemClock;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Xlo {
    public static Sensor accelerometer = null; // sensor
    public static SensorManager sm = null;     // manager
    public static double xc = 0;  // current value as read from the sensor
    public static double yc = 0;
    public static double zc = 0;
    public static double rc = 0; // current radius (polar coordinates)
    public static double totalR = 0;
    public static double avgR = 0;
    public static long current_time;
    public static long last_time;
    public static double[] rAcc;
    public static double[] rAcc0;
    public static boolean isRunning = false; // continue thread flag
    public static boolean isEnabled = true;  // vibration selected flag
    private boolean isSampling = false;
    private boolean sensorEvent = false;
    private int readCount = 0;
    public static double avg_sample_Ts = 0;
    private double avg_sampling = 0;

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
        rAcc = new double[samples];
        rAcc0 = new double[samples];
        sm = (SensorManager) mMain.getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void run() {
        if (isEnabled & !isRunning) {
            isRunning = true;
            sm.registerListener(xlo_read, //listener
                    accelerometer, //sensor
                    200); // period in us (NOT PRECISE, USUALLY FASTER)
            timer = new Timer();
            accumulate = new TimerTask() {
                @Override
                public synchronized void run() {
                    isSampling = true;
                    last_time = current_time;
                    current_time = System.currentTimeMillis();
                    avg_sampling += (current_time - last_time);
                    while(sensorEvent);
                    rAcc0[val] = rc - avgR;
                    val = (val + 1) % N; // increment index
                    if (val % (N / accum) == 0) { // send message to main thread
                        avg_sample_Ts = avg_sampling / (N / accum);
                        avg_sampling = 0;
                        for (int i = 0; i < N; ++i) {
                            rAcc[i] = rAcc0[i];
                        }
                        Message done = mHandler.obtainMessage(1);
                        mHandler.sendMessage(done);
                    }
                    isSampling = false;
                }
            };
            current_time = System.currentTimeMillis();
            timer.schedule(accumulate, // timer task
                    0, // delay
                    4); // period in ms
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
            if (!isSampling) {
                sensorEvent = true;
                xc = event.values[0];
                yc = event.values[1];
                zc = event.values[2];
                rc = Math.sqrt(xc * xc + yc * yc + zc * zc);
                totalR += rc;
                readCount++;
                avgR = totalR / readCount;
                sensorEvent = false;
            } else {
                readCount = 0;
                totalR = 0;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
