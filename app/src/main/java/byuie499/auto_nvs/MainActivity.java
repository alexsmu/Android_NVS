package byuie499.auto_nvs;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    View mMain = null;
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
    private TextView textTitle;
    private Spinner fileSpinner;
    private SeekBar fftseekBar;
    private double[] audio_omega = new double[audio_samples];
    private double[] accel_omega = new double[acc_samples];
    private Fft[] accelFFT = new Fft[3];
    private MicData rec_mic = null;
    private Xlo rec_acc = null;
    private LineGraphSeries<DataPoint> audioSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> xSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> ySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> zSeries = new LineGraphSeries<>();
    private GraphView graph = null;
    public Handler mHandler = null;
    public OBDConnection obdConnection;
    MyApplication app;
    public CheckBox dontShowAgain;
    public static final String PREFS_NAME = "MyPrefsFile1";
    private ToggleButton noise, vibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListenerToToggleButtons();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#FF0000' >Bluetooth Disconnected</font><small>"));
        }

        checkBluetoothConnection();

        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1: // Audio buffer is ready
                    {
                        // begin audio fft

                        break;
                    }
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
                    default:
                    {
                        super.handleMessage(msg);
                    }
                }
            }
        };

        rec_acc = new Xlo(this, mHandler, acc_samples, 2);
        rec_mic = new MicData(mHandler, audio_samples, 1.0, false);

        accelFFT[0] = new Fft(acc_samples, mHandler, 4);
        accelFFT[1] = new Fft(acc_samples, mHandler, 5);
        accelFFT[2] = new Fft(acc_samples, mHandler, 6);

        Fft.getOmega(audio_omega, audio_Fs);
        Fft.getOmega(accel_omega, acc_Fs);

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

        xSeries.setColor(Color.parseColor("#0B3861"));
        ySeries.setColor(Color.parseColor("#0B6138"));
        zSeries.setColor(Color.parseColor("#610B0B"));

        graph.addSeries(audioSeries);
        graph.addSeries(xSeries);
        graph.addSeries(ySeries);
        graph.addSeries(zSeries);

        rec_mic.run();
        rec_acc.run();
    }

    void addListenerToToggleButtons() {
        noise = (ToggleButton) findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) findViewById(R.id.toggleVibration);
        recordButton = (ToggleButton) findViewById(R.id.startstop);

        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noise.isChecked()){
                    noise.setChecked(false);
                    rec_mic.onPause();
                    graph.removeSeries(audioSeries);

                } else {
                    noise.setChecked(true);
                    rec_mic.run();
                    graph.addSeries(audioSeries);
                }
            }
        });

        vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (!vibration.isChecked()) {
                    vibration.setChecked(false);
                    rec_acc.onPause();
                    graph.removeSeries(xSeries);
                    graph.removeSeries(ySeries);
                    graph.removeSeries(zSeries);
                } else {
                    vibration.setChecked(true);
                    rec_acc.run();
                    graph.addSeries(xSeries);
                    graph.addSeries(ySeries);
                    graph.addSeries(zSeries);
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
            alertDialog.setView(eulaLayout);
            alertDialog.setTitle("Bluetooth Connection Alert");
            alertDialog.setMessage("In order for this app to work correctly " +
                    "you need to be connected to an OBDII.");
            alertDialog.setNegativeButton("Ok", new android.app.AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String checkBoxResult = "NOT checked";
                    if (dontShowAgain.isChecked())  checkBoxResult = "checked";
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("skipMessage", checkBoxResult);
                    // Commit the edits!
                    editor.apply();
                }
            });
            alertDialog.setPositiveButton("Bluetooth Settings", new android.app.AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String checkBoxResult = "NOT checked";
                    if (dontShowAgain.isChecked())  checkBoxResult = "checked";
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("skipMessage", checkBoxResult);
                    // Commit the edits!
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(intent);
                }
            });

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String skipMessage = settings.getString("skipMessage", "NOT checked");
            if (skipMessage != "checked" )
                alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        switch(item.getItemId()){
            case R.id.one:

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

    @Override
    public void onPause() {
        super.onPause();
        rec_acc.onPause();
        rec_mic.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Bluetooth setup
        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }


}