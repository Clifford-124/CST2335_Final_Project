package com.project.cst2335.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.project.cst2335.Models.PhotoModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * database helper class to perform database functions
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "PexelsAppdb"; //name of the database
	public static final int DATABASE_VERSION = 1;
	public static String TABLE_PEXELS_PHOTO= "pexels_photo"; // name of the table


	private static final String PHOTO_ROW_ID = "photo_row_id"; //primary key of the table
	private static final String PHOTO_ID = "photo_id"; //column name
	private static final String PHOTO_TINY_URL= "photo_tiny_url"; //column name
	private static final String PHOTO_LARGE_URL= "photo_large_url"; //column name

	Context ctx;

	/**
	 * Contructor for database helper
	 *
	 * @param context context to use
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ctx=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createPexelsPhotoTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropPexelsPhotoTable(db);
		onCreate(db);
	}


	/**
	 * insert the photo in the table
	 *
	 * @param photo photo to insert
	 * @return id of inserted photo
	 */
	public long insertPhoto(PhotoModel photo) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(PHOTO_ID,photo.getId());
		cv.put(PHOTO_TINY_URL,photo.getTinyUrl());
		cv.put(PHOTO_LARGE_URL,photo.getLarge2xUrl());
		long i = db.insert(TABLE_PEXELS_PHOTO, null, cv); //insert the new photo and return the id created
		db.close();
		return i;
	}

	/**
	 * delete photo by photo id
	 *
	 * @param id id of photo to delete
	 * @return if successfully deleted
	 */
	public boolean deletePhotoByID(int id) {
		SQLiteDatabase db = getWritableDatabase();
		boolean res=db.delete(TABLE_PEXELS_PHOTO, PHOTO_ID+"="+id, null)>0;
		db.close();
		return res;
	}


	/**
	 *
	 * drop table
	 *
	 * @param db database to use
	 */
	private void dropPexelsPhotoTable(SQLiteDatabase db ){
		if(!db.isOpen()){
			db=getWritableDatabase();
		}
		db.execSQL("DROP TABLE  IF EXISTS '" + TABLE_PEXELS_PHOTO + "'");
	}

	/**
	 * create table
	 *
	 * @param db database to use
	 */
	private void createPexelsPhotoTable(SQLiteDatabase db ){
		if(!db.isOpen()){
			db=getWritableDatabase(); //returns a SQLiteDtabase object that lets you insert, update and delete.
		}
		db.execSQL("CREATE TABLE '"
				+ TABLE_PEXELS_PHOTO
				+ "' ("+PHOTO_ROW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
				""+PHOTO_ID+" INTEGER," +
				""+PHOTO_LARGE_URL+" TEXT," +
				""+PHOTO_TINY_URL+" TEXT)");
	}

	/**
	 * get photo by id
	 *
	 * @param id id of photo to get
	 * @return photo returned by id
	 *
	 */
	public PhotoModel getPhotoByID(int id)
	{
		SQLiteDatabase db = getReadableDatabase();//returns a database that is read-only
		String query = "SELECT *"
				+ " FROM " + TABLE_PEXELS_PHOTO + ""
				+ " WHERE " + PHOTO_ID + " = '" + id + "' ;";
		Cursor cursor = db.rawQuery(query, null);
		PhotoModel photo = null;
		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		if (cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {

				do {
					int photo_id = cursor.getInt(cursor.getColumnIndex(PHOTO_ID));//0
					String tiny_url = cursor.getString(cursor.getColumnIndex(PHOTO_TINY_URL));//1
					String large_url = cursor.getString(cursor.getColumnIndex(PHOTO_LARGE_URL));//2

					photo = new PhotoModel();
					photo.setId(photo_id);
					photo.setTinyUrl(tiny_url);
					photo.setLarge2xUrl(large_url);
				} while (cursor.moveToNext());

			}

		}
		db.close();
		cursor.close();
		return photo;
	}

	/**
	 * get all photos in the table
	 *
	 * @return list of photos in the table
	 */
	public List<PhotoModel> getAllPhotos()
	{
		SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT *"
        		+" FROM "+TABLE_PEXELS_PHOTO+""
        		+" ;";
	    Cursor cursor = db.rawQuery(query,null);
		List<PhotoModel> List = new ArrayList<PhotoModel>();

		if (cursor==null || cursor.getCount()==0)
	    {
	    	cursor.close();
	    	db.close();
			return List;

	    }
		if (cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {

				do {
					int photo_id = cursor.getInt(cursor.getColumnIndex(PHOTO_ID));//0
					String tiny_url = cursor.getString(cursor.getColumnIndex(PHOTO_TINY_URL));//1
					String large_url = cursor.getString(cursor.getColumnIndex(PHOTO_LARGE_URL));//2

					PhotoModel photo = new PhotoModel();
					photo.setId(photo_id);
					photo.setTinyUrl(tiny_url);
					photo.setLarge2xUrl(large_url);
					List.add(photo);
				} while (cursor.moveToNext());

			}

		}
		db.close();
		cursor.close();
		return List;
	}

}
