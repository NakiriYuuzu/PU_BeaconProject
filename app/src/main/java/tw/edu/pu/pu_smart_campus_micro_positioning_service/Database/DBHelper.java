package tw.edu.pu.pu_smart_campus_micro_positioning_service.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Login.db";

    Activity activity;

    public DBHelper(@Nullable Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table users(username TEXT primary key, password TEXT, status BOOL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists users");
    }

    public boolean insertData(String username, String password) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("username", username);
        contentValues.put("password", password);

        long result = myDb.insert("users", null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = myDb.rawQuery("Select * from users where username = ?", new String[] {username});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean checkUserPassword(String username, String password) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = myDb.rawQuery("Select * from users where username = ? and password = ?", new String[] {username, password});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean checkUserStatus(String username) {
        boolean check = true;
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("Select * from users where username = " + " + username " + ";", null);

        if (cursor.moveToFirst()) {
            check = cursor.getInt(cursor.getColumnIndex("status")) == 0;
            Log.e("TAG", "LoginStatus" + "\nAccount: " + username + "\nStatus: " + check);
        }
        else {
            Toast.makeText(activity, "failed...", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();

        return check;
    }

    public void changeStatus(String username, int status) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("status", status);

        @SuppressLint("Recycle") Cursor cursor = myDb.rawQuery("Select * from users where username = ?", new String[]{username});

        if (cursor.getCount() > 0) {
            long result = myDb.update("users", contentValues, "username = ?", new String[]{username});
            if (result == -1)
                Log.e("TAG_changeStatus", String.valueOf(result));
        }
    }

    public void changePassword(String username, String password) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("password", password);

        @SuppressLint("Recycle") Cursor cursor = myDb.rawQuery("Select * from users where username = ?", new String[]{username});

        if (cursor.getCount() > 0) {
            long result = myDb.update("users", contentValues, "username = ?", new String[]{username});
            if (result == -1)
                Log.e("TAG_changePassword", String.valueOf(result));
        }
    }
}
