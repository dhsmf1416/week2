package com.example.q.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String TAG = this.getClass().getSimpleName();
    private static final String DATABASE_NAME = "emp_db";
    private static final int DATABASE_VERSION = 1;

    //TABLE NAMES
    private static final String TABLE_EMP = "myPicture";

    /* Keys for Table myPictures */
    private static final String KEY_ID = "iID";
    private static final String KEY_IMG = "img";

    String CREATE_TABLE_CALL = "CREATE TABLE " + TABLE_EMP + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IMG + " BLOB);";
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_CALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMP);
        onCreate(db);
    }

    public long createImage(byte[] img)
    {
        long c;
        System.out.println(img.length);
        System.out.println(CREATE_TABLE_CALL);
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMG,img);
        c = database.insert(TABLE_EMP,null,values);
        database.close();
        return c;
    }
    public Boolean deleteImage(int id)
    {
        SQLiteDatabase database = getWritableDatabase();
        return database.delete(TABLE_EMP,KEY_ID+"="+String.valueOf(id),null)>0;
    }
    public ArrayList<Integer> getAllId()
    {
        String query = "SELECT * FROM " + TABLE_EMP;
        ArrayList<Integer> ids = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query,null);
        if(c != null)
        {
            while(c.moveToNext())
            {
                int x = c.getInt(0);
                ids.add(x);
            }
        }
        return ids;
    }
    public ArrayList<byte[]> getAllImage()
    {
        String query = "SELECT * FROM " + TABLE_EMP;
        ArrayList<byte[]> images = new ArrayList<byte[]>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query,null);
        if(c != null)
        {
            while (c.moveToNext())
            {

                byte[] x =  c.getBlob(1);
                images.add(x);
            }
        }
        return images;
    }

}
