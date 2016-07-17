package byuie499.auto_nvs;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

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
    private int readCount = 0; // index for accumulator
    public static double avg_sample_Fs = 0;
    private double avg_sampling = 0;
    public Thread xlo_thread;
    public SensorEventListener xlo_read;
    private int N = 0;   // number of samples to accumulate before overwriting
    private int accum = 1; // divisions of buffer to wait before sending message
    private Handler mHandler = null; // thread handler for message

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
                            xc = event.values[0];
                            yc = event.values[1];
                            zc = event.values[2];
                            rAcc0[readCount] = Math.sqrt(xc * xc + yc * yc + zc * zc);
                            totalR += rAcc0[readCount];
                            avg_sampling += current_time - last_time; //converted to micro secs
                            readCount++;
                            if (readCount % (N / accum) == 0 || avg_sampling > 740000000) { // send message to main thread
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

    public void onPause() {
        if (isRunning) {
            sm.unregisterListener(xlo_read, accelerometer); // release listener
            try {
                xlo_thread.join();
            } catch(Exception e) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }
}
