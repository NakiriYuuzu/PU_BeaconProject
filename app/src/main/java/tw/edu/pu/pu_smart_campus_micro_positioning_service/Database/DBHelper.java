package tw.edu.pu.pu_smart_campus_micro_positioning_service.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "login.db";

    public DBHelper(@Nullable Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table if not exists spot(spotName TEXT primary key, spotInfo TEXT, url TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists spot");
    }

    public void insertSpotData(String spotName, String spotInfo, String url) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("spotName", spotName);
        contentValues.put("spotInfo", spotInfo);
        contentValues.put("url", url);

        myDb.insert("spot", null, contentValues);
    }

    public String[] fetchSpotData(String data) {
        SQLiteDatabase myDb = this.getReadableDatabase();
        Cursor cursor = myDb.rawQuery("select * from spot", null, null);

        String arr[] = new String[3];

        for (int i = 0; i < cursor.getColumnCount(); i++) {

            cursor.moveToPosition(i);

            if (cursor.getString(0).equals(data)) {
                arr[0] = cursor.getString(0);
                arr[1] = cursor.getString(1);
                arr[2] = cursor.getString(2);
                return arr;
            }

        }
        return arr;
    }
}
