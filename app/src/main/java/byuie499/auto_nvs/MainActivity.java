package byuie499.auto_nvs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int graph_x_axis_end = 125;  // graph x axis domain limit
    private static final int audio_samples = 32768;    // samples to record before taking fft (must be power of 2)
    private static final double audio_Fs = 44100;     // audio sampling rate. (DO NOT MODIFY)
    private static final double audio_freq_step = audio_samples / audio_Fs;
    private static final int audio_numdps = (int)(Math.ceil(audio_samples * graph_x_axis_end / audio_Fs) ); // number of audio graph datapoints
    private static final int audio_startdps = audio_samples / 2; // starting index (corresponds to 0 hz)
    private static final int audio_enddps = audio_startdps + audio_numdps; // ending index (corresponds to x_axis_end hz)
    private static final int acc_samples = 128; // accelerometer samples
    private static final double acc_Fs = 250;  // accelerometer sampling rate. (MUST MATCH Xlo CLASS SAMPLING FROM TIMER TIMER
    private static final double acc_freq_step = acc_samples / acc_Fs;
    private static final boolean normalize = true;
    private static final boolean in_dB = true;
    private static final double audio_scaling = 1.0;
    private static final int acc_dvsr = 1;
    private static final int acc_numdps = (int) (Math.ceil(acc_samples * graph_x_axis_end / acc_Fs) ); // number of acc graph datapoints
    private static final int acc_startdps = acc_samples / 2; // starting index (corresponds to 0 hz)
    private static final int acc_enddps = acc_startdps + acc_numdps; // ending index (corresponds to x_axis_end hz)
    private static DataPoint[] audio_dps = new DataPoint[audio_numdps];
    private static DataPoint[] accel_dpsX = new DataPoint[acc_numdps];
    private static DataPoint[] accel_dpsY = new DataPoint[acc_numdps];
    private static DataPoint[] accel_dpsZ = new DataPoint[acc_numdps];
    private static DataPoint[] tire_dps = new DataPoint[1];
    private static DataPoint[] rpm_dps = new DataPoint[1];
    private static DataPoint[] device2_dps = new DataPoint[1];
    private static DataPoint[] device3_dps = new DataPoint[1];
    private static DataPoint[] device4_dps = new DataPoint[1];
    private static DataPoint[] device5_dps = new DataPoint[1];
    private static DataPoint[] device6_dps = new DataPoint[1];
    private static double[] audio_result = null;
    private static double[] accel_resultX = null;
    private static double[] accel_resultY = null;
    private static double[] accel_resultZ = null;
    private static double[] obd_result = null;
    private static boolean permission = false; // RECORD_AUDIO permission granted?
    private double[] audio_omega = new double[audio_samples]; // omega container for audio FFT
    private double[] accel_omega = new double[acc_samples];   // omega container for accel FFT
    private Fft[] accelFFT = new Fft[3]; // containers for each accelerometer axis FFT results
    private MicData rec_mic = null;      // container for audio recording thread object
    private Xlo rec_acc = null;          // container for accelerometer recording thread object
    private LineGraphSeries<DataPoint> audioSeries = new LineGraphSeries<>();       // graph series
    private LineGraphSeries<DataPoint> xSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> ySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> zSeries = new LineGraphSeries<>();
    private DataPoint[] tempZPeaks;
    private PointsGraphSeries<DataPoint> device2_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device3_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device4_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device5_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device6_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> x_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> y_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> z_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> secondOrderPeaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> thirdOrderPeaks = new PointsGraphSeries<>();
    private GraphView graph = null; // container for graph object
    private SettingsData settingsData = null; // dummy container to initialize SettingsData for the current context
    public Handler mHandler = null;  // container for thread handler
    public OBDData obdData;         // container for OBD recording thread object
    MyApplication app = new MyApplication();              // required for bluetooth socket
    public CheckBox dontShowAgain;  // don't show again (bluetooth connection expected) checkbox
    private ToggleButton recordButton, noise, vibration; // containers for layout buttons
    private String spinnerName = "Profile 1";
    private double dev1val;
    private double dev2val;
    private double dev3val;
    private double dev4val;
    private double dev5val;
    private double dev6val;
    private CheckBox xCheck;
    private CheckBox yCheck;
    private CheckBox zCheck;
    private TextView rpmFreqText;
    private TextView tireRPMFreqText;
    private ToggleButton measureButton;
    private double measureX1 = 0;
    private double measureX2 = 0;
    private ToggleButton vibPause;
    private ToggleButton noisePause;
    private Spinner scope;
    private boolean decreasingZoom=true;
    private Button leftScroll;
    private Button rightScroll;
    private String[] arraySpinner;
    private BroadcastReceiver mReceiver;
    private Correlation correlate;
    private DataPoint[] a_peaks;
    private List<Map.Entry<String, Integer>> a_occ;
    private TextView occ_text;
    private int[] occ_ids = { R.id.occ0a, R.id.occ0b, R.id.occ0c, R.id.occ0d, R.id.occ0e,
            R.id.occ1a, R.id.occ1b, R.id.occ1c, R.id.occ1d, R.id.occ1e};

    //These Varibales are for the tutorial
    private ShowcaseView showcaseView;
    private int counter = 0;
    private Target t1, t2, t3, t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // load layout
        settingsData = new SettingsData(getApplicationContext());
        if (SettingsData.isChecked(SettingsData.currentProfile + "_check9", true)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on
        }
        setBluetoothReceiver();
        initMembers(); // initialize containers
        initGraph();   // initialize graph

        t1 = new ViewTarget(R.id.toggleVibration, this);
        t2 = new ViewTarget(R.id.toggleNoise, this);
        t3 = new ViewTarget(R.id.toggleMeasure, this);
        t4 = new ViewTarget(R.id.vibrationPause, this);


        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(Target.NONE)
                .setOnClickListener(showcaseClickListener)
                .setContentTitle("Tutorial")
                .setContentText("In the Center you'll find a graph. This will contain the Information " +
                        "on the FFT for the noise and vibration.")
                .singleShot(42)
                .build();
        showcaseView.setButtonText("next");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsData.mContext != getApplicationContext())
            settingsData = new SettingsData(getApplicationContext());
        if (SettingsData.isChecked(SettingsData.currentProfile + "_check9", true)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on
        }
        setPrefs();
        addDeviceSeries();
        checkBluetoothConnection();   // show connection pop-up if necessary
        check_record_permissions();
        // Bluetooth setup
        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        // Start obd thread
        addListenerToToggleButtons(); // add listeners
        handleVibrationChecks();
        obdData.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        rec_acc.onPause(); // stop recordings
        rec_mic.onPause();
        obdData.onPause();
        graph.removeAllSeries();
        unregisterReceiver(mReceiver); // release bluetooth receiver
    }

    @Override // Handles whether user granted recording permission (Android 6.0+)
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            } else {
                permission = false;
            }
        }
    }

    protected void check_record_permissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) { // check for permission
            permission = false;
            ActivityCompat.requestPermissions(MainActivity.this, // request permission
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        } else { // we have permission
            permission = true;
        }
    }

    public void handleVibrationChecks(){
        xCheck.setChecked(SettingsData.isChecked(xCheck.getTag().toString(), true));
        yCheck.setChecked(SettingsData.isChecked(yCheck.getTag().toString(), true));
        zCheck.setChecked(SettingsData.isChecked(zCheck.getTag().toString(), true));
        vibPause.setChecked(SettingsData.isChecked(vibPause.getTag().toString(), false));
        noisePause.setChecked(SettingsData.isChecked(noisePause.getTag().toString(), false));

        if (xCheck.isChecked()) {
            graph.addSeries(x_peaks);
            graph.addSeries(xSeries);
        }
        if (yCheck.isChecked()) {
            graph.addSeries(y_peaks);
            graph.addSeries(ySeries);
        }
        if (zCheck.isChecked()) {
            graph.addSeries(z_peaks);
            graph.addSeries(zSeries);
        }

        this.arraySpinner = new String[]{
                "100","200","300","400"
        };

        vibPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if (isChecked){
                    rec_acc.onPause();
                }else {
                    rec_acc.run();
                }
            }
        });

        noisePause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if (isChecked){
                    rec_mic.onPause();
                } else {
                    rec_mic.run();
                }
            }
        });

        xCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if(isChecked) {
                    graph.removeSeries(x_peaks);
                    graph.removeSeries(xSeries);
                    graph.addSeries(x_peaks);
                    graph.addSeries(xSeries);
                }
                else
                {
                    graph.removeSeries(x_peaks);
                    graph.removeSeries(xSeries);
                }
            }
        });

        yCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if(isChecked) {
                    graph.removeSeries(y_peaks);
                    graph.removeSeries(ySeries);
                    graph.addSeries(y_peaks);
                    graph.addSeries(ySeries);
                }
                else
                {
                    graph.removeSeries(y_peaks);
                    graph.removeSeries(ySeries);
                }
            }
        });

        zCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if(isChecked) {
                    graph.removeSeries(z_peaks);
                    graph.removeSeries(zSeries);
                    graph.addSeries(z_peaks);
                    graph.addSeries(zSeries);
                }
                else
                {
                    graph.removeSeries(z_peaks);
                    graph.removeSeries(zSeries);
                }
            }
        });

    }

    protected void setPrefs(){
        dev1val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio1", "0"));
        dev2val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio2", "0"));
        dev3val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio3", "0"));
        dev4val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio4", "0"));
        dev5val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio5", "0"));
        dev6val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio6", "0"));
    }

    protected void initMembers() { // Initialize member containers
        mHandler = new MainHandler(Looper.getMainLooper());
        rec_acc = new Xlo(this, mHandler, acc_samples, acc_dvsr);
        rec_mic = new MicData(mHandler, audio_samples, audio_scaling, normalize, in_dB);
        correlate = new Correlation();
        xCheck = (CheckBox) findViewById(R.id.xCheck);
        yCheck = (CheckBox) findViewById(R.id.yCheck);
        zCheck = (CheckBox) findViewById(R.id.zCheck);
        rpmFreqText = (TextView) findViewById(R.id.rpmFreq);
        tireRPMFreqText = (TextView) findViewById(R.id.tireFreq);
        measureButton = (ToggleButton) findViewById(R.id.toggleMeasure);
        vibPause = (ToggleButton) findViewById(R.id.vibrationPause);
        noisePause = (ToggleButton) findViewById(R.id.noisePause);
        //scope = (Spinner) findViewById(R.id.zoom);
        rightScroll = (Button) findViewById(R.id.right);
        leftScroll = (Button) findViewById(R.id.left);
        noise = (ToggleButton) findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) findViewById(R.id.toggleVibration);
        recordButton = (ToggleButton) findViewById(R.id.startstop);

        //We might want to hand this differently in the future
        if (app.getGlobalBluetoothSocket() == null) {
            obdData = new OBDData(mHandler, getApplicationContext(), acc_samples, true);
        } else {
            obdData = new OBDData(mHandler,getApplicationContext(), acc_samples, false);
        }

        accelFFT[0] = new Fft(acc_samples, mHandler, 4, normalize, in_dB);
        accelFFT[1] = new Fft(acc_samples, mHandler, 5, normalize, in_dB);
        accelFFT[2] = new Fft(acc_samples, mHandler, 6, normalize, in_dB);
        Fft.getOmega(audio_omega, audio_Fs);
    }

    protected void initGraph() {
        graph = (GraphView) findViewById(R.id.fftGraph);
        if (graph == null) throw new AssertionError("Object cannot be null");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.getLegendRenderer().setWidth(graph_x_axis_end);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100/*xMaxBoundary*/);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-80);
        graph.getViewport().setMaxY(20);

        //Set Scalable and Zoom
        //graph.getViewport().setScalable(true);
        //graph.getViewport().setScrollable(true);

        // Titles
        audioSeries.setTitle("Mic");
        xSeries.setTitle("X");
        ySeries.setTitle("Y");
        zSeries.setTitle("Z");
        audio_peaks.setTitle("APeaks");
        x_peaks.setTitle("XPeaks");
        y_peaks.setTitle("YPeaks");
        z_peaks.setTitle("ZPeaks");
        secondOrderPeaks.setTitle("2ndOrder");
        thirdOrderPeaks.setTitle("3rdOrder");

        // Colors
        audioSeries.setColor(Color.parseColor("#181907"));
        xSeries.setColor(Color.parseColor("#0B3861"));
        ySeries.setColor(Color.parseColor("#0B6138"));
        zSeries.setColor(Color.parseColor("#610B0B"));
        audio_peaks.setColor(Color.parseColor("yellow"));
        x_peaks.setColor(Color.parseColor("yellow"));
        y_peaks.setColor(Color.parseColor("yellow"));
        z_peaks.setColor(Color.parseColor("yellow"));
        secondOrderPeaks.setColor(Color.parseColor("blue"));
        thirdOrderPeaks.setColor(Color.parseColor("blue"));

        // Shapes
        secondOrderPeaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(6);
                canvas.drawCircle(x,y,5,paint);
            }
        });

        thirdOrderPeaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(6);
                canvas.drawCircle(x,y,5,paint);
            }
        });

        audio_peaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(4);
                canvas.drawLine(x-5, y-5, x+5, y+5, paint);
                canvas.drawLine(x+5, y-5, x-5, y+5, paint);
            }
        });

        x_peaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(4);
                canvas.drawLine(x-5, y-5, x+5, y+5, paint);
                canvas.drawLine(x+5, y-5, x-5, y+5, paint);
            }
        });

        y_peaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(4);
                canvas.drawLine(x-5, y-5, x+5, y+5, paint);
                canvas.drawLine(x+5, y-5, x-5, y+5, paint);
            }
        });

        z_peaks.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(4);
                canvas.drawLine(x-5, y-5, x+5, y+5, paint);
                canvas.drawLine(x+5, y-5, x-5, y+5, paint);
            }
        });

        audio_peaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                if(measureButton.isChecked()){
                    if (measureX1 == 0){
                        measureX1 = dataPoint.getX();
                    } else if (measureX2 == 0) {
                        measureX2 = dataPoint.getX();
                        double result = measureX2 - measureX1;
                        Toast.makeText(MainActivity.this,"Distance: "+ Double.toString(result),Toast.LENGTH_SHORT).show();
                        measureX2 =0;
                        measureX1 = 0;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Ap: " + dataPoint, Toast.LENGTH_SHORT).show();
                }
            }
        });

        x_peaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Ap: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        y_peaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Ap: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        z_peaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Ap: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        addDevices();
        graph.addSeries(secondOrderPeaks);
    }

    void addDevices(){
        device2_series.setTitle(SettingsData.getString("name2", ""));
        device2_series.setColor(Color.parseColor("#003366"));

        device3_series.setTitle(SettingsData.getString("name3", ""));
        device3_series.setColor(Color.parseColor("#00aeef"));

        device4_series.setTitle(SettingsData.getString("name4", ""));
        device4_series.setColor(Color.parseColor("#8eca40"));

        device5_series.setTitle(SettingsData.getString("name5", ""));
        device5_series.setColor(Color.parseColor("#8c0066"));

        device6_series.setTitle(SettingsData.getString("name6", ""));
        device6_series.setColor(Color.parseColor("#fe6302"));

        device2_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });
        device3_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });
        device4_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });
        device5_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });
        device6_series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });
    }

    protected void addDeviceSeries() {
        if (SettingsData.isChecked("check2", false)){
            graph.addSeries(device2_series);
        }
        if (SettingsData.isChecked("check3", false)){
            graph.addSeries(device3_series);
        }
        if (SettingsData.isChecked("check4", false)){
            graph.addSeries(device4_series);
        }
        if (SettingsData.isChecked("check5", false)){
            graph.addSeries(device5_series);
        }
        if (SettingsData.isChecked("check6", false)){
            graph.addSeries(device6_series);
        }
    }

    void addListenerToToggleButtons() {
        noise.setChecked(SettingsData.isChecked(noise.getTag().toString(), false)); // retrieve previous state
        if (noise.isChecked()) { // enable mic recording
            if (permission) {
                MicData.isEnabled = true; // enable thread
                if (!SettingsData.isChecked(noisePause.getTag().toString(), false))
                    rec_mic.run(); // run recording thread
                graph.addSeries(audioSeries); // graph results
                graph.addSeries(audio_peaks);
            } else {
                MicData.isEnabled = false;
                noise.setChecked(false); // no permission, undo check
                Toast.makeText(getApplicationContext(), "RECORDING DENIED BY USER", Toast.LENGTH_SHORT).show();
            }
        } else {
            MicData.isEnabled = false;
            rec_mic.onPause();
        }

        vibration.setChecked(SettingsData.isChecked(vibration.getTag().toString(), false));
        if  (vibration.isChecked()) {
            Xlo.isEnabled = true;
            if (!SettingsData.isChecked(vibPause.getTag().toString(), false))
                rec_acc.run();
        } else {
            Xlo.isEnabled = false;
            rec_acc.onPause();
        }

        noise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                graph.removeSeries(audioSeries);
                graph.removeSeries(audio_peaks);
                if (isChecked) { // enable mic recording
                    check_record_permissions();
                    if (permission) {
                        MicData.isEnabled = true; // enable thread
                        if (noisePause.isChecked())
                            noisePause.setChecked(false);
                        else
                            rec_mic.run(); // run recording thread
                        graph.addSeries(audioSeries); // graph results
                        graph.addSeries(audio_peaks);
                    } else {
                        buttonView.setChecked(false); // no permission, undo check
                    }
                } else {
                    rec_mic.onPause();
                } // store settings (remember checked state)
                SettingsData.setChecked(buttonView.getTag().toString(), buttonView.isChecked());
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Xlo.isEnabled = isChecked; // enable/disable thread
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if  (isChecked) {
                    if (vibPause.isChecked())
                        vibPause.setChecked(false);
                    else
                        rec_acc.run(); // run thread

                } else {
                    rec_acc.onPause(); // stop thread
                }
            }
        });
/*
        recordButton.setOnClickListener(new View.OnClickListener(){ //to be implemented
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), prefs.getString("ratio1", ""), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }

    void setBluetoothReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Off</font><small>"));
                            }
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            //Do something
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#008000' >Bluetooth On</font><small>"));
                            }
                            new AlertDialog.Builder(context)
                                    .setTitle("Bluetooth On")
                                    .setMessage("The Bluetooth on your device is currently on. Please make sure that you are connected " +
                                            "to the correct device.")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            //Do something
                            break;
                    }
                }
            }
        };
    }

    void checkBluetoothConnection() {
        if (app.getGlobalBluetoothSocket() == null) {
            LayoutInflater adbInflater = LayoutInflater.from(this);
            View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
            dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
            if (!SettingsData.isChecked(dontShowAgain.getTag().toString(), false)) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                dontShowAgain.setChecked(false);
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SettingsData.setChecked(buttonView.getTag().toString(), isChecked);
                    }
                });
                alertDialog.setView(eulaLayout);
                alertDialog.setTitle("Bluetooth Connection Alert");
                alertDialog.setMessage("In order for this app to work correctly " +
                        "you need to be connected to an OBDII.");
                alertDialog.setNegativeButton("Ok", new android.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (getSupportActionBar() != null)      // update action bar (must still be set to connected if connected!)
                            getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Disconnected</font><small>"));
                    }
                });
                alertDialog.setPositiveButton("Bluetooth Settings", new android.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialog.show();
            }
        } else {
            if (getSupportActionBar() != null)      // update action bar (must still be set to connected if connected!)
                getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Connected</font><small>"));
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        switch(item.getItemId()){
            case R.id.one:
                Intent intent1 = new Intent(this, SettingsMenu.class);
                startActivity(intent1);
                break;
            case R.id.two:

                break;
            case R.id.three:
                alertDialog.setTitle("About");
                alertDialog.setMessage(getString(R.string.copyright));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            case R.id.bluetooth_settings:
                Intent intent = new Intent(this, BluetoothActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        "Unknown...",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.save_screenshot:
                try {
                    File imageFile ;

                    Date now = new Date();
                    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                    // image naming and path  to include sd card  appending name you choose for file
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
                    imageFile = new File(mPath);

                    // create bitmap screen capture
                    View v1 = getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);


                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();



                } catch (Throwable e) {
                    // Several error may come out with file handling or OOM
                    e.printStackTrace();
                }
                break;

        }
        //Return false to allow normal menu processing to proceed,
        //true to consume it here.
        return false;
    }

    // separated handler as a class, so the code is more readable, and less things clog onCreate
    private class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 2: // Audio fft is complete
                {
                    // add to series
                    audio_result = (double[]) msg.obj;
                    int j = 0;
                    for (int i = audio_startdps; i < audio_enddps; ++i) {
                        audio_dps[j++] = new DataPoint(audio_omega[i], audio_result[i]);
                    }
                    audioSeries.resetData(audio_dps);
                    a_peaks = correlate.findPeaks(audio_dps);
                    audio_peaks.resetData(a_peaks);
                    a_occ = correlate.count_occurrence(a_peaks);
                    j = 0;
                    for (Map.Entry<String, Integer> entry : a_occ)
                    {
                        occ_text = ((TextView) findViewById(occ_ids[j++]));
                        occ_text.setText(entry.getKey() + ": " + entry.getValue());
                        if (j == 10)
                            break;
                    }
                    for (int i = j; i < 10; i++) {
                        occ_text = ((TextView) findViewById(occ_ids[j++]));
                        occ_text.setText(" ");
                    }

                    break;
                }
                case 3: // Accelerometer data is ready
                {
                    Fft.getOmega(accel_omega, 1000 / Xlo.avg_sample_Ts);
                    // begin fft
                    if (xCheck.isChecked())
                        accelFFT[0].run(Xlo.xAcc, "x");
                    if (yCheck.isChecked())
                        accelFFT[1].run(Xlo.yAcc, "y");
                    //if (yCheck.isChecked())
                        //accelFFT[1].run(Xlo.rAcc, "r");
                    if (zCheck.isChecked())
                        accelFFT[2].run(Xlo.zAcc, "z");
                    break;
                }
                case 4: // Accelerometer x fft is complete
                {
                    if (xCheck.isChecked()) {
                        // add to series
                        accel_resultX = (double[]) msg.obj;
                        int j = 0;
                        for (int i = acc_startdps; i < acc_enddps; ++i) {
                            accel_dpsX[j++] = new DataPoint(accel_omega[i], accel_resultX[i]);
                        }
                        xSeries.resetData(accel_dpsX);
                        x_peaks.resetData(correlate.findPeaks(accel_dpsX));
                    }
                    break;
                }
                case 5: // Accelerometer y fft complete
                {
                    if (yCheck.isChecked()) {
                        // add to series
                        accel_resultY = (double[]) msg.obj;
                        int j = 0;
                        for (int i = acc_startdps; i < acc_enddps; ++i) {
                            accel_dpsY[j++] = new DataPoint(accel_omega[i], accel_resultY[i]);
                        }
                        ySeries.resetData(accel_dpsY);
                        y_peaks.resetData(correlate.findPeaks(accel_dpsY));
                        occ_text = ((TextView) findViewById(occ_ids[0]));
                        occ_text.setText(String.format("%.2f ms", Xlo.avg_sample_Ts));
                    }
                    break;
                }
                case 6: // Accelerometer z fft complete
                {
                    if (zCheck.isChecked()) {
                        //add to series
                        accel_resultZ = (double[]) msg.obj;
                        int j = 0;
                        for (int i = acc_startdps; i < acc_enddps; ++i) {
                            accel_dpsZ[j++] = new DataPoint(accel_omega[i], accel_resultZ[i]);
                        }

                        try {
                            //****** There are some issues happening here ******* Null Exception
                            zSeries.resetData(accel_dpsZ);
                            tempZPeaks = correlate.findPeaks(accel_dpsZ);
                            z_peaks.resetData(tempZPeaks);

                            try {
                                secondOrderPeaks.resetData(correlate.findSecOrderPeaks(tempZPeaks, obd_result[0]));
                            } catch (Exception ex) {
                                //do nothing for now
                            }
                        } catch (Exception ex){
                            //do nothing for now
                        }
                        //code to test interpolating rpm frequenzy
                        /*double accel_RPM_freqZ = obd_result[0];
                        double accel_RPM_magZ = correlate.interpolateMagnitude(accel_RPM_freqZ, acc_freq_step, accel_dpsZ);
                        z_peaks.resetData(new DataPoint[] {
                            new DataPoint(accel_RPM_freqZ, accel_RPM_magZ)
                        });*/
                    }
                    break;
                }
                case 7: // Audio fft correlation complete
                {
                    // add to series
                    break;
                }
                case 8: // Accelerometer fft correlation complete
                {
                    //add to series
                    break;
                }
                case 9:
                {
                    //OBD Data (it is test data for now)
                    obd_result = (double[]) msg.obj;
                    rpm_dps[0] = new DataPoint(obd_result[0],0);

                    DecimalFormat df = new DecimalFormat("#.##");

                    rpmFreqText.setText("RPM/Freq: "+ df.format(obd_result[0]));

                    /*TESTING*/
                    device2_dps[0] = new DataPoint(dev1val*obd_result[0]/dev2val,0);
                    device2_series.resetData(device2_dps);

                    device3_dps[0] = new DataPoint(dev1val*obd_result[0]/dev3val,0);
                    device3_series.resetData(device3_dps);

                    device4_dps[0] = new DataPoint(dev1val*obd_result[0]/dev4val,0);
                    device4_series.resetData(device4_dps);

                    device5_dps[0] = new DataPoint(dev1val*obd_result[0]/dev5val,0);
                    device5_series.resetData(device5_dps);

                    device6_dps[0] = new DataPoint(dev1val*obd_result[0]/dev6val,0);
                    device6_series.resetData(device6_dps);
                    /*TESTING*/

                    //Tire RPM Frequency
                    tire_dps[0] = new DataPoint(obd_result[1],0);
                    tireRPMFreqText.setText("TireRPM/Freq: "+ df.format(obd_result[1]));

                    break;
                }
                default:
                {
                    super.handleMessage(msg);
                }
            }
        }
    }

    public View.OnClickListener showcaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(counter){
                case 0:
                    showcaseView.setShowcase(t1, true);
                    showcaseView.setContentTitle("VIBRATION");
                    showcaseView.setContentText("This Button Will enable you to graph the vibrations.");
                    break;
                case 1:
                    showcaseView.setShowcase(t2, true);
                    showcaseView.setContentTitle("NOISE");
                    showcaseView.setContentText("This button will enable you to graph the noise");
                    break;
                case 2:
                    showcaseView.setShowcase(t3, true);
                    showcaseView.setContentTitle("MEASURE");
                    showcaseView.setContentText("Finds the distance between two peaks.");
                    showcaseView.setButtonText("close");
                    break;
                case 3:
                    showcaseView.setShowcase(t4, true);
                    showcaseView.setContentTitle("PAUSE");
                    showcaseView.setContentText("This will pause the graph that's running real time.");
                    showcaseView.setButtonText("close");
                    break;
                case 4:
                    showcaseView.hide();
                    break;

            }
            counter++;
        }
    };
}