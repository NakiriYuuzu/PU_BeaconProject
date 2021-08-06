package tw.edu.pu.pu_smart_campus_micro_positioning_service.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "login.db";

    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table spot(spotName TEXT primary key, spotImage BLOB, spotInfo TEXT, url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists spot");
    }

    public void insertSpotData(String spotName, byte[] img, String spotInfo, String url){
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("spotName",spotName);
        contentValues.put("image",img);
        contentValues.put("spotInfo",spotInfo);
        contentValues.put("url",url);

        myDb.insert("spot",null,contentValues);
    }
}
