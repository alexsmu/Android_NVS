package byuie499.auto_nvs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsMenu extends AppCompatActivity implements View.OnFocusChangeListener{
    private SettingsData staticData = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SettingsData.mContext == null)
            staticData = new SettingsData(getApplicationContext());
        setContentView(R.layout.settings);
        CheckBox check1 = (CheckBox) findViewById(R.id.check1);
        //check1.setChecked(SettingsData.isChecked(check1.getTag().toString(), true));

        //addListenerToNames();
        //addListenersToValues();
        //addListenersToCheckBoxes();
    }

    void addListenerToNames(){

        EditText name1 = (EditText) findViewById((R.id.name1));
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
        EditText ratio1 = (EditText) findViewById((R.id.value1));
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
                Toast.makeText(getApplicationContext(), "name 1 focus change", Toast.LENGTH_SHORT).show();
                //EditText name1 = (EditText) findViewById((R.id.name1));
                //EditText ratio1 = (EditText) findViewById((R.id.value1));
                //CheckBox check1 = (CheckBox) findViewById(R.id.check1);
                //SettingsData.putRatio(name1.getText().toString(), Float.valueOf(ratio1.getText().toString()), check1.isChecked());

                break;
            case R.id.name2:
                Toast.makeText(getApplicationContext(), "name 2 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name3:
                Toast.makeText(getApplicationContext(), "name 3 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name4:
                Toast.makeText(getApplicationContext(), "name 4 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name5:
                Toast.makeText(getApplicationContext(), "name 5 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.name6:
                Toast.makeText(getApplicationContext(), "name 6 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value1:
                Toast.makeText(getApplicationContext(), "value 1 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value2:
                Toast.makeText(getApplicationContext(), "value 2 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value3:
                Toast.makeText(getApplicationContext(), "value 3 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value4:
                Toast.makeText(getApplicationContext(), "value 4 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value5:
                Toast.makeText(getApplicationContext(), "value 5 focus change", Toast.LENGTH_SHORT).show();
                break;
            case R.id.value6:
                Toast.makeText(getApplicationContext(), "value 6 focus change", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Not sure?", Toast.LENGTH_SHORT).show();
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