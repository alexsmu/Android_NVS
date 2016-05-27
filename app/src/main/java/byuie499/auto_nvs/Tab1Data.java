package byuie499.auto_nvs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Tab1Data {
    private static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor = null;
    private static Set<String> defaultRatios = new HashSet<>();
    private static Set<String> ratio_names = new HashSet<>();
    private static Set<String> temp = null;
    private static Set<String> disabled_ratio_set = new HashSet<>();
    private static Set<String> enabled_ratio_set = new HashSet<>();
    private static String[] disabled_ratios = null;
    public static String[] enabled_ratios = null;
    private static ArrayAdapter<String> disabled_adapter = null;
    private static HashMap<String, Boolean> checkBoxes = new HashMap<>();
    private static HashMap<String, String> ratios = new HashMap<>();
    public static Context mContext = null;
    public static ListPopupWindow dropdown = null;
    public static EditText edit_ratio = null;
    public static EditText edit_val = null;
    public static CheckBox edit_cb = null;

    public Tab1Data(Context context) {

        mContext = context;
        prefs = mContext.getSharedPreferences(mContext.getString(R.string.preference_file), Context.MODE_PRIVATE);
        checkBoxes.put("Vibration", prefs.getBoolean("c_Vibration", true));
        checkBoxes.put("Noise", prefs.getBoolean("c_Noise", false));
        defaultRatios.add("Differential gear ratio");
        defaultRatios.add("Crankshaft pulley diameter");
        defaultRatios.add("Power steering pulley diameter");
        defaultRatios.add("Tire size");
        temp = prefs.getStringSet("ratios", defaultRatios); // Cannot modify set instance returned by this call

        for (String s : temp) {
            ratio_names.add(s);
            ratios.put(s, prefs.getString("r_" + s, "1.0"));
            checkBoxes.put(s, prefs.getBoolean("c_" + s, false));
            if (checkBoxes.get(s))
                enabled_ratio_set.add(s);
            else
                disabled_ratio_set.add(s);
        }

        dropdown = new ListPopupWindow(mContext);
        disabled_ratios = disabled_ratio_set.toArray(new String[disabled_ratio_set.size()]);
        enabled_ratios = enabled_ratio_set.toArray(new String[enabled_ratio_set.size()]);
        disabled_adapter = new ArrayAdapter<>(mContext, R.layout.ratios_dropdown, disabled_ratios);
        dropdown.setAdapter(disabled_adapter);
        dropdown.setOnItemClickListener(selectRatio);
    }

    public static boolean getBool(String name, boolean def_val){
        if (checkBoxes.containsKey(name))
            return checkBoxes.get(name);
        else
            return def_val;
    }

    public static void putBool(String name, boolean val) {
        editor = prefs.edit();
        editor.putBoolean("c_" + name, val);
        editor.apply();
    }

    public static String getRatioVal(String name) {
        if (ratios.containsKey(name))
            return ratios.get(name);
        else
            return "1.0";
    }

    public static boolean isRatioEN(String name) {
        return getBool(name, false);
    }

    public static void putRatio(String name, String val, boolean EN) {
        ratios.put(name, val);
        checkBoxes.put(name, EN);
        ratio_names.add(name);
        update_dropdown(name, EN);
        if (EN) {
            if (enabled_ratio_set.add(name))
                enabled_ratios = enabled_ratio_set.toArray(new String[enabled_ratio_set.size()]);
        }
        else {
            if (enabled_ratio_set.remove(name))
                enabled_ratios = enabled_ratio_set.toArray(new String[enabled_ratio_set.size()]);
        }
        editor = prefs.edit();
        editor.putString("r_" + name, val);
        editor.putBoolean("c_" + name, EN);
        editor.putStringSet("ratios", ratio_names);
        editor.apply();
        //Log.d(" enabled_ratios[0]", enabled_ratios[0] );
        //Log.d(" ratio_names.toString()", ratio_names.toString());
    }

    public static void update_dropdown(String name, boolean EN) {
        if (EN)
            disabled_adapter.remove(name);
        else
            disabled_adapter.add(name);
    }

    public static void removeRatio(String name) {
        if (!isDefaultRatio(name)){
            ratios.remove(name);
            checkBoxes.remove(name);
            ratio_names.remove(name);
            enabled_ratio_set.remove(name);
            editor = prefs.edit();
            editor.remove("r_" + name);
            editor.remove("c_" + name);
            editor.putStringSet("ratios", ratio_names);
            editor.apply();
        }
    }

    public static boolean isDefaultRatio(String name) {
        return defaultRatios.contains(name);
    }

    public static boolean definedCheckBox(String name) {
        return checkBoxes.containsKey(name);
    }

    public static void setCheckBox(String name, boolean val) {
        checkBoxes.put(name, val);
        putBool(name,val);

    }

    public static void setFirstRun(boolean val) {
        putBool("firstrun", val);
    }

    public static boolean isFirstRun() {
        return prefs.getBoolean("firstrun", true);
    }

    public static AdapterView.OnItemClickListener selectRatio = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String name = disabled_ratios[position];
            String val = getRatioVal(name);
            boolean EN = false;
            edit_ratio.setText(name);
            edit_val.setText(val);
            edit_cb.setChecked(EN);
            dropdown.dismiss();
        }
    };

}