package byuie499.auto_nvs;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class Xlo {
    public static Sensor accelerometer = null; // sensor
    public static SensorManager sm = null;     // manager
    public static int axis = 0;
    public static double xc = 0;  // current value as read from the sensor
    public static double yc = 0;
    public static double zc = 0;
    public static double rc = 0;
    public static double totalR = 0;
    public static double avgR = 0;
    public static long current_time;
    public static long last_time;
    public static double[] rAcc;
    public static double[] rAcc0;
    public static boolean isRunning = false; // continue thread flag
    public static boolean isEnabled = true;  // vibration selected flag
    public static boolean pseudoSampling = false;
    private int readCount = 0; // index for accumulator
    private int eventCount = 0; // index for accumulator
    public static double avg_sample_Fs = 0;
    private double avg_sampling = 0;
    public Thread xlo_thread;
    public SensorEventListener xlo_read;
    private int N = 0;   // number of samples to accumulate before overwriting
    private Handler mHandler = null; // thread handler for message
    private Timer timer;
    private TimerTask accumulate = null;

    public Xlo(Activity mMain, Handler global_handler, int samples){
        mHandler = global_handler;
        N = samples;
        rAcc = new double[samples];
        rAcc0 = new double[samples];
        sm = (SensorManager) mMain.getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void run() {
        if (isEnabled & !isRunning) {
            isRunning = true;
            if (pseudoSampling) {
                eventCount = 0;
                readCount = 0;
                xlo_read = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        xc = event.values[0];
                        yc = event.values[1];
                        zc = event.values[2];
                        if (axis > 2)
                            rc = Math.sqrt(xc * xc + yc * yc + zc * zc);
                        else
                            rc = event.values[axis];
                        totalR += rc;
                        eventCount++;
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };
                sm.registerListener(xlo_read, //listener
                        accelerometer, //sensor
                        0); // period in us (NOT PRECISE, USUALLY FASTER)
                timer = new Timer();
                accumulate = new TimerTask() {
                    @Override
                    public synchronized void run() {
                        last_time = current_time;
                        rAcc0[readCount++] = rc;
                        current_time = System.nanoTime();
                        avg_sampling += (current_time - last_time);
                        if (readCount == N) { // send message to main thread
                            avg_sample_Fs = 1000000000.0d * readCount / avg_sampling;
                            avg_sampling = 0;
                            avgR = totalR / eventCount;
                            for (int i = 0; i < readCount; ++i) {
                                rAcc[i] = rAcc0[i] - avgR;// copy current sensor values
                            }
                            Message done = mHandler.obtainMessage(1);
                            mHandler.sendMessage(done);
                            readCount = 0;
                            eventCount = 0;
                            totalR = 0;
                        }
                    }
                };
                current_time = System.nanoTime();
                timer.schedule(accumulate, // timer task
                        0, // delay
                        5); // period in ms
            } else {
                xlo_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readCount = 0;
                        totalR = 0;
                        current_time = System.nanoTime();
                        xlo_read = new SensorEventListener() {
                            @Override
                            public void onSensorChanged(SensorEvent event) {
                                last_time = current_time;
                                current_time = System.nanoTime();
                                if (axis == 3) {
                                    xc = event.values[0];
                                    yc = event.values[1];
                                    zc = event.values[2];
                                    rAcc0[readCount] = Math.sqrt(xc * xc + yc * yc + zc * zc);
                                } else {
                                    rAcc0[readCount] = event.values[axis];
                                }
                                totalR += rAcc0[readCount];
                                avg_sampling += current_time - last_time; //converted to micro secs
                                readCount++;
                                if (readCount == N || avg_sampling > 740000000) { // send message to main thread
                                    avg_sample_Fs = 1000000000.0d * readCount / avg_sampling; //in Hz
                                    avg_sampling = 0;
                                    avgR = totalR / readCount;
                                    for (int i = 0; i < readCount; ++i) {
                                        rAcc[i] = rAcc0[i] - avgR;
                                    }
                                    for (int i = readCount; i < N; ++i) {
                                        rAcc[i] = 0;
                                    }
                                    Message done = mHandler.obtainMessage(1);
                                    mHandler.sendMessage(done);
                                    readCount = 0;
                                    totalR = 0;
                                }
                            }

                            @Override
                            public void onAccuracyChanged(Sensor sensor, int accuracy) {

                            }
                        };
                        sm.registerListener(xlo_read, //listener
                                accelerometer, //sensor
                                0);
                    }
                }, "xlo_thread");
                xlo_thread.start();
            }
        }
    }

    public void onPause() {
        if (isRunning) {
            sm.unregisterListener(xlo_read, accelerometer); // release listener
            isRunning = false;
            if (pseudoSampling) {
                timer.cancel();
                timer.purge();
            } else {
                try {
                    xlo_thread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
