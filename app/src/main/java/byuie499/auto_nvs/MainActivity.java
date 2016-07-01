package byuie499.auto_nvs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int graph_x_axis_end = 500;  // graph x axis domain limit
    private static final int audio_samples = 32768;    // samples to record before taking fft (must be power of 2)
    private static final double audio_Fs = 44100;     // audio sampling rate. (DO NOT MODIFY)
    private static final int audio_numdps = (int)(Math.ceil(audio_samples * graph_x_axis_end / audio_Fs) ); // number of audio graph datapoints
    private static final int audio_startdps = audio_samples / 2; // starting index (corresponds to 0 hz)
    private static final int audio_enddps = audio_startdps + audio_numdps; // ending index (corresponds to x_axis_end hz)
    private static final int acc_samples = 512; // accelerometer samples
    private static final double acc_Fs = 1000;  // accelerometer sampling rate. (MUST MATCH Xlo CLASS SAMPLING FROM TIMER TIMER
    private static final int peakThresh = -50;
    private static final boolean normalize = false;
    private static final boolean in_dB = false;
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
    private PointsGraphSeries<DataPoint> obdSeries = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device2_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device3_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device4_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device5_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> device6_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> obdSeriesSpeed = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> x_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> y_peaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> z_peaks = new PointsGraphSeries<>();
    private GraphView graph = null; // container for graph object
    private SettingsData settingsData = null; // dummy container to initialize SettingsData for the current context
    public Handler mHandler = null;  // container for thread handler
    public OBDData obdData;         // container for OBD recording thread object
    MyApplication app = new MyApplication();              // required for bluetooth socket
    public CheckBox dontShowAgain;  // don't show again (bluetooth connection expected) checkbox
    private ToggleButton recordButton, noise, vibration; // containers for layout buttons
    private SharedPreferences prefs;
    private SharedPreferences settingsPrefs;
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
    private int xMaxBoundary = 400;
    private boolean decreasingZoom=true;
    private Button leftScroll;
    private Button rightScroll;
    private String[] arraySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // load layout
        handleVibrationChecks();
        checkBluetoothConnection();   // show connection pop-up if necessary
        initMembers(); // initialize containers
        initGraph();   // initialize graph
        addListenerToToggleButtons(); // add listeners
    }

    public void handleVibrationChecks(){
        xCheck = (CheckBox) findViewById(R.id.xCheck);
        yCheck = (CheckBox) findViewById(R.id.yCheck);
        zCheck = (CheckBox) findViewById(R.id.zCheck);
        rpmFreqText = (TextView) findViewById(R.id.rpmFreq);
        tireRPMFreqText = (TextView) findViewById(R.id.tireFreq);
        measureButton = (ToggleButton) findViewById(R.id.toggleMeasure);
        vibPause = (ToggleButton) findViewById(R.id.vibrationPause);
        noisePause = (ToggleButton) findViewById(R.id.noisePause);
        scope = (Spinner) findViewById(R.id.zoom);
        rightScroll = (Button) findViewById(R.id.right);
        leftScroll = (Button) findViewById(R.id.left);

        this.arraySpinner = new String[]{
            "100","200","300","400"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arraySpinner);
        scope.setAdapter(adapter);



//        clearButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                graph.removeSeries(xSeries);
//                graph.removeSeries(ySeries);
//                graph.removeSeries(zSeries);
//                graph.removeSeries(x_peaks);
//                graph.removeSeries(y_peaks);
//                graph.removeSeries(z_peaks);
//            }
//        });

        rightScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(xMaxBoundary == 300) {
                    graph.getViewport().setMinX(100);
                    graph.getViewport().setMaxX(400);
                } else if (xMaxBoundary == 200){
                    graph.getViewport().setMinX(200);
                    graph.getViewport().setMaxX(300);
                } else if (xMaxBoundary == 100) {
                    graph.getViewport().setMinX(100);
                    graph.getViewport().setMaxX(200);
                }
            }
        });

        leftScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        vibPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                if(isChecked) {

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
                if(isChecked) {
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
                if(isChecked) {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // NOTE: buttons first set to opposite state than what is retrieved from the settings
        // then a click is perform to change them to the correct state. This is done in order to
        // trigger the onCheckChanged listener, which better handles the functionality.
        noise.setChecked(!SettingsData.isChecked(noise.getTag().toString(), false)); // retrieve previous state
       // noise.performClick(); // update state
        vibration.setChecked(!SettingsData.isChecked(vibration.getTag().toString(), true));
        vibration.performClick();
        // Bluetooth setup
        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        // Start obd thread
        obdData.run();

        setPrefs();
    }

    @Override
    public void onPause() {
        super.onPause();
        rec_acc.onPause(); // stop recordings
        rec_mic.onPause();
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

    protected void setPrefs(){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        settingsPrefs = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        String profile = settingsPrefs.getString("profile", "");
        settingsPrefs.edit().putString("profile", profile).apply();
        prefs = getSharedPreferences(profile, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

        try {
            dev1val = Double.parseDouble(prefs.getString("ratio1", ""));
        }
        catch(NumberFormatException ex) {
            dev1val = 0; // default ??
        }
        try {
            dev2val = Double.parseDouble(prefs.getString("ratio2", ""));
        }
        catch(NumberFormatException ex) {
            dev2val = 0; // default ??
        }
        try {
            dev3val = Double.parseDouble(prefs.getString("ratio3", ""));
        }
        catch(NumberFormatException ex) {
            dev3val = 0; // default ??
        }
        try {
            dev4val = Double.parseDouble(prefs.getString("ratio4", ""));
        }
        catch(NumberFormatException ex) {
            dev4val = 0; // default ??
        }
        try {
            dev5val = Double.parseDouble(prefs.getString("ratio5", ""));
        }
        catch(NumberFormatException ex) {
            dev5val = 0; // default ??
        }
        try {
            dev6val = Double.parseDouble(prefs.getString("ratio6", ""));
        }
        catch(NumberFormatException ex) {
            dev6val = 0; // default ??
        }

    }

    protected void initMembers() { // Initialize member containers
        if (SettingsData.mContext == null) //Check if settings already have context
            settingsData = new SettingsData(getApplicationContext());
        mHandler = new MainHandler(Looper.getMainLooper());
        rec_acc = new Xlo(this, mHandler, acc_samples, acc_dvsr);
        rec_mic = new MicData(mHandler, audio_samples, audio_scaling, normalize, in_dB);

        //We might want to hand this differently in the future
        if (app.getGlobalBluetoothSocket() == null) {
            obdData = new OBDData(mHandler, acc_samples, true);
        } else {
            obdData = new OBDData(mHandler,acc_samples, false);
        }

        accelFFT[0] = new Fft(acc_samples, mHandler, 4, normalize, in_dB);
        accelFFT[1] = new Fft(acc_samples, mHandler, 5, normalize, in_dB);
        accelFFT[2] = new Fft(acc_samples, mHandler, 6, normalize, in_dB);
        Fft.getOmega(audio_omega, audio_Fs);
        Fft.getOmega(accel_omega, acc_Fs);
    }

    protected void initGraph() {
        graph = (GraphView) findViewById(R.id.fftGraph);
        if (graph == null) throw new AssertionError("Object cannot be null");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(xMaxBoundary);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-80);
        graph.getViewport().setMaxY(40);

        //Set Scalable and Zoom
        graph.getViewport().setScalable(true);
        //graph.getViewport().setScrollable(true);

        // Titles
        audioSeries.setTitle("Mic");
        xSeries.setTitle("X");
        ySeries.setTitle("Y");
        zSeries.setTitle("Z");
        obdSeries.setTitle("RPMFreq");
        obdSeriesSpeed.setTitle("TireFreq");
        audio_peaks.setTitle("APeaks");
        x_peaks.setTitle("XPeaks");

        // Colors
        audioSeries.setColor(Color.parseColor("#181907"));
        xSeries.setColor(Color.parseColor("#0B3861"));
        ySeries.setColor(Color.parseColor("#0B6138"));
        zSeries.setColor(Color.parseColor("#610B0B"));
        obdSeries.setColor(Color.parseColor("red"));
        obdSeriesSpeed.setColor(Color.parseColor("blue"));
        audio_peaks.setColor(Color.parseColor("yellow"));
        x_peaks.setColor(Color.parseColor("yellow"));
        y_peaks.setColor(Color.parseColor("yellow"));
        z_peaks.setColor(Color.parseColor("yellow"));

        // Shapes
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

        //puts the line in the graph
        obdSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
               // canvas.drawLine(x-20, y-20, x+20, y+20, paint);
               // canvas.drawLine(x+20, y-20, x-20, y+20, paint);
                canvas.drawLine(x-1,y-500,x+1,y+1000,paint);
            }
        });



        obdSeriesSpeed.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
               // canvas.drawLine(x-20, y-20, x+20, y+20, paint);
               // canvas.drawLine(x+20, y-20, x-20, y+20, paint);
                canvas.drawLine(x,y-500,x,y+1000,paint);
            }
        });

        // Add to graph
        graph.addSeries(audioSeries);
        graph.addSeries(xSeries);
        graph.addSeries(ySeries);
        graph.addSeries(zSeries);
        graph.addSeries(obdSeries);
        graph.addSeries(obdSeriesSpeed);
        graph.addSeries(audio_peaks);

        /*
        graph.addSeries(x_peaks);
        graph.addSeries(y_peaks);
        graph.addSeries(z_peaks);
        */

        setPrefs();
        addDevices();
    }

    void addDevices(){
        device2_series.setTitle(prefs.getString("name2", ""));
        device2_series.setColor(Color.parseColor("green"));

        device3_series.setTitle(prefs.getString("name3", ""));
        device3_series.setColor(Color.parseColor("#FA8258"));

        device4_series.setTitle(prefs.getString("name4", ""));
        device4_series.setColor(Color.parseColor("#AC58FA"));

        device5_series.setTitle(prefs.getString("name5", ""));
        device5_series.setColor(Color.parseColor("#81BEF7"));

        device6_series.setTitle(prefs.getString("name6", ""));
        device6_series.setColor(Color.parseColor("#F781F3"));

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

        if (Boolean.valueOf(prefs.getString("check2", ""))){
            graph.addSeries(device2_series);
        }
        if (Boolean.valueOf(prefs.getString("check3", ""))){
            graph.addSeries(device3_series);
        }
        if (Boolean.valueOf(prefs.getString("check4", ""))){
            graph.addSeries(device4_series);
        }
        if (Boolean.valueOf(prefs.getString("check5", ""))){
            graph.addSeries(device5_series);
        }
        if (Boolean.valueOf(prefs.getString("check6", ""))){
            graph.addSeries(device6_series);
        }
    }

    void addListenerToToggleButtons() {
        noise = (ToggleButton) findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) findViewById(R.id.toggleVibration);
        recordButton = (ToggleButton) findViewById(R.id.startstop);

        noise.setChecked(false);
        noise.setChecked(false);

        noise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                graph.removeSeries(audioSeries);
                graph.removeSeries(audio_peaks);
                if (isChecked) { // enable mic recording
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
                    if (permission) {
                        MicData.isEnabled = true; // enable thread
                        rec_mic.run(); // run recording thread
                        graph.addSeries(audioSeries); // graph results
                        graph.addSeries(audio_peaks);
                    } else {
                        buttonView.setChecked(false); // no permission, undo check
                    }
                } else {
                    rec_mic.onPause();
                    graph.removeSeries(audioSeries);
                    graph.removeSeries(audio_peaks);
                } // store settings (remember checked state)
               // SettingsData.setChecked(buttonView.getTag().toString(), buttonView.isChecked());
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Xlo.isEnabled = isChecked; // enable/disable thread
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if  (isChecked) {
                    graph.removeSeries(xSeries); // remove results from graph
                    graph.removeSeries(ySeries);
                    graph.removeSeries(zSeries);
                    rec_acc.run(); // run thread
                    if(xCheck.isChecked()) {
                        graph.addSeries(xSeries); // graph results
                        graph.addSeries(x_peaks);
                    }

                    if(yCheck.isChecked()) {
                        graph.addSeries(ySeries);
                        graph.addSeries(y_peaks);
                    }

                    if(zCheck.isChecked()) {
                        graph.addSeries(zSeries);
                        graph.addSeries(z_peaks);
                    }
                } else {
                   // rec_acc.onPause(); // stop thread
                    graph.removeSeries(xSeries); // remove results from graph
                   graph.removeSeries(ySeries);
                   graph.removeSeries(zSeries);
                    graph.removeSeries(x_peaks); // remove results from graph
                    graph.removeSeries(y_peaks);
                    graph.removeSeries(z_peaks);
                }
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener(){ //to be implemented
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), prefs.getString("ratio1", ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void checkBluetoothConnection() {

        if (app.getGlobalBluetoothSocket() == null) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
            LayoutInflater adbInflater = LayoutInflater.from(this);
            View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
            dontShowAgain = (CheckBox)eulaLayout.findViewById(R.id.skip);
            dontShowAgain.setChecked(SettingsData.isChecked(dontShowAgain.getTag().toString(), false));
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

            if (!dontShowAgain.isChecked()){
                //less annoying popup for now
                Toast.makeText(this, "bluetooth", Toast.LENGTH_SHORT).show();
                //alertDialog.show();
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
        }
        //Return false to allow normal menu processing to proceed,
        //true to consume it here.
        return false;
    }

    //private variable
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
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

    // The part of the function that help detect peaks
    public DataPoint[] findPeaks(DataPoint[] data){

        int max = 1;
        int numPeaks = 0 ;
        int[] indexes = new int[(data.length + 1)/2];
        DataPoint[] peaks;
        // Checking for the conditions of the peaks
        // if the change is ocurring, then graph.
        for(int i = 1; i < data.length; i++){
            while(i < data.length && data[i].getY() < data[i-1].getY()){
                i++;
            }
            while(i < data.length && data[i].getY() > data[i-1].getY()){
                max = i++;
            }
            if(i < indexes.length && data[max].getY() > peakThresh)
                indexes[numPeaks++]= max;
        }

        if(numPeaks > 0)
            peaks = new DataPoint[numPeaks];
        else
            peaks = new DataPoint[]{};
        //placing the peaks in the coordinates into peaks so we can see where they are
        for(int i = 0; i < numPeaks; i++)
            peaks[i] = data[indexes[i]];

    // make diplayable variable to show us the interval =audio_dps[1] - audio_dps[2];
        return peaks;
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


                        // Void intervalSpacing{

                        //if(audio_dps[i] > audio_dps[i+1])
                          //  aps.appendData(audio_dps[i],false, audio_dps.length);

                        // make diplayable variable to show us the interval =audio_dps[1] - audio_dps[2];

                    }
                    audioSeries.resetData(audio_dps);
                    audio_peaks.resetData(findPeaks(audio_dps));
                    break;

                }
                case 3: // Accelerometer data is ready
                {
                    // begin fft
                    accelFFT[0].run(Xlo.xAcc, "x");
                    accelFFT[1].run(Xlo.yAcc, "y");
                    accelFFT[2].run(Xlo.zAcc, "z");
                    break;
                }
                case 4: // Accelerometer x fft is complete
                {
                    // add to series
                    accel_resultX = (double[]) msg.obj;
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        accel_dpsX[j++] = new DataPoint(accel_omega[i], accel_resultX[i]);
                    }
                    xSeries.resetData(accel_dpsX);
                    x_peaks.resetData(findPeaks(accel_dpsX));
                    break;
                }
                case 5: // Accelerometer y fft complete
                {
                    // add to series
                    accel_resultY = (double[]) msg.obj;
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        accel_dpsY[j++] = new DataPoint(accel_omega[i], accel_resultY[i]);
                    }
                    ySeries.resetData(accel_dpsY);
                    y_peaks.resetData(findPeaks(accel_dpsY));
                    break;
                }
                case 6: // Accelerometer z fft complete
                {
                    //add to series
                    accel_resultZ = (double[]) msg.obj;
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        accel_dpsZ[j++] = new DataPoint(accel_omega[i], accel_resultZ[i]);
                    }
                    zSeries.resetData(accel_dpsZ);
                    z_peaks.resetData(findPeaks(accel_dpsZ));
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
                    obdSeries.resetData(rpm_dps);

                    DecimalFormat df = new DecimalFormat("#.##");

                    rpmFreqText.setText("RPM/Freq: "+ df.format(obd_result[0]));

//                    /*TESTING*/
//                    obd_result = (double[]) msg.obj;
//                    device2_dps[0] = new DataPoint(dev1val*obd_result[0]/dev2val,0);
//                    device2_series.resetData(device2_dps);
//                    /*TESTING*/
                    /*TESTING*/
                    obd_result = (double[]) msg.obj;
                    device2_dps[0] = new DataPoint(dev1val*obd_result[0]/dev2val,0);
                    device2_series.resetData(device2_dps);

                    obd_result = (double[]) msg.obj;
                    device3_dps[0] = new DataPoint(dev1val*obd_result[0]/dev3val,0);
                    device3_series.resetData(device3_dps);

                    obd_result = (double[]) msg.obj;
                    device4_dps[0] = new DataPoint(dev1val*obd_result[0]/dev4val,0);
                    device4_series.resetData(device4_dps);

                    obd_result = (double[]) msg.obj;
                    device5_dps[0] = new DataPoint(dev1val*obd_result[0]/dev5val,0);
                    device5_series.resetData(device5_dps);

                    obd_result = (double[]) msg.obj;
                    device6_dps[0] = new DataPoint(dev1val*obd_result[0]/dev6val,0);
                    device6_series.resetData(device6_dps);
                    /*TESTING*/

                    //Tire RPM Frequency
                    tire_dps[0] = new DataPoint(obd_result[1],0);
                    obdSeriesSpeed.resetData(tire_dps);
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
    
    private int getFreqIndexCeil (double frequency, double freq_step) {
        return (int)( Math.ceil(freq_step * frequency) );
    }

    private int getFreqIndexFloor (double frequency, double freq_step) {
        return (int)( Math.floor(freq_step * frequency) );
    }

    private double interpolateMagnitude(double frequency, double freq_step, DataPoint[] datapoints)
    {
        int ceil = getFreqIndexCeil(frequency, freq_step);
        int floor = getFreqIndexFloor(frequency, freq_step);
        double slope = (datapoints[ceil].getY() - datapoints[floor].getY()) / (datapoints[ceil].getX() - datapoints[floor].getX());
        double mag = datapoints[floor].getY() + slope * (frequency - datapoints[floor].getX());
        return mag;
    }

    private HashMap<String, Integer> count_occurrence(DataPoint[] peaks) {
        double val = 0;
        String sval;
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (int i = 0; i < peaks.length; i++) {
            for (int j = i + 1; j < peaks.length; j++) {
                val = peaks[j].getX() - peaks[i].getX();
                sval = String.format("%.2f", val);
                occurrences.put(sval, occurrences.get(sval) + 1);
            }
        }
        return occurrences;
    }

}