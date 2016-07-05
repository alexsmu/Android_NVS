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

public class SettingsData {
    private static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor = null;
    public static Context mContext = null;

    public SettingsData(Context context) {
        mContext = context;
        prefs = mContext.getSharedPreferences(mContext.getString(R.string.preference_file), Context.MODE_PRIVATE);
    }

    public static String getString(String name, String def_val){
        return (prefs == null ? def_val : prefs.getString(name, def_val));
    }

    public static void setString(String name, String val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putString(name, val);
            editor.apply();
        }
    }

    public static float getFloat(String name, float def_val){
        return (prefs == null ? def_val : prefs.getFloat(name, def_val));
    }

    public static void setFloat(String name, float val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putFloat(name, val);
            editor.apply();
        }
    }

    public static boolean isChecked(String name, boolean def_val){
        return (prefs == null ? def_val : prefs.getBoolean(name, def_val));
    }

    public static void setChecked(String name, boolean val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putBoolean(name, val);
            editor.apply();
        }
    }

    public static void setFirstRun(boolean val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putBoolean("firstrun", val);
            editor.apply();
        }
    }

    public static boolean isFirstRun() {
        return prefs == null || prefs.getBoolean("firstrun", true);
    }
}