package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import static android.content.Context.MODE_PRIVATE;
import static tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData_Variable.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShareData {

    private final Context context;
    private ArrayList<CheckModel> checkModels;

    public ShareData(Context context) {
        this.context = context;
    }
    public ShareData(Context context, ArrayList<CheckModel> checkModels) {
        this.context = context;
        this.checkModels = checkModels;
    }

    /**
     **************** Login Activity *****************
     */

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

    /**
     **************** Guide Activity *****************
     */

    public void saveSpotTitle(String spotTitle) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTTITLE, spotTitle);
        editor.apply();
    }

    public String getSpotTitle() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SPOTTITLE, "");
    }

    public void saveSpotInfo(String spotInfo) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTINFO, spotInfo);
        editor.apply();
    }

    public String getSpotInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SPOTINFO, "");
    }

    public void saveSpotImage(String spotImage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTIMAGE, spotImage);
        editor.apply();
    }

    public String getSpotImage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SPOTIMAGE, "");
    }

    public void saveSpotUrl(String spotUrl) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SPOTURL, spotUrl);
        editor.apply();
    }

    public String getSpotUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SPOTURL, "");
    }

    /**
     **************** Check Activity *****************
     */

    public void saveData(ArrayList<CheckModel> checkModels) {
        this.checkModels = checkModels;

        Log.e("saveData", checkModels.toString());

        SharedPreferences sharedPreferences = context.getSharedPreferences("spotData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(checkModels);

        editor.putString(SPOTNAME, json);
        editor.apply();
    }

    public ArrayList<CheckModel> loadData() {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("spotData", MODE_PRIVATE);

            Gson gson = new Gson();
            String json = sharedPreferences.getString(SPOTNAME, null);

            Type type = new TypeToken<ArrayList<CheckModel>>() {
            }.getType();

            checkModels = gson.fromJson(json, type);

            if (checkModels == null) {
                checkModels = new ArrayList<>();
            }
        }

        return checkModels;
    }
}
