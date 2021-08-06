package tw.edu.pu.pu_smart_campus_micro_positioning_service.Database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "spot.db";

    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table spot(major TEXT primary key, spotName TEXT, spotImage BLOB, spotInfo TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists spot");
    }

    public void insertSpotData(String major, String spotName, byte[] img, String spotInfo){
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("major",major);
        contentValues.put("spotName",spotName);
        contentValues.put("image",img);
        contentValues.put("spotInfo",spotInfo);

        myDb.insert("spot",null,contentValues);
    }
}
