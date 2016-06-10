package byuie499.auto_nvs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends AppCompatActivity implements View.OnFocusChangeListener{
    private SettingsData staticData = null;
    private Spinner fileSpinner;
    private SharedPreferences prefs;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

        if (SettingsData.mContext == null)
            staticData = new SettingsData(getApplicationContext());
        setContentView(R.layout.settings);
        //CheckBox check1 = (CheckBox) findViewById(R.id.check1);
        //check1.setChecked(SettingsData.isChecked(check1.getTag().toString(), true));

        List<String> testFiles = new ArrayList<String>();
        testFiles.add("Profile 1");
        testFiles.add("Profile 2");
        testFiles.add("Profile 3");
        testFiles.add("Profile 4");

        fileSpinner = (Spinner) findViewById(R.id.fileSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, testFiles);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        fileSpinner.setAdapter(adapter);

        addListenerToSpinner();
        addListenerToNames();
        addListenersToValues();
        addListenersToCheckBoxes();
        addListenerToButtons();
    }

    void addListenerToSpinner(){
        fileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ratio1  = (EditText) findViewById((R.id.value1));
                ratio2  = (EditText) findViewById((R.id.value2));
                ratio3  = (EditText) findViewById((R.id.value3));
                ratio4  = (EditText) findViewById((R.id.value4));
                ratio5  = (EditText) findViewById((R.id.value5));
                ratio6  = (EditText) findViewById((R.id.value6));

                name2  = (EditText) findViewById((R.id.name2));
                name3  = (EditText) findViewById((R.id.name3));
                name4  = (EditText) findViewById((R.id.name4));
                name5  = (EditText) findViewById((R.id.name5));
                name6  = (EditText) findViewById((R.id.name6));

                ratio7  = (EditText) findViewById((R.id.tire1));
                ratio8  = (EditText) findViewById((R.id.gearval));

                prefs.edit().putString("ratio1", ratio1.getText().toString()).apply();
                prefs.edit().putString("ratio2", ratio2.getText().toString()).apply();
                prefs.edit().putString("ratio3", ratio3.getText().toString()).apply();
                prefs.edit().putString("ratio4", ratio4.getText().toString()).apply();
                prefs.edit().putString("ratio5", ratio5.getText().toString()).apply();
                prefs.edit().putString("ratio6", ratio6.getText().toString()).apply();
                prefs.edit().putString("name2", name2.getText().toString()).apply();
                prefs.edit().putString("name3", name3.getText().toString()).apply();
                prefs.edit().putString("name4", name4.getText().toString()).apply();
                prefs.edit().putString("name5", name5.getText().toString()).apply();
                prefs.edit().putString("name6", name6.getText().toString()).apply();
                prefs.edit().putString("ratio7", ratio7.getText().toString()).apply();
                prefs.edit().putString("ratio8", ratio8.getText().toString()).apply();

                String text = fileSpinner.getSelectedItem().toString();
                prefs = getSharedPreferences(text, MODE_PRIVATE);

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
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void addListenerToButtons(){
        Button save1 = (Button) findViewById(R.id.save1);
        Button save2 = (Button) findViewById(R.id.save2);
        if (save1 != null) {
            save1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putString("ratio1", ratio1.getText().toString()).apply();
                    prefs.edit().putString("ratio2", ratio2.getText().toString()).apply();
                    prefs.edit().putString("ratio3", ratio3.getText().toString()).apply();
                    prefs.edit().putString("ratio4", ratio4.getText().toString()).apply();
                    prefs.edit().putString("ratio5", ratio5.getText().toString()).apply();
                    prefs.edit().putString("ratio6", ratio6.getText().toString()).apply();
                    prefs.edit().putString("name2", name2.getText().toString()).apply();
                    prefs.edit().putString("name3", name3.getText().toString()).apply();
                    prefs.edit().putString("name4", name4.getText().toString()).apply();
                    prefs.edit().putString("name5", name5.getText().toString()).apply();
                    prefs.edit().putString("name6", name6.getText().toString()).apply();
                }
            });
        }
        if (save2 != null) {
            save2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putString("ratio7", ratio7.getText().toString()).apply();
                    prefs.edit().putString("ratio8", ratio8.getText().toString()).apply();
                }
            });
        }
    }

    void addListenerToNames(){

        TextView name1 = (TextView) findViewById((R.id.name1));
        if (name1 != null) {
            name1.setOnFocusChangeListener(this);
        }
        EditText name2 = (EditText) findViewById((R.id.name2));
        if (name2 != null) {
            name2.setOnFocusChangeListener(this);
        }
        EditText name3 = (EditText) findViewById((R.id.name3));
        if (name3 != null) {
            name3.setOnFocusChangeListener(this);
        }
        EditText name4 = (EditText) findViewById((R.id.name4));
        if (name4 != null) {
            name4.setOnFocusChangeListener(this);
        }
        EditText name5 = (EditText) findViewById((R.id.name5));
        if (name5 != null) {
            name5.setOnFocusChangeListener(this);
        }
        EditText name6 = (EditText) findViewById((R.id.name6));
        if (name6 != null) {
            name6.setOnFocusChangeListener(this);
        }

    }

    void addListenersToValues(){
        if (ratio1 != null) {
            ratio1.setOnFocusChangeListener(this);
        }
        EditText ratio2 = (EditText) findViewById((R.id.value2));
        if (ratio2 != null) {
            ratio2.setOnFocusChangeListener(this);
        }
        EditText ratio3 = (EditText) findViewById((R.id.value3));
        if (ratio3 != null) {
            ratio3.setOnFocusChangeListener(this);
        }
        EditText ratio4 = (EditText) findViewById((R.id.value4));
        if (ratio4 != null) {
            ratio4.setOnFocusChangeListener(this);
        }
        EditText ratio5 = (EditText) findViewById((R.id.value5));
        if (ratio5 != null) {
            ratio5.setOnFocusChangeListener(this);
        }
        EditText ratio6 = (EditText) findViewById((R.id.value6));
        if (ratio6 != null) {
            ratio6.setOnFocusChangeListener(this);
        }
    }

    void addListenersToCheckBoxes(){
        final CheckBox check1 = (CheckBox) findViewById(R.id.check1);
        if (check1 != null) {
            check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check1 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check1 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final CheckBox check2 = (CheckBox) findViewById(R.id.check2);
        if (check2 != null) {
            check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check2 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check2 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final CheckBox check3 = (CheckBox) findViewById(R.id.check3);
        if (check3 != null) {
            check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check3 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check3 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final CheckBox check4 = (CheckBox) findViewById(R.id.check4);
        if (check4 != null) {
            check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check4 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check4 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final CheckBox check5 = (CheckBox) findViewById(R.id.check5);
        if (check5 != null) {
            check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check5 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check5 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final CheckBox check6 = (CheckBox) findViewById(R.id.check6);
        if (check6 != null) {
            check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "check6 true", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "check6 false", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.name1:
                break;
            default:
                //Toast.makeText(getApplicationContext(), "Focus changed", Toast.LENGTH_SHORT).show();
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