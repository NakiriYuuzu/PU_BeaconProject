package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData_Variable.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ShareData {

    private final Context context;

    public ShareData(Context context) {
        this.context = context;
    }

    public void saveUID(String uid) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UID, uid);
        editor.apply();
    }

    public String getUID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(UID, "");
    }

    public void saveNAME(String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public String getNAME() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(NAME, "");
    }

    public void saveROLE(String role) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ROLE, role);
        editor.apply();
    }

    public String getROLE() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(ROLE, "0");
    }

    public void saveTOKEN(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public String getTOKEN() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(TOKEN, "");
    }
}
