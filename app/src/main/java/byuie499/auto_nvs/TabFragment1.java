package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

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

        return mMain;
    }

    void addListenerToToggleButtons() {
        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tab1Data.getBool("Noise", false))
                {
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
                if (Tab1Data.getBool("Vibration", true))
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
        CheckBox check1 = (CheckBox) mMain.findViewById(R.id.check1);
        check1.setChecked(true);

        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Tab1Data.getBool("check1", true)) {
                    Toast.makeText(getActivity(), "check1 true", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "check1 false", Toast.LENGTH_SHORT).show();
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