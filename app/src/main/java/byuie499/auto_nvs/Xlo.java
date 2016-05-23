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
    public static Sensor accelerometer = null;
    public static SensorManager sm = null;
    public static double xc = 0;
    public static double yc = 0;
    public static double zc = 0;
    public static double[] xAcc;
    public static double[] yAcc;
    public static double[] zAcc;
    public static boolean isRunning = false;
    public static boolean isEnabled = true;
    private int val = 0;
    private int N = 0;
    private int accum = 1;
    private Handler mHandler = null;

    public Xlo(Activity mMain, Handler global_handler, int samples, int dvsr){
        mHandler = global_handler;
        N = samples;
        accum = dvsr;
        xAcc = new double[samples];
        yAcc = new double[samples];
        zAcc = new double[samples];
        if (sm == null)
            sm = (SensorManager) mMain.getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public void run() {
        if (isEnabled) {
            isRunning = true;
            Thread fftThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sm.registerListener(xlo_read, accelerometer, 1000);
                    Timer timer = new Timer();
                    TimerTask accumulate = new TimerTask() {
                        @Override
                        public void run() {
                            xAcc[val] = xc;
                            yAcc[val] = yc;
                            zAcc[val] = zc;
                            ++val;
                            if (val == N / accum) {
                                Message done = mHandler.obtainMessage(3);
                                mHandler.sendMessage(done);
                            }
                        }
                    };
                    timer.schedule(accumulate, 0, 1);
                    while (isRunning) ;
                    timer.cancel();
                    sm.unregisterListener(xlo_read, accelerometer);
                }
            }, "auto_nvs_fft");
            fftThread.start();
        }
    }

    public void onPause() {
        isRunning = false;
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
