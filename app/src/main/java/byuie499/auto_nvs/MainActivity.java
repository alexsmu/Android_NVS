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
import android.net.Uri;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static int graph_x_axis_end = 200;  // graph x axis domain limit
    private static int graph_x_axis_end1 = 200;  // graph x axis domain limit
    private static int graph_y_axis_end = 20;
    private static final int audio_samples = 32768;    // samples to record before taking fft (must be power of 2)
    private static final double audio_Fs = 44100;     // audio sampling rate. (DO NOT MODIFY)
    private static final int audio_numdps = (int)(Math.ceil(audio_samples * graph_x_axis_end / audio_Fs) ); // number of audio graph datapoints
    private static final int audio_startdps = audio_samples / 2; // starting index (corresponds to 0 hz)
    private static final int audio_enddps = audio_startdps + audio_numdps; // ending index (corresponds to x_axis_end hz)
    private static final int audio_enddps_occ = audio_startdps + (int) Math.ceil(audio_samples * 1000 / audio_Fs) ; // in prep for better audio occurrence finding.
    private static final int acc_samples = 128; // accelerometer samples
    private static boolean normalizeDB;
    private static double audio_scaling_normalizeDB = 4.0;
    private static double audio_scaling = 0.01;
    private static int acc_numdps; // number of acc graph datapoints
    private static int acc_startdps; // starting index (corresponds to 0 hz)
    private static int acc_enddps; // ending index (corresponds to x_axis_end hz)
    private static DataPoint[] audio_dps = new DataPoint[audio_numdps];
    private static DataPoint[] xlo_dps;
    private static DataPoint[] empty_dps = new DataPoint[] { new DataPoint(-80,-80)};
    private static Correlation xlo_correlation = new Correlation();
    private static Correlation audio_correlation = new Correlation();
    private static double[] audio_result = null;
    private static double[] xlo_result = null;
    private static double[] obd_result = null;
    private double mic_max;
    private double xlo_max;
    private static boolean permission = false; // RECORD_AUDIO permission granted?
    private static boolean storage_permission = false; // WRITE_EXTERNAL_STORAGE permission granted?
    private double[] audio_omega = new double[audio_samples]; // omega container for audio FFT
    private double[] accel_omega = new double[acc_samples];   // omega container for accel FFT
    private Fft accelFFT; // containers for each accelerometer axis FFT results
    private MicData rec_mic = null;      // container for audio recording thread object
    private Xlo rec_acc = null;          // container for accelerometer recording thread object
    private LineGraphSeries<DataPoint> audioSeries = new LineGraphSeries<>();       // graph series
    private LineGraphSeries<DataPoint> xloSeries = new LineGraphSeries<>();
    private PointsGraphSeries<DataPoint> accel_device2_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> accel_device3_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> accel_device4_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_device2_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_device3_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_device4_series = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> accel_rpmPeaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_rpmPeaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> accel_tirePeaks = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> audio_tirePeaks = new PointsGraphSeries<>();
    private GraphView graph = null; // container for graph object
    private SettingsData settingsData = null; // dummy container to initialize SettingsData for the current context
    public Handler mHandler = null;  // container for thread handler
    public OBDData obdData;         // container for OBD recording thread object
    MyApplication app = new MyApplication();              // required for bluetooth socket
    public CheckBox dontShowAgain;  // don't show again (bluetooth connection expected) checkbox
    private ToggleButton noise, vibration; // containers for layout buttons
    private Button screenShot;
    private double dev1val;
    private double dev2val;
    private double dev3val;
    private double dev4val;
    private static boolean check1, check2, check3, check4, check7;
    private TextView rpmFreqText;
    private TextView tireRPMFreqText;
    private ToggleButton graphPause;
    private ToggleButton vibCheck;
    private ToggleButton micCheck;
    private BroadcastReceiver mReceiver;
    private Correlation correlate;
    private DataPoint[] a_peaks;
    private DataPoint[] x_peaks;

    //These Varibales are for the tutorial
    private ShowcaseView showcaseView;
    private int counter = 0;
    private Target t1, t2, t3, t4, t5, t6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // load layout
        settingsData = new SettingsData(getApplicationContext());
        setBluetoothReceiver();
        initMembers(); // initialize containers
        initGraph();   // initialize graph

        t1 = new ViewTarget(R.id.toggleVibration, this);
        t2 = new ViewTarget(R.id.vibCheck, this);
        t3 = new ViewTarget(R.id.graphPause, this);
        t4 = new ViewTarget(R.id.screenShot, this);
        t5 = new ViewTarget(R.id.micCheck, this);
        t6 = new ViewTarget(R.id.toggleNoise, this);

        if(SettingsData.isFirstRun()) {
            disableClicks();
            counter = 0;
            showcaseView = new ShowcaseView.Builder(this, true)
                    .withMaterialShowcase()
                    .doNotBlockTouches()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(Target.NONE)
                    .setOnClickListener(showcaseClickListener)
                    .setContentTitle("Tutorial")
                    .setContentText("In the Center you'll find a graph. This will contain the Information " +
                            "on the FFT for the noise and vibration.\n" +
                            "Red marks on peaks represent potential pulses.\n" +
                            "Engine RPM peaks are marked the order followed by the letter E (e.g. 2E).\n" +
                            "If a tire diameter is specified, Speed peaks are marked like RPM peaks but with the letter S\n" +
                            "If crankshaft diameter is provided, any enabled ratios are marked with the letter F, G, and H respectively.")
                    .build();
            showcaseView.setButtonText("next");
            SettingsData.setFirstRun(false);
        }
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
        }else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        normalizeDB = SettingsData.isChecked(SettingsData.currentProfile + "_check8", false);

        if ( normalizeDB )
            graph.getViewport().setMinY(-80);
        else
            graph.getViewport().setMinY(0);

        Correlation.peakThresh = (normalizeDB ? -55 : 0.5);
        Fft.db = normalizeDB;
        Fft.norm = normalizeDB;
        Xlo.axis = SettingsData.getInt(SettingsData.currentProfile + "_accelOpt", 3);
        Xlo.pseudoSampling = SettingsData.isChecked(SettingsData.currentProfile + "_check5", false);
        MicData.scale = (normalizeDB ? audio_scaling_normalizeDB : audio_scaling);

        setPrefs();
        addDeviceSeries();
        checkBluetoothConnection();   // show connection pop-up if necessary
        check_record_permissions();
        check_storage_permissions();
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
        switch(requestCode) {
            case 1:
                permission = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case 2:
                storage_permission = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
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

    protected void check_storage_permissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { // check for permission
            storage_permission = false;
            ActivityCompat.requestPermissions(MainActivity.this, // request permission
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        } else { // we have permission
            storage_permission = true;
        }
    }

    public void handleVibrationChecks(){
        vibCheck.setChecked(SettingsData.isChecked(vibCheck.getTag().toString(), true));
        micCheck.setChecked(SettingsData.isChecked(micCheck.getTag().toString(), true));
        graphPause.setChecked(SettingsData.isChecked(graphPause.getTag().toString(), true));

        if (vibCheck.isChecked()) {
            graph.addSeries(xloSeries);
        }
        if (micCheck.isChecked()) {
            graph.addSeries(audioSeries);
        }

        graphPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if (isChecked){
                    rec_acc.onPause();
                    rec_mic.onPause();
                } else {
                    rec_acc.run();
                    rec_mic.run();
                }
            }
        });

        vibCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if(isChecked) {
                    graph.addSeries(xloSeries);
                }
                else
                {
                    graph.removeSeries(xloSeries);
                    updateXloCorrelation(x_peaks);
                }
            }
        });

        micCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked); // store state
                if(isChecked) {
                    graph.addSeries(audioSeries);
                }
                else
                {
                    graph.removeSeries(audioSeries);
                    updateMicCorrelation(a_peaks);
                }
            }
        });
    }

    protected void setPrefs(){
        dev1val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio1", "0"));
        dev2val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio2", "0"));
        dev3val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio3", "0"));
        dev4val = Double.parseDouble(SettingsData.getString(SettingsData.currentProfile + "_ratio4", "0"));
        check1 = SettingsData.isChecked(SettingsData.currentProfile + "_check1", true);
        check2 = SettingsData.isChecked(SettingsData.currentProfile + "_check2", false);
        check3 = SettingsData.isChecked(SettingsData.currentProfile + "_check3", false);
        check4 = SettingsData.isChecked(SettingsData.currentProfile + "_check4", false);
        check7 = SettingsData.isChecked(SettingsData.currentProfile + "_check7", false);
    }

    protected void initMembers() { // Initialize member containers
        mHandler = new MainHandler(Looper.getMainLooper());
        rec_acc = new Xlo(this, mHandler, acc_samples);
        correlate = new Correlation();
        accelFFT = new Fft(acc_samples, mHandler, 2);
        rec_mic = new MicData(mHandler, audio_samples);
        vibCheck = (ToggleButton) findViewById(R.id.vibCheck);
        micCheck = (ToggleButton) findViewById(R.id.micCheck);
        rpmFreqText = (TextView) findViewById(R.id.rpmFreq);
        tireRPMFreqText = (TextView) findViewById(R.id.tireFreq);
        graphPause = (ToggleButton) findViewById(R.id.graphPause);
        noise = (ToggleButton) findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) findViewById(R.id.toggleVibration);
        screenShot = (Button) findViewById(R.id.screenShot);

        //We might want to hand this differently in the future
        if (app.getGlobalBluetoothSocket() == null) {
            obdData = new OBDData(mHandler, getApplicationContext(), acc_samples, true);
        } else {
            obdData = new OBDData(mHandler,getApplicationContext(), acc_samples, false);
        }
        Fft.getOmega(audio_omega, audio_Fs);
    }

    protected void initGraph() {
        graph = (GraphView) findViewById(R.id.fftGraph);
        if (graph == null) throw new AssertionError("Object cannot be null");

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(graph_x_axis_end);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(graph_y_axis_end);

        // Titles
        audioSeries.setTitle("Audio");
        xloSeries.setTitle("Accel");

        // Colors

        audioSeries.setColor(Color.parseColor("#0B6138"));
        xloSeries.setColor(Color.parseColor("#610B0B"));
        accel_rpmPeaks.setColor(Color.parseColor("blue"));
        audio_rpmPeaks.setColor(Color.parseColor("blue"));
        accel_tirePeaks.setColor(Color.parseColor("blue"));
        audio_tirePeaks.setColor(Color.parseColor("blue"));

        // Shapes
        accel_rpmPeaks.setCustomShape(correlationShape);
        audio_rpmPeaks.setCustomShape(correlationShape);
        accel_tirePeaks.setCustomShape(correlationShape);
        audio_tirePeaks.setCustomShape(correlationShape);

        accel_device2_series.setColor(Color.parseColor("#003366"));
        accel_device3_series.setColor(Color.parseColor("#00aeef"));
        accel_device4_series.setColor(Color.parseColor("#8eca40"));

        accel_device2_series.setCustomShape(correlationShape);
        accel_device3_series.setCustomShape(correlationShape);
        accel_device4_series.setCustomShape(correlationShape);

        audio_device2_series.setColor(Color.parseColor("#003366"));
        audio_device3_series.setColor(Color.parseColor("#00aeef"));
        audio_device4_series.setColor(Color.parseColor("#8eca40"));

        audio_device2_series.setCustomShape(correlationShape);
        audio_device3_series.setCustomShape(correlationShape);
        audio_device4_series.setCustomShape(correlationShape);

        audio_rpmPeaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Audio Peak: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        audio_tirePeaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Audio Peak: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        accel_rpmPeaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Accel Peak: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        accel_tirePeaks.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(MainActivity.this, "Accel Peak: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.MIDDLE);
    }

    void addDeviceSeries(){
        graph.addSeries(accel_rpmPeaks);
        graph.addSeries(accel_tirePeaks);
        graph.addSeries(audio_rpmPeaks);
        graph.addSeries(audio_tirePeaks);
        graph.addSeries(accel_device2_series);
        graph.addSeries(accel_device3_series);
        graph.addSeries(accel_device4_series);
        graph.addSeries(audio_device2_series);
        graph.addSeries(audio_device3_series);
        graph.addSeries(audio_device4_series);
    }

    public PointsGraphSeries.CustomShape correlationShape = new PointsGraphSeries.CustomShape() {
        @Override
        public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
            Paint p = new Paint(paint);
            if (dataPoint.isOcc())
                p.setColor(Color.parseColor("red"));
            paint.setStrokeWidth(15);
            canvas.drawCircle(x, y, 15, p);
            paint.setTextSize(36);
            canvas.drawText(dataPoint.getTag(), x+10, y-15, p);
        }
    };

    void addListenerToToggleButtons() {
        noise.setChecked(SettingsData.isChecked(noise.getTag().toString(), false)); // retrieve previous state
        if (noise.isChecked()) { // enable mic recording
            if (permission) {
                MicData.isEnabled = true; // enable thread
                if (!SettingsData.isChecked(graphPause.getTag().toString(), false))
                    rec_mic.run(); // run recording thread
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
            if (!SettingsData.isChecked(graphPause.getTag().toString(), false))
                rec_acc.run();
        } else {
            Xlo.isEnabled = false;
            rec_acc.onPause();
        }

        noise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // enable mic recording
                    check_record_permissions();
                    if (permission) {
                        MicData.isEnabled = true; // enable thread
                        if (!graphPause.isChecked())
                            rec_mic.run(); // run recording thread
                    } else {
                        buttonView.setChecked(false); // no permission, undo check
                    }
                } else {
                    MicData.isEnabled = false;
                    rec_mic.onPause();
                } // store settings (remember checked state)
                SettingsData.setChecked(buttonView.getTag().toString(), buttonView.isChecked());
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Xlo.isEnabled = isChecked; // enable/disable thread
                if  (isChecked) {
                    if (!graphPause.isChecked())
                        rec_acc.run(); // run thread
                } else {
                    rec_acc.onPause(); // stop thread
                }
                SettingsData.setChecked(buttonView.getTag().toString(), buttonView.isChecked()); // store state
            }
        });

        screenShot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_storage_permissions();
                if (storage_permission) {
                    try {
                        File imageFile;

                        Date now = new Date();
                        String tnow = android.text.format.DateFormat.format("yyyy-MM-dd_hh_mm_ss", now).toString();

                        // image naming and path  to include sd card  appending name you choose for file
                        String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Screenshots/Screenshot_" + tnow + ".png";
                        Log.d("SAVING SCREENSHOT", mPath + "\n\n\n -------------------------");
                        imageFile = new File(mPath);

                        // create bitmap screen capture
                        View v1 = getWindow().getDecorView().getRootView();
                        v1.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                        v1.setDrawingCacheEnabled(false);

                        FileOutputStream outputStream = new FileOutputStream(imageFile);
                        int quality = 100;
                        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(imageFile);
                        mediaScanIntent.setData(contentUri);
                        sendBroadcast(mediaScanIntent);
                        Toast.makeText(getApplicationContext(), "Screenshot saved to Gallery.", Toast.LENGTH_SHORT).show();
                    } catch (Throwable e) {
                        // Several error may come out with file handling or OOM
                        Toast.makeText(getApplicationContext(), "Error in saving screenshot.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No permission to save", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                SettingsData.setChecked(dontShowAgain.getTag().toString(), true);
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
                dontShowAgain.setChecked(true);
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
                counter = 0;
                disableClicks();
                showcaseView = new ShowcaseView.Builder(this, true)
                        .withMaterialShowcase()
                        .doNotBlockTouches()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setTarget(Target.NONE)
                        .setOnClickListener(showcaseClickListener)
                        .setContentTitle("Tutorial")
                        .setContentText("In the Center you'll find a graph. This will contain the Information " +
                                "on the FFT for the noise and vibration.\n" +
                                "Red marks on peaks represent potential pulses.\n" +
                                "Engine RPM peaks are marked the order followed by the letter E (e.g. 2E).\n" +
                                "If a tire diameter is specified, Speed peaks are marked like RPM peaks but with the letter S.\n" +
                                "If crankshaft diameter is provided, any enabled ratios are marked with the letter F, G, and H respectively.")
                        .build();
                showcaseView.setButtonText("next");
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

    // separated handler as a class, so the code is more readable, and less things clog onCreate
    private class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0: // Audio fft is complete
                {
                    if (!Xlo.isRunning && !vibCheck.isChecked()) {
                        graph.getViewport().setMaxX(graph_x_axis_end1);
                    }
                    // add to series
                    audio_result = (double[]) msg.obj;
                    int j = 0;
                    mic_max = -80;
                    for (int i = audio_startdps; i < audio_enddps; ++i) {
                        audio_dps[j++] = new DataPoint(audio_omega[i], audio_result[i]);
                        if (audio_result[i] > mic_max)
                            mic_max = audio_result[i];
                    }
                    if (!vibCheck.isChecked() || mic_max > xlo_max)
                        graph.getViewport().setMaxY(mic_max + 1);
                    a_peaks = correlate.findPeaks(audio_dps);
                    try {
                        audioSeries.resetData(audio_dps);
                        updateMicCorrelation(a_peaks);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 1: // Accelerometer data is ready
                {
                    // begin fft
                    if (vibCheck.isChecked()) {
                        Fft.getOmega(accel_omega, Xlo.avg_sample_Fs);
                        graph_x_axis_end = (int) Math.ceil(Xlo.avg_sample_Fs / 2);
                        accelFFT.run(Xlo.rAcc, "Accel");
                    }
                    break;
                }
                case 2: // Accelerometer fft is complete
                {
                    if (vibCheck.isChecked()) {
                        // add to series
                        xlo_result = (double[]) msg.obj;
                        int j = 0;
                        graph.getViewport().setMaxX(graph_x_axis_end);
                        acc_numdps = (int) (Math.ceil(acc_samples * graph_x_axis_end / Xlo.avg_sample_Fs) );
                        acc_startdps = acc_samples / 2;
                        if (acc_numdps > (acc_samples / 2));
                            acc_numdps = acc_samples / 2;
                        acc_enddps = acc_startdps + acc_numdps;
                        xlo_dps = new DataPoint[acc_numdps];
                        xlo_max = -80;
                        for (int i = acc_startdps; i < acc_enddps; ++i) {
                            xlo_dps[j++] = new DataPoint(accel_omega[i], xlo_result[i]);
                            if (xlo_result[i] > xlo_max)
                                xlo_max = xlo_result[i];
                        }
                        if (!vibCheck.isChecked() || mic_max < xlo_max )
                            graph.getViewport().setMaxY(xlo_max + 1);
                        x_peaks = correlate.findPeaks(xlo_dps);

                        try {
                            xloSeries.resetData(xlo_dps);
                            updateXloCorrelation(x_peaks);
                        } catch (Exception ex) {
                            //do nothing for now
                        }
                    }
                    break;
                }
                case 3: // OBD data is ready
                {
                    //OBD Data (it is test data for now)
                    obd_result = (double[]) msg.obj;
                    DecimalFormat df = new DecimalFormat("#.##");

                    rpmFreqText.setText("RPM/Freq: "+ df.format(obd_result[0]));

                    //Tire RPM Frequency
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

    public void updateXloCorrelation(DataPoint[] peaks) {
        accel_rpmPeaks.resetData(empty_dps);
        accel_device2_series.resetData(empty_dps);
        accel_device3_series.resetData(empty_dps);
        accel_device4_series.resetData(empty_dps);
        accel_tirePeaks.resetData(empty_dps);
        if (OBDData.isRunning && vibCheck.isChecked()) {
            xlo_correlation.count_occurrence(peaks);
            if (check1 && obd_result[0] > 0) {
                accel_rpmPeaks.resetData(xlo_correlation.markPeaks(peaks, obd_result[0], "E"));
                if (check2 && dev2val > 0) {
                    accel_device2_series.resetData(xlo_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev2val, "F"));
                }
                if (check3 && dev3val > 0) {
                    accel_device3_series.resetData(xlo_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev3val, "G"));
                }
                if (check4 && dev4val > 0) {
                    accel_device4_series.resetData(xlo_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev4val, "H"));
                }
            }
            if (check7 && obd_result[1] == obd_result[1] && obd_result[1] > 0) {
                accel_tirePeaks.resetData(xlo_correlation.markPeaks(peaks, obd_result[1], "S"));
            }
        }
    }

    public void updateMicCorrelation(DataPoint[] peaks) {
        audio_rpmPeaks.resetData(empty_dps);
        audio_device2_series.resetData(empty_dps);
        audio_device3_series.resetData(empty_dps);
        audio_device4_series.resetData(empty_dps);
        audio_tirePeaks.resetData(empty_dps);
        if (OBDData.isRunning && micCheck.isChecked()) {
            audio_correlation.count_occurrence(peaks);
            if (check1 && obd_result[0] > 0) {
                audio_rpmPeaks.resetData(audio_correlation.markPeaks(peaks, obd_result[0], "E"));
                if (check2 && dev2val > 0) {
                    audio_device2_series.resetData(audio_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev2val, "F"));
                }
                if (check3 && dev3val > 0) {
                    audio_device3_series.resetData(audio_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev3val, "G"));
                }
                if (check4 && dev4val > 0) {
                    audio_device4_series.resetData(audio_correlation.markPeaks(peaks, dev1val*obd_result[0]/dev4val, "H"));
                }
            }
            if (check7 && obd_result[1] == obd_result[1] && obd_result[1] > 0) {
                audio_tirePeaks.resetData(audio_correlation.markPeaks(peaks, obd_result[1], "S"));
            }
        }
    }

    public View.OnClickListener showcaseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disableClicks();
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // This aligns button to the bottom left side of screen
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            // Set margins to the button, we add 16dp margins here
            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            lps.setMargins(margin, margin, margin, margin);
            switch(counter){
                case 0:
                    showcaseView.setShowcase(t1, true);
                    showcaseView.setContentTitle("Vibration");
                    showcaseView.setContentText("Pressing the icon enables/disables accelerometer");
                    enableClick(R.id.toggleVibration);
                    break;
                case 1:
                    showcaseView.setShowcase(t2, true);
                    showcaseView.setContentTitle("Visibility");
                    showcaseView.setContentText("Pressing the icon shows/hides vibration plot");
                    enableClick(R.id.vibCheck);
                    break;
                case 2:
                    showcaseView.setShowcase(t3, true);
                    showcaseView.setContentTitle("Updates");
                    showcaseView.setContentText("Pressing the icon resumes/pauses plot updates");
                    enableClick(R.id.graphPause);
                    break;
                case 3:
                    showcaseView.setShowcase(t4, true);
                    showcaseView.setContentTitle("Screenshot");
                    showcaseView.setContentText("Pressing the icon takes screenshot");
                    showcaseView.setButtonPosition(lps);
                    enableClick(R.id.screenShot);
                    break;
                case 4:
                    showcaseView.setShowcase(t5, true);
                    showcaseView.setContentTitle("Visibility");
                    showcaseView.setContentText("Pressing the icon shows/hides noise plot");
                    enableClick(R.id.micCheck);
                    break;
                case 5:
                    showcaseView.setShowcase(t6, true);
                    showcaseView.setContentTitle("Noise");
                    showcaseView.setContentText("Pressing the icon enables/disables microphone");
                    showcaseView.setButtonText("Close");
                    enableClick(R.id.toggleNoise);
                    break;
                case 6:
                    showcaseView.hide();
                    enableClicks();
                    break;

            }
            counter++;
        }
    };

    public void disableClicks() {
        try {
            graphPause.setClickable(false);
            vibCheck.setClickable(false);
            micCheck.setClickable(false);
            noise.setClickable(false);
            vibration.setClickable(false);
            screenShot.setClickable(false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void enableClicks() {
        try {
            graphPause.setClickable(true);
            vibCheck.setClickable(true);
            micCheck.setClickable(true);
            noise.setClickable(true);
            vibration.setClickable(true);
            screenShot.setClickable(true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void enableClick(int id) {
        try {
            this.findViewById(id).setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}