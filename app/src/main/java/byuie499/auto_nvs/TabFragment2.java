package byuie499.auto_nvs;

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

import java.util.ArrayList;
import java.util.List;

public class TabFragment2 extends Fragment {
    View mMain = null;
    private ToggleButton toggleRealTime, togglePlayBack;
    private ImageButton playButton,stopButton,pauseButton,recordButton;
    private TextView textTitle;
    private Spinner fileSpinner;
    private SeekBar fftseekBar;
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
                        break;
                    }
                    case 3: // Accelerometer data is ready
                    {
                        // begin fft
                        break;
                    }
                    case 4: // Accelerometer fft is complete
                    {
                        // add to series
                        break;
                    }
                    case 5: // Audio fft correlation complete
                    {
                        // add to series
                        break;
                    }
                    case 6: // Accelerometer fft correlation complete
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

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Is fragment currently visible ?
        if (this.isVisible()) {
            // Is it becoming invisible?
            if (!isVisibleToUser) {

            }
        }
        else { // Is fragment currently invisible?
            // Is it becoming visible?
            if (isVisibleToUser) {

            }
        }
    }
}
