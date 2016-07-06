package byuie499.auto_nvs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SettingsMenu extends AppCompatActivity {
    //private SettingsData staticData = null;
    private Spinner fileSpinner;
    private SharedPreferences prefs;
    private SharedPreferences settingsPrefs;
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
    private EditText name5;
    private EditText name6;
    private CheckBox check1;
    private CheckBox check2;
    private CheckBox check3;
    private CheckBox check4;
    private CheckBox check5;
    private CheckBox check6;
    private CheckBox check7;
    private CheckBox check8;
    private SettingsData preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        settingsPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

        //if (SettingsData.mContext == null)
        //    staticData = new SettingsData(getApplicationContext());
        setContentView(R.layout.settings);

        List<String> testFiles = new ArrayList<>(Arrays.asList("Profile 1","Profile 2","Profile 3","Profile 4"));

        fileSpinner = (Spinner) findViewById(R.id.fileSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, testFiles);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        fileSpinner.setAdapter(adapter);

        if (SettingsData.mContext != getApplicationContext()){
            preferences = new SettingsData(getApplicationContext());
        }

        //SettingsData.currentProfile = SettingsData.SettingsData.currentProfile;

        settingsPrefs = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        if (settingsPrefs.getString("profile", "").length() == 0){
            settingsPrefs.edit().putString("profile", "Profile 1").apply();
        }

        init();

        addListenerToSpinner();
        addListenersToCheckBoxes();
        addListenersToEditTexts();

        //addListenerToButtons();
        //setSpinner();
    }

    void init(){
        ratio1  = (EditText) findViewById((R.id.value1));
        ratio2  = (EditText) findViewById((R.id.value2));
        ratio3  = (EditText) findViewById((R.id.value3));
        ratio4  = (EditText) findViewById((R.id.value4));
        ratio5  = (EditText) findViewById((R.id.value5));
        ratio6  = (EditText) findViewById((R.id.value6));

        check1 = (CheckBox) findViewById(R.id.check1);
        check2 = (CheckBox) findViewById(R.id.check2);
        check3 = (CheckBox) findViewById(R.id.check3);
        check4 = (CheckBox) findViewById(R.id.check4);
        check5 = (CheckBox) findViewById(R.id.check5);
        check6 = (CheckBox) findViewById(R.id.check6);
        check7 = (CheckBox) findViewById(R.id.check7);
        check8 = (CheckBox) findViewById(R.id.check8);

        name2  = (EditText) findViewById((R.id.name2));
        name3  = (EditText) findViewById((R.id.name3));
        name4  = (EditText) findViewById((R.id.name4));
        name5  = (EditText) findViewById((R.id.name5));
        name6  = (EditText) findViewById((R.id.name6));

        ratio7  = (EditText) findViewById((R.id.tire1));
        ratio8  = (EditText) findViewById((R.id.gearval));
    }

    void setProfile(){
        //String profile = fileSpinner.getSelectedItem().toString();
        //prefs = getSharedPreferences(profile, MODE_PRIVATE);
        //settingsPrefs.edit().putString("profile", profile).apply();
        //Toast.makeText(getApplicationContext(), profile, Toast.LENGTH_SHORT).show();

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
        ratio5.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio5", ratio5.getText().toString());
            }
        });
        ratio6.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio6", ratio6.getText().toString());
            }
        });

        name2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_name2", name2.getText().toString());
            }
        });
        name3.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_name3", name3.getText().toString());
            }
        });
        name4.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_name4", name4.getText().toString());
            }
        });
        name5.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_name5", name5.getText().toString());
            }
        });
        name6.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_name6", name6.getText().toString());
            }
        });

        ratio7.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio7", ratio7.getText().toString());
            }
        });
        ratio8.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsData.setString(SettingsData.currentProfile + "_ratio8", ratio8.getText().toString());
            }
        });
    }

    void loadVars(){
        ratio1.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio1", "0"));
        ratio2.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio2", "0"));
        ratio3.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio3", "0"));
        ratio4.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio4", "0"));
        ratio5.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio5", "0"));
        ratio6.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio6", "0"));

        name2.setText(SettingsData.getString(SettingsData.currentProfile + "_name2", ""));
        name3.setText(SettingsData.getString(SettingsData.currentProfile + "_name3", ""));
        name4.setText(SettingsData.getString(SettingsData.currentProfile + "_name4", ""));
        name5.setText(SettingsData.getString(SettingsData.currentProfile + "_name5", ""));
        name6.setText(SettingsData.getString(SettingsData.currentProfile + "_name6", ""));

        ratio7.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio7", "0"));
        ratio8.setText(SettingsData.getString(SettingsData.currentProfile + "_ratio8", "0"));

        check1.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check1", false));
        check2.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check2", false));
        check3.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check3", false));
        check4.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check4", false));
        check5.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check5", false));
        check6.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check6", false));
        check7.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check7", false));
        check8.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_check8", false));
    }

    void setSpinner(){
        String profile = SettingsData.getString("profile", "Profile 1");
        String strNum = profile.substring(8,9);
        int num = Integer.parseInt(strNum);

        //Toast.makeText(getApplicationContext(), Integer.toString(num), Toast.LENGTH_SHORT).show();

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
                //prefs.edit().putString("check1", String.valueOf(check1.isChecked())).apply();
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
        check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check5", isChecked);
            }
        });
        check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check6", isChecked);
            }
        });
        check7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check7", isChecked);
            }
        });
        check8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setChecked(SettingsData.currentProfile + "_check8", isChecked);
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
        setSpinner();
    }

}
