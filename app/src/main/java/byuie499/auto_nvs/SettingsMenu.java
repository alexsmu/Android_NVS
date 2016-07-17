package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsMenu extends AppCompatActivity {
    private SettingsData staticData = null;// dummy container to initialize SettingsData for the current context
    private Spinner fileSpinner;
    private EditText ratio1;
    private EditText ratio2;
    private EditText ratio3;
    private EditText ratio4;
    private EditText ratio5;
    private EditText ratio6;
    private EditText ratio7;
    private EditText ratio8;
    private EditText name2;
    private EditText name3;
    private EditText name4;
    private CheckBox check1;
    private CheckBox check2;
    private CheckBox check3;
    private CheckBox check4;
    private CheckBox screenOn;
    private CheckBox tutorialOn;
    private TextView screenTxt;
    private TextView tutorialTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        List<String> testFiles = new ArrayList<>(Arrays.asList("Profile 1","Profile 2","Profile 3","Profile 4"));

        fileSpinner = (Spinner) findViewById(R.id.fileSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, testFiles);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        fileSpinner.setAdapter(adapter);

        if (SettingsData.mContext != getApplicationContext())
            staticData = new SettingsData(getApplicationContext());

        init();

        addListenerToSpinner();
        addListenersToCheckBoxes();
        addListenersToEditTexts();
    }

    void init(){
        ratio1  = (EditText) findViewById((R.id.value1));
        ratio2  = (EditText) findViewById((R.id.value2));
        ratio3  = (EditText) findViewById((R.id.value3));
        ratio4  = (EditText) findViewById((R.id.value4));

        check1 = (CheckBox) findViewById(R.id.check1);
        check2 = (CheckBox) findViewById(R.id.check2);
        check3 = (CheckBox) findViewById(R.id.check3);
        check4 = (CheckBox) findViewById(R.id.check4);

        name2  = (EditText) findViewById((R.id.name2));
        name3  = (EditText) findViewById((R.id.name3));
        name4  = (EditText) findViewById((R.id.name4));

        ratio7  = (EditText) findViewById((R.id.tire1));

        screenOn = (CheckBox) findViewById((R.id.screenOn));
        tutorialOn = (CheckBox) findViewById((R.id.tutorialOn));

        screenTxt = (TextView) findViewById(R.id.screenTxt);
        tutorialTxt = (TextView) findViewById(R.id.tutorialTxt);

    }

    void setProfile(){

        SettingsData.currentProfile = fileSpinner.getSelectedItem().toString();
        SettingsData.setString("profile", SettingsData.currentProfile);

        loadVars();
    }

    void addListenersToEditTexts(){
        ratio1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio1", ratio1.getText().toString());
            }
        });
        ratio2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio2", ratio2.getText().toString());
            }
        });
        ratio3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio3", ratio3.getText().toString());
            }
        });
        ratio4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio4", ratio4.getText().toString());
            }
        });
    }

    void loadVars(){
        ratio1.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio1", "0"));
        ratio2.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio2", "0"));
        ratio3.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio3", "0"));
        ratio4.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio4", "0"));

        name2.setText(SettingsData.getString(SettingsData.currentProfile + "_name2", ""));
        name3.setText(SettingsData.getString(SettingsData.currentProfile + "_name3", ""));
        name4.setText(SettingsData.getString(SettingsData.currentProfile + "_name4", ""));

        ratio7.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio7", "0"));

        check1.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check1", false));
        check2.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check2", false));
        check3.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check3", false));
        check4.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check4", false));
        screenOn.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check9", true));
        tutorialOn.setChecked(SettingsData.isFirstRun());
    }

    void setSpinner(){
        String profile = SettingsData.getString("profile", "Profile 1");
        String strNum = profile.substring(8,9);
        int num = Integer.parseInt(strNum);

        switch(num) {
            case 1:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(0);
                    }
                });
                setProfile();
                break;
            case 2:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(1);
                    }
                });
                setProfile();
                break;
            case 3:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(2);
                    }
                });
                setProfile();
                break;
            case 4:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(3);
                    }
                });
                setProfile();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                break;
        }

        Toast.makeText(getApplicationContext(), profile, Toast.LENGTH_SHORT).show();
    }

    void addListenerToSpinner(){
        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                setProfile();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                setProfile();
            }
        });
    }

    void addListenersToCheckBoxes(){
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check1", isChecked);
            }
        });
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check2", isChecked);
            }
        });
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check3", isChecked);
            }
        });
        check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check4", isChecked);
            }
        });
        screenOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check9", isChecked);
            }
        });
        tutorialOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setFirstRun(isChecked);
            }
        });
        screenTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenOn.performClick();
            }
        });
        tutorialTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialOn.performClick();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        setProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SettingsData.mContext != getApplicationContext())
            staticData = new SettingsData(getApplicationContext());
        setSpinner();
        setProfile();

        if (SettingsData.isChecked(SettingsData.currentProfile + "_check9", true)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on
        }else {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

}
