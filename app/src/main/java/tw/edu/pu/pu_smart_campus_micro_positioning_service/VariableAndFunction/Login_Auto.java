package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.Login_Variable.KEY_ID;
import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.Login_Variable.KEY_PASSWORD;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Login_Auto {

    private final Context context;

    public Login_Auto(Context context) {
        this.context = context;
    }

    public void saveID(String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ID, id);
        editor.apply();
    }

    public String getID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_ID, null);
    }

    public void savePassword(String password) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getPassword() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_PASSWORD, null);
    }
}
