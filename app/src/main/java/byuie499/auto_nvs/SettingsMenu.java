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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsMenu extends AppCompatActivity {
    private SettingsData staticData = null;// dummy container to initialize SettingsData for the current context
    private Spinner fileSpinner;
    private RadioButton xButton, yButton, zButton, rButton;
    private EditText ratio1;
    private EditText ratio2;
    private EditText ratio3;
    private EditText ratio4;
    private EditText ratio7;
    private EditText name2;
    private EditText name3;
    private EditText name4;
    private CheckBox check1;
    private CheckBox check2;
    private CheckBox check3;
    private CheckBox check4;
    private CheckBox check5;
    private CheckBox check7;
    private CheckBox check8;
    private CheckBox screenOn;
    private CheckBox tutorialOn;
    private TextView name1;
    private TextView tireTxt;
    private TextView screenTxt;
    private TextView tutorialTxt;
    private TextView normTxt;
    private TextView pSampling;
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
        check5 = (CheckBox) findViewById(R.id.check5);
        check7 = (CheckBox) findViewById(R.id.check7);
        check8 = (CheckBox) findViewById(R.id.check8);

        name1  = (TextView) findViewById(R.id.name1);
        name2  = (EditText) findViewById((R.id.name2));
        name3  = (EditText) findViewById((R.id.name3));
        name4  = (EditText) findViewById((R.id.name4));

        ratio7  = (EditText) findViewById((R.id.tire1));

        xButton = (RadioButton) findViewById(R.id.xButton);
        yButton  = (RadioButton) findViewById(R.id.yButton);
        zButton = (RadioButton) findViewById(R.id.zButton);
        rButton = (RadioButton) findViewById(R.id.rButton);

        screenOn = (CheckBox) findViewById((R.id.screenOn));
        tutorialOn = (CheckBox) findViewById((R.id.tutorialOn));

        tireTxt = (TextView) findViewById(R.id.tireTxt);
        pSampling = (TextView) findViewById(R.id.pSmpling);
        normTxt = (TextView) findViewById(R.id.normTxt);
        screenTxt = (TextView) findViewById(R.id.screenTxt);
        tutorialTxt = (TextView) findViewById(R.id.tutorialTxt);
    }

    void setProfile(){
        SettingsData.currentProfile = fileSpinner.getSelectedItem().toString();
        SettingsData.setString("profile", SettingsData.currentProfile);
        loadVars();
    }

    public class TextListener implements TextWatcher {
        private String t;
        public TextListener(String tag) {
            super();
            t = tag;
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SettingsData.setString(SettingsData.currentProfile + "_" + t, s.toString());
        }
    }

    void addListenersToEditTexts(){
        ratio1.addTextChangedListener(new TextListener(ratio1.getTag().toString()));
        ratio2.addTextChangedListener(new TextListener(ratio2.getTag().toString()));
        ratio3.addTextChangedListener(new TextListener(ratio3.getTag().toString()));
        ratio4.addTextChangedListener(new TextListener(ratio4.getTag().toString()));
        ratio7.addTextChangedListener(new TextListener(ratio7.getTag().toString()));
    }

    void loadVars(){
        ratio1.setText(SettingsData.getString(SettingsData.currentProfile + "_" + ratio1.getTag().toString(), "0"));
        ratio2.setText(SettingsData.getString(SettingsData.currentProfile + "_" + ratio1.getTag().toString(), "0"));
        ratio3.setText(SettingsData.getString(SettingsData.currentProfile + "_" + ratio1.getTag().toString(), "0"));
        ratio4.setText(SettingsData.getString(SettingsData.currentProfile + "_" + ratio1.getTag().toString(), "0"));

        name2.setText(SettingsData.getString(SettingsData.currentProfile + "_" + name2.getTag().toString(), ""));
        name3.setText(SettingsData.getString(SettingsData.currentProfile + "_" + name3.getTag().toString(), ""));
        name4.setText(SettingsData.getString(SettingsData.currentProfile + "_" + name4.getTag().toString(), ""));

        ratio7.setText(SettingsData.getString(SettingsData.currentProfile + "_" + ratio7.getTag().toString(), "0"));

        check1.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check1.getTag().toString(), true));
        check2.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check2.getTag().toString(), false));
        check3.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check3.getTag().toString(), false));
        check4.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check4.getTag().toString(), false));
        check5.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check5.getTag().toString(), false));
        check7.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check7.getTag().toString(), false));
        check8.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + check8.getTag().toString(), false));

        xButton.setChecked(SettingsData.getInt(SettingsData.currentProfile + "_accelOpt", 3) == 0);
        yButton.setChecked(SettingsData.getInt(SettingsData.currentProfile + "_accelOpt", 3) == 1);
        zButton.setChecked(SettingsData.getInt(SettingsData.currentProfile + "_accelOpt", 3) == 2);
        rButton.setChecked(SettingsData.getInt(SettingsData.currentProfile + "_accelOpt", 3) == 3);

        screenOn.setChecked(SettingsData.isChecked(SettingsData.currentProfile + "_" + screenOn.getTag().toString(), true));
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

    public CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SettingsData.setChecked(SettingsData.currentProfile + "_"  + buttonView.getTag().toString(), isChecked);
        }
    };

    void addListenersToCheckBoxes(){
        check1.setOnCheckedChangeListener(checkListener);
        check2.setOnCheckedChangeListener(checkListener);
        check3.setOnCheckedChangeListener(checkListener);
        check4.setOnCheckedChangeListener(checkListener);
        check5.setOnCheckedChangeListener(checkListener);
        check7.setOnCheckedChangeListener(checkListener);
        check8.setOnCheckedChangeListener(checkListener);
        screenOn.setOnCheckedChangeListener(checkListener);
        tutorialOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsData.setFirstRun(isChecked);
            }
        });
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xButton.isChecked()) {
                    SettingsData.setInt(SettingsData.currentProfile + "_accelOpt", 0);
                    xButton.setChecked(true);
                    yButton.setChecked(false);
                    zButton.setChecked(false);
                    rButton.setChecked(false);
                }
            }
        });
        yButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yButton.isChecked()) {
                    SettingsData.setInt(SettingsData.currentProfile + "_accelOpt", 1);
                    xButton.setChecked(false);
                    yButton.setChecked(true);
                    zButton.setChecked(false);
                    rButton.setChecked(false);
                }
            }
        });

        zButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zButton.isChecked()) {
                    SettingsData.setInt(SettingsData.currentProfile + "_accelOpt", 2);
                    xButton.setChecked(false);
                    yButton.setChecked(false);
                    zButton.setChecked(true);
                    rButton.setChecked(false);
                }
            }
        });

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rButton.isChecked()) {
                    SettingsData.setInt(SettingsData.currentProfile + "_accelOpt", 3);
                    xButton.setChecked(false);
                    yButton.setChecked(false);
                    zButton.setChecked(false);
                    rButton.setChecked(true);
                }
            }
        });

        name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check1.performClick();
            }
        });
        tireTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check7.performClick();
            }
        });
        pSampling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check5.performClick();
            }
        });
        normTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check8.performClick();
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
