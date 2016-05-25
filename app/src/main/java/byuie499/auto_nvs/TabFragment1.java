package byuie499.auto_nvs;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class TabFragment1 extends Fragment implements View.OnFocusChangeListener{
    private View mMain;
    private Tab1Data staticData = null;
    private ToggleButton noise, vibration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_1, container, false);
        staticData = new Tab1Data(mMain.getContext());
        noise = (ToggleButton) mMain.findViewById(R.id.toggleNoise);
        vibration = (ToggleButton) mMain.findViewById(R.id.toggleVibration);
        noise.setChecked(Tab1Data.getBool("Noise", false));
        vibration.setChecked(Tab1Data.getBool("Vibration", true));
        CheckBox check1 = (CheckBox) mMain.findViewById(R.id.check1);
        check1.setChecked(Tab1Data.getBool("check1", true));

        addListenerToToggleButtons();
        addListenerToNames();
        addListenersToValues();
        addListenersToCheckBoxes();

        SensorManager sensorMngr = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        List<Sensor> sensors = sensorMngr.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensors) {Log.d("     " + "SENSOR LIST", sensor.getName());}


        return mMain;
    }

    void addListenerToToggleButtons() {
        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noise.isChecked() && !vibration.isChecked()) {
                    noise.setChecked(true);
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot disable both buttons at the same time!",
                            Toast.LENGTH_SHORT).show();
                } else if (!noise.isChecked()){
                    noise.setChecked(false);
                    Tab1Data.setCheckBox("Noise", false);
                } else {
                    noise.setChecked(true);
                    Tab1Data.setCheckBox("Noise", true);
                }

            }
        });

        vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!noise.isChecked() && !vibration.isChecked()) {
                    vibration.setChecked(true);
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot disable both buttons at the same time!",
                            Toast.LENGTH_SHORT).show();
                } else if  (!vibration.isChecked())
                {
                    vibration.setChecked(false);
                    Tab1Data.setCheckBox("Vibration", false);
                } else {
                    vibration.setChecked(true);
                    Tab1Data.setCheckBox("Vibration", true);
                }
            }
        });
    }

    void addListenerToNames() {
        EditText name1 = (EditText) mMain.findViewById((R.id.name1));
        name1.setOnFocusChangeListener(this);
        EditText name2 = (EditText) mMain.findViewById((R.id.name2));
        name2.setOnFocusChangeListener(this);
        EditText name3 = (EditText) mMain.findViewById((R.id.name3));
        name3.setOnFocusChangeListener(this);
        EditText name4 = (EditText) mMain.findViewById((R.id.name4));
        name4.setOnFocusChangeListener(this);
        EditText name5 = (EditText) mMain.findViewById((R.id.name5));
        name5.setOnFocusChangeListener(this);
        EditText name6 = (EditText) mMain.findViewById((R.id.name6));
        name6.setOnFocusChangeListener(this);
    }

    void addListenersToValues(){
        EditText ratio1 = (EditText) mMain.findViewById((R.id.value1));
        ratio1.setOnFocusChangeListener(this);
        EditText ratio2 = (EditText) mMain.findViewById((R.id.value2));
        ratio2.setOnFocusChangeListener(this);
        EditText ratio3 = (EditText) mMain.findViewById((R.id.value3));
        ratio3.setOnFocusChangeListener(this);
        EditText ratio4 = (EditText) mMain.findViewById((R.id.value4));
        ratio4.setOnFocusChangeListener(this);
        EditText ratio5 = (EditText) mMain.findViewById((R.id.value5));
        ratio5.setOnFocusChangeListener(this);
        EditText ratio6 = (EditText) mMain.findViewById((R.id.value6));
        ratio6.setOnFocusChangeListener(this);
    }

    void addListenersToCheckBoxes(){
        final CheckBox check1 = (CheckBox) mMain.findViewById(R.id.check1);
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check1.isChecked()) {
                    Toast.makeText(getActivity(), "check1 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check1 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox check2 = (CheckBox) mMain.findViewById(R.id.check2);
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check2.isChecked()) {
                    Toast.makeText(getActivity(), "check2 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check2 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox check3 = (CheckBox) mMain.findViewById(R.id.check3);
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check3.isChecked()) {
                    Toast.makeText(getActivity(), "check3 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check3 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox check4 = (CheckBox) mMain.findViewById(R.id.check4);
        check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check4.isChecked()) {
                    Toast.makeText(getActivity(), "check4 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check4 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox check5 = (CheckBox) mMain.findViewById(R.id.check5);
        check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check5.isChecked()) {
                    Toast.makeText(getActivity(), "check5 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check5 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox check6 = (CheckBox) mMain.findViewById(R.id.check6);
        check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check6.isChecked()) {
                    Toast.makeText(getActivity(), "check6 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check6 false", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.name1:
                Toast.makeText(getActivity(), "name 1 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name2:
                Toast.makeText(getActivity(), "name 2 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name3:
                Toast.makeText(getActivity(), "name 3 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name4:
                Toast.makeText(getActivity(), "name 4 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name5:
                Toast.makeText(getActivity(), "name 5 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name6:
                Toast.makeText(getActivity(), "name 6 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value1:
                Toast.makeText(getActivity(), "value 1 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value2:
                Toast.makeText(getActivity(), "value 2 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value3:
                Toast.makeText(getActivity(), "value 3 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value4:
                Toast.makeText(getActivity(), "value 4 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value5:
                Toast.makeText(getActivity(), "value 5 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value6:
                Toast.makeText(getActivity(), "value 6 focus change", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "Not sure?", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

}