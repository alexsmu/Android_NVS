package byuie499.auto_nvs;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

public class TabFragment2 extends Fragment {
    View mMain = null;
    private ToggleButton toggleRealTime, togglePlayBack;
    private ImageButton playButton,stopButton,pauseButton,recordButton;
    private TextView textTitle;
    private Spinner fileSpinner;
    private SeekBar fftseekBar;
    private double[] audio_omega = new double[16384];
    private double[] accel_omega = new double[256];
    private Fft[] accelFFT = new Fft[3];
    private MicData rec_mic = null;
    private Xlo rec_acc = null;
    private LineGraphSeries<DataPoint> audioSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> xSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> ySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> zSeries = new LineGraphSeries<>();
    private GraphView graph = null;
    public Handler mHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_2, container, false);
        addListenerToToggleButtons();
        addListenerToImageButtons();
        addListenerToSeekBar();
        /*THE FOLLOWING ITEMS ARE FOR TESTING ONLY*/
        List<String> testFiles = new ArrayList<String>();
        testFiles.add("BlueCar.txt");
        testFiles.add("RedCar.txt");
        testFiles.add("NeonCar.txt");
        testFiles.add("BlackCar.txt");
        /*TESTCODE ENDS HERE*/

        fileSpinner = (Spinner) mMain.findViewById(R.id.fileSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(), R.layout.file_spinner, testFiles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileSpinner.setAdapter(adapter);

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
                        DataPoint[] dps = new DataPoint[186];
                        int j = 0;
                        for (int i = 8192; i < 8378; ++i) {
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
                        DataPoint[] dps = new DataPoint[128];
                        int j = 0;
                        for (int i = 128; i < 256; ++i) {
                            dps[j++] = new DataPoint(accel_omega[i], result[i]);
                        }
                        xSeries.resetData(dps);
                        break;
                    }
                    case 5: // Accelerometer y fft complete
                    {
                        // add to series
                        double[] result = (double[]) msg.obj;
                        DataPoint[] dps = new DataPoint[128];
                        int j = 0;
                        for (int i = 128; i < 256; ++i) {
                            dps[j++] = new DataPoint(accel_omega[i], result[i]);
                        }
                        ySeries.resetData(dps);
                        break;
                    }
                    case 6: // Accelerometer z fft complete
                    {
                        //add to series
                        double[] result = (double[]) msg.obj;
                        DataPoint[] dps = new DataPoint[128];
                        int j = 0;
                        for (int i = 128; i < 256; ++i) {
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

        rec_acc = new Xlo(this.getActivity(), mHandler, 256, 2);
        rec_mic = new MicData(mHandler, 16384);

        accelFFT[0] = new Fft(256, mHandler, 4);
        accelFFT[1] = new Fft(256, mHandler, 5);
        accelFFT[2] = new Fft(256, mHandler, 6);
        Fft.getOmega(audio_omega, 44100);
        Fft.getOmega(accel_omega, 1000);
        graph = (GraphView) mMain.findViewById(R.id.fftGraph);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-60);
        graph.getViewport().setMaxY(20);

        audioSeries.setTitle("Mic");
        audioSeries.setColor(Color.BLACK);
        xSeries.setTitle("X");
        ySeries.setTitle("Y");
        zSeries.setTitle("Z");
        xSeries.setColor(Color.BLUE);
        ySeries.setColor(Color.GREEN);
        zSeries.setColor(Color.RED);

        return mMain;
    }

    void addListenerToSeekBar() {
        fftseekBar = (SeekBar) mMain.findViewById(R.id.fftSeekBar);

        fftseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(togglePlayBack.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Seeking",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "SeekBar disabled during Real Time Session",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void addListenerToImageButtons() {
        playButton = (ImageButton) mMain.findViewById(R.id.playButton);
        stopButton = (ImageButton) mMain.findViewById(R.id.stopButton);
        pauseButton = (ImageButton) mMain.findViewById(R.id.pauseButton);
        recordButton = (ImageButton) mMain.findViewById(R.id.recordButton);

        playButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(togglePlayBack.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Play",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Play disabled during Real Time Session",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        stopButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(togglePlayBack.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Stop",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Stop Recording",
                            Toast.LENGTH_SHORT).show();
                    rec_mic.onPause();
                    rec_acc.onPause();
                }
            }

        });

        pauseButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(togglePlayBack.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Pause",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Pause disabled during Real Time Session",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        recordButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(toggleRealTime.isChecked()){
                    Toast.makeText(getActivity().getApplicationContext(), "Record",
                            Toast.LENGTH_SHORT).show();
                    rec_mic.run();
                    rec_acc.run();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Record disabled during Playback Session",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    void addListenerToToggleButtons() {
        toggleRealTime = (ToggleButton) mMain.findViewById(R.id.toggleRealTime);
        togglePlayBack = (ToggleButton) mMain.findViewById(R.id.togglePlayBack);
        textTitle = (TextView) mMain.findViewById(R.id.textTitle);

        toggleRealTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleRealTime.setChecked(true);
                togglePlayBack.setChecked(false);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.tab2_title1),
                        Toast.LENGTH_SHORT).show();
                textTitle.setText(getString(R.string.tab2_title1));
                recordButton.setEnabled(true);
                playButton.setEnabled(false);
                pauseButton.setEnabled(false);
            }
        });

        togglePlayBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePlayBack.setChecked(true);
                toggleRealTime.setChecked(false);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.tab2_title2),
                        Toast.LENGTH_SHORT).show();
                textTitle.setText(getString(R.string.tab2_title2));
                recordButton.setEnabled(false);
                playButton.setEnabled(true);
                pauseButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        rec_acc.onPause();
        rec_mic.onPause();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Is fragment currently visible ?
        if (this.isVisible()) {
            // Is it becoming invisible?
            if (!isVisibleToUser) {
                rec_acc.onPause();
                rec_mic.onPause();
                graph.removeAllSeries();
            }
            else if(graph != null) {
                Xlo.isEnabled = Tab1Data.getBool("Vibration", true);
                MicData.isEnabled = Tab1Data.getBool("Noise", false);
                if (MicData.isEnabled)
                    graph.addSeries(audioSeries);
                if (Xlo.isEnabled) {
                    graph.addSeries(xSeries);
                    graph.addSeries(ySeries);
                    graph.addSeries(zSeries);
                }
            }
        }
    }
}
