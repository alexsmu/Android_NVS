package byuie499.auto_nvs;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.BassBoost;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class MainActivity extends AppCompatActivity{
    private static final int graph_x_axis_end = 500;
    private static final int audio_samples = 8192;
    private static final double audio_Fs = 44100;
    private static final int audio_numdps = (int)(Math.ceil(audio_samples * graph_x_axis_end / audio_Fs) );
    private static final int audio_startdps = audio_samples / 2;
    private static final int audio_enddps = audio_startdps + audio_numdps;
    private static final int acc_samples = 256;
    private static final double acc_Fs = 1000;
    private static final int acc_numdps = (int) (Math.ceil(acc_samples * graph_x_axis_end / acc_Fs) );
    private static final int acc_startdps = acc_samples / 2;
    private static final int acc_enddps = acc_startdps + acc_numdps;
    private ToggleButton recordButton;
    private double[] audio_omega = new double[audio_samples];
    private double[] accel_omega = new double[acc_samples];
    private Fft[] accelFFT = new Fft[3];
    private MicData rec_mic = null;
    private Xlo rec_acc = null;
    private LineGraphSeries<DataPoint> audioSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> xSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> ySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> zSeries = new LineGraphSeries<>();
    private PointsGraphSeries<DataPoint> obdSeries = new PointsGraphSeries<>();
    private PointsGraphSeries<DataPoint> obdSeriesSpeed = new PointsGraphSeries<>();
    private GraphView graph = null;
    private SettingsData settingsData = null;
    public Handler mHandler = null;
    public OBDData obdData;
    MyApplication app;
    public CheckBox dontShowAgain;
    private ToggleButton noise, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Disconnected</font><small>"));
        initMembers();
        initGraph();
        addListenerToToggleButtons();
        checkBluetoothConnection();
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
        noise.setChecked(SettingsData.isChecked(noise.getTag().toString(), false));
        MicData.isEnabled = noise.isChecked();
        vibration.setChecked(SettingsData.isChecked(vibration.getTag().toString(), true));
        Xlo.isEnabled = vibration.isChecked();
        // Bluetooth setup
        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        // Start threads
        rec_mic.run();
        rec_acc.run();
        obdData.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        rec_acc.onPause();
        rec_mic.onPause();
        unregisterReceiver(mReceiver);
    }


    protected void initMembers() {
        if (SettingsData.mContext == null)
            settingsData = new SettingsData(getApplicationContext());
        mHandler = new MainHandler(Looper.getMainLooper());
        rec_acc = new Xlo(this, mHandler, acc_samples, 2);
        obdData = new OBDData(mHandler,acc_samples,true);
        rec_mic = new MicData(mHandler, audio_samples, 4.0, true);
        accelFFT[0] = new Fft(acc_samples, mHandler, 4);
        accelFFT[1] = new Fft(acc_samples, mHandler, 5);
        accelFFT[2] = new Fft(acc_samples, mHandler, 6);
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
        graph.getViewport().setMaxX(graph_x_axis_end);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-80);
        graph.getViewport().setMaxY(40);

        audioSeries.setTitle("Mic");
        audioSeries.setColor(Color.parseColor("#181907"));

        xSeries.setTitle("X");
        ySeries.setTitle("Y");
        zSeries.setTitle("Z");
        obdSeries.setTitle("RPMFreq");
        obdSeriesSpeed.setTitle("TireFreq");

        xSeries.setColor(Color.parseColor("#0B3861"));
        ySeries.setColor(Color.parseColor("#0B6138"));
        zSeries.setColor(Color.parseColor("#610B0B"));
        obdSeries.setColor(Color.parseColor("red"));
        obdSeriesSpeed.setColor(Color.parseColor("blue"));

        //puts the line in the graph
        obdSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                canvas.drawLine(x-20, y-20, x+20, y+20, paint);
                canvas.drawLine(x+20, y-20, x-20, y+20, paint);
                canvas.drawLine(x-1,y-250,x+1,y+500,paint);
            }
        });

        obdSeriesSpeed.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(10);
                canvas.drawLine(x-20, y-20, x+20, y+20, paint);
                canvas.drawLine(x+20, y-20, x-20, y+20, paint);
                canvas.drawLine(x-1,y-250,x+1,y+500,paint);
            }
        });
        graph.addSeries(audioSeries);
        graph.addSeries(xSeries);
        graph.addSeries(ySeries);
        graph.addSeries(zSeries);
        graph.addSeries(obdSeries);
        graph.addSeries(obdSeriesSpeed);
    }

    void addListenerToToggleButtons() {
        noise = (ToggleButton) findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) findViewById(R.id.toggleVibration);
        recordButton = (ToggleButton) findViewById(R.id.startstop);

        noise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MicData.isEnabled = isChecked;
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked);
                if (isChecked){
                    rec_mic.run();
                    graph.addSeries(audioSeries);
                } else {
                    rec_mic.onPause();
                    graph.removeSeries(audioSeries);
                }
            }
        });

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Xlo.isEnabled = isChecked;
                SettingsData.setChecked(buttonView.getTag().toString(), isChecked);
                if  (isChecked) {
                    rec_acc.run();
                    graph.addSeries(xSeries);
                    graph.addSeries(ySeries);
                    graph.addSeries(zSeries);
                } else {
                    rec_acc.onPause();
                    graph.removeSeries(xSeries);
                    graph.removeSeries(ySeries);
                    graph.removeSeries(zSeries);
                }
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
    }

    void checkBluetoothConnection() {

        app = new MyApplication();

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

                }
            });
            alertDialog.setPositiveButton("Bluetooth Settings", new android.app.AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(intent);
                }
            });

            if (!dontShowAgain.isChecked())
                alertDialog.show();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        switch(item.getItemId()){
            case R.id.one:
                Intent intent1 = new Intent(this, PopUpRatios.class);
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

    private class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 2: // Audio fft is complete
                {
                    // add to series
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[audio_numdps];
                    int j = 0;
                    for (int i = audio_startdps; i < audio_enddps; ++i) {
                        dps[j++] = new DataPoint(audio_omega[i], result[i]);
                    }
                    audioSeries.resetData(dps);
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
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[acc_numdps];
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        dps[j++] = new DataPoint(accel_omega[i], result[i]);
                    }
                    xSeries.resetData(dps);
                    break;
                }
                case 5: // Accelerometer y fft complete
                {
                    // add to series
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[acc_numdps];
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        dps[j++] = new DataPoint(accel_omega[i], result[i]);
                    }
                    ySeries.resetData(dps);
                    break;
                }
                case 6: // Accelerometer z fft complete
                {
                    //add to series
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[acc_numdps];
                    int j = 0;
                    for (int i = acc_startdps; i < acc_enddps; ++i) {
                        dps[j++] = new DataPoint(accel_omega[i], result[i]);
                    }
                    zSeries.resetData(dps);
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
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[1];
                    dps[0] = new DataPoint(result[0],0);
                    obdSeries.resetData(dps);

                    //Tire RPM Frequency
                    DataPoint[] dps2 = new DataPoint[1];
                    dps2[0] = new DataPoint(result[1],0);
                    obdSeriesSpeed.resetData(dps2);

                    break;
                }
                default:
                {
                    super.handleMessage(msg);
                }
            }
        }
    }
}