package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.LoginConstants.KEY_ID;
import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.LoginConstants.KEY_PASSWORD;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AutoLogin {

    public AutoLogin() {

    }

    public static boolean saveID(String id, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ID, id);
        editor.apply();
        return true;
    }

    public static String getID(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_ID, null);
    }

    public static boolean savePassword(String password, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
        return true;
    }

    public static String getPassword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(KEY_PASSWORD, null);
    }
}
