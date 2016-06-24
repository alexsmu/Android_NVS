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
import java.util.List;

public class SettingsMenu extends AppCompatActivity {
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

        List<String> testFiles = new ArrayList<>();
        testFiles.add("Profile 1");
        testFiles.add("Profile 2");
        testFiles.add("Profile 3");
        testFiles.add("Profile 4");

        fileSpinner = (Spinner) findViewById(R.id.fileSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, testFiles);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        fileSpinner.setAdapter(adapter);

        settingsPrefs = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);

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

    void update(){
        String profile = fileSpinner.getSelectedItem().toString();
        prefs = getSharedPreferences(profile, MODE_PRIVATE);
        settingsPrefs.edit().putString("profile", profile).apply();
        //Toast.makeText(getApplicationContext(), profile, Toast.LENGTH_SHORT).show();

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
                prefs.edit().putString("ratio1", ratio1.getText().toString()).apply();
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
                prefs.edit().putString("ratio2", ratio2.getText().toString()).apply();
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
                prefs.edit().putString("ratio3", ratio3.getText().toString()).apply();
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
                prefs.edit().putString("ratio4", ratio4.getText().toString()).apply();
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
                prefs.edit().putString("ratio5", ratio5.getText().toString()).apply();
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
                prefs.edit().putString("ratio6", ratio6.getText().toString()).apply();
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
                prefs.edit().putString("name2", name2.getText().toString()).apply();
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
                prefs.edit().putString("name3", name3.getText().toString()).apply();
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
                prefs.edit().putString("name4", name4.getText().toString()).apply();
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
                prefs.edit().putString("name5", name3.getText().toString()).apply();
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
                prefs.edit().putString("name6", name6.getText().toString()).apply();
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
                prefs.edit().putString("ratio7", ratio7.getText().toString()).apply();
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
                prefs.edit().putString("ratio8", ratio8.getText().toString()).apply();
            }
        });
    }

    void loadVars(){
        ratio1.setText(prefs.getString("ratio1", ""));
        ratio2.setText(prefs.getString("ratio2", ""));
        ratio3.setText(prefs.getString("ratio3", ""));
        ratio4.setText(prefs.getString("ratio4", ""));
        ratio5.setText(prefs.getString("ratio5", ""));
        ratio6.setText(prefs.getString("ratio6", ""));

        name2.setText(prefs.getString("name2", ""));
        name3.setText(prefs.getString("name3", ""));
        name4.setText(prefs.getString("name4", ""));
        name5.setText(prefs.getString("name5", ""));
        name6.setText(prefs.getString("name6", ""));

        ratio7.setText(prefs.getString("ratio7", ""));
        ratio8.setText(prefs.getString("ratio8", ""));

        check1.setChecked(Boolean.valueOf(prefs.getString("check1", "")));
        check2.setChecked(Boolean.valueOf(prefs.getString("check2", "")));
        check3.setChecked(Boolean.valueOf(prefs.getString("check3", "")));
        check4.setChecked(Boolean.valueOf(prefs.getString("check4", "")));
        check5.setChecked(Boolean.valueOf(prefs.getString("check5", "")));
        check6.setChecked(Boolean.valueOf(prefs.getString("check6", "")));
        check7.setChecked(Boolean.valueOf(prefs.getString("check7", "")));
        check8.setChecked(Boolean.valueOf(prefs.getString("check8", "")));
    }

    void setSpinner(){
        String profile = settingsPrefs.getString("profile", "");
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
                update();
                break;
            case 2:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(1);
                    }
                });
                update();
                break;
            case 3:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(2);
                    }
                });
                update();
                break;
            case 4:
                fileSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        fileSpinner.setSelection(3);
                    }
                });
                update();
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
                update();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                update();
            }
        });
    }

    /*void addListenerToButtons(){
        Button save1 = (Button) findViewById(R.id.save1);
        Button save2 = (Button) findViewById(R.id.save2);
        if (save1 != null) {
            save1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (save2 != null) {
            save2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    void addListenersToCheckBoxes(){
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check1", String.valueOf(check1.isChecked())).apply();
            }
        });
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check2", String.valueOf(check2.isChecked())).apply();
            }
        });
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check3", String.valueOf(check3.isChecked())).apply();
            }
        });
        check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check4", String.valueOf(check4.isChecked())).apply();
            }
        });
        check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check5", String.valueOf(check5.isChecked())).apply();
            }
        });
        check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check6", String.valueOf(check6.isChecked())).apply();
            }
        });
        check7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check7", String.valueOf(check7.isChecked())).apply();
            }
        });
        check8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putString("check8", String.valueOf(check8.isChecked())).apply();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        setSpinner();
    }

}