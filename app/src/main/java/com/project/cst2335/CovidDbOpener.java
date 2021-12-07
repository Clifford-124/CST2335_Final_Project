package com.project.cst2335;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**This is the class that create the database
 *
 * */
public class CovidDbOpener extends SQLiteOpenHelper {


    public static final String name  = "Database";
    public static final int version = 1;
    public static final String Table_name = "CovidTracker";
    public static final String Col_id = "_id";
    public static final String Col_date = "date";
    public static final String Col_total_cases = "total_cases";
    public static final String Col_total_fatalities = "total_fatalities";
    public static final String Col_total_hospitalizations = "total_hospitalizations";
    public static final String Col_total_vaccinations = "total_vaccinations";
    public static final String Col_total_recoveries = "total_recoveries";



    public CovidDbOpener(Context context) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL( String.format( "Create table %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s  INTEGER, %s INTEGER,%s INTEGER,%s INTEGER,%s INTEGER );"
                , Table_name, Col_id,Col_date, Col_total_cases, Col_total_fatalities,Col_total_hospitalizations,Col_total_vaccinations,Col_total_recoveries ) );
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "Drop table if exists " + Table_name ); //deletes the current data
        onCreate (db);
    }
}