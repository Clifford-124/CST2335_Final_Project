package com.project.cst2335.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.cst2335.Models.CarModel;
import com.project.cst2335.Models.Estimates;
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


	public static final String DATABASE_NAME = "PexelsAppdb";
	public static final int DATABASE_VERSION = 1;
	public static String TABLE_PEXELS_PHOTO= "pexels_photo";
	public static String TABLE_CARBON_DIOXIDE= "carbon_dioxide";


	private static final String PHOTO_ROW_ID = "photo_row_id"; //primary key of the table
	private static final String PHOTO_ID = "photo_id"; //column name
	private static final String PHOTO_TINY_URL= "photo_tiny_url"; //column name
	private static final String PHOTO_LARGE_URL= "photo_large_url"; //column name

	private static final String CARBON_D_ID = "id";
	private static final String CARBON_D_MODEL_NAME = "name";
	private static final String CARBON_D_MODEL_DISTANCE = "distance";
	private static final String CARBON_D_MODEL_DISTANCE_UNIT = "distance_unit";
	private static final String CARBON_D_EST_G = "carbon_g";
	private static final String CARBON_D_EST_LB = "carbon_lb";
	private static final String CARBON_D_EST_KG = "carbon_kg";
	private static final String CARBON_D_EST_MT = "carbon_mt";

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
		createCarbonDioxideTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropPexelsPhotoTable(db);
		dropCarbonDioxideTable(db);
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

	/**
	 * create table
	 *
	 * @param db database to use
	 */
	private void createCarbonDioxideTable(SQLiteDatabase db ){
		if(!db.isOpen()){
			db=getWritableDatabase();
		}
		db.execSQL("CREATE TABLE '"
				+ TABLE_CARBON_DIOXIDE
				+ "' ("+CARBON_D_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
				""+CARBON_D_MODEL_NAME+" TEXT," +
				""+CARBON_D_EST_G+" REAL," +
				""+CARBON_D_MODEL_DISTANCE+" INTEGER," +
				""+CARBON_D_MODEL_DISTANCE_UNIT+" TEXT," +
				""+CARBON_D_EST_LB+" REAL," +
				""+CARBON_D_EST_KG+" REAL," +
				""+CARBON_D_EST_MT+" REAL)");
	}

	/**
	 *
	 * drop CarbonDioxide table
	 *
	 * @param db database to use for deletion
	 */
	private void dropCarbonDioxideTable(SQLiteDatabase db ){
		if(!db.isOpen()){
			db=getWritableDatabase();
		}
		db.execSQL("DROP TABLE  IF EXISTS '" + TABLE_CARBON_DIOXIDE + "'");
	}

	/**
	 * get all models from table
	 *
	 * @return ArrayList of carModels
	 */
	public ArrayList<CarModel> getModels()
	{
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT *"
				+" FROM "+TABLE_CARBON_DIOXIDE+""
				+" ;";
		Cursor cursor = db.rawQuery(query,null);
		ArrayList<CarModel> models = new ArrayList<CarModel>();
		System.out.println("Cusrosr"+cursor.getCount());
		if (cursor==null || cursor.getCount()==0)
		{
			cursor.close();
			db.close();
			return models;
		}
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					int model_id = cursor.getInt(cursor.getColumnIndex(CARBON_D_ID));
					String model_name = cursor.getString(cursor.getColumnIndex(CARBON_D_MODEL_NAME));
					System.out.println(String.valueOf(model_id)+model_name+"");
					CarModel model= new CarModel(model_id+"",model_name);
					models.add(model);
				} while (cursor.moveToNext());
			}
		}
		db.close();
		cursor.close();
		return models;
	}


	/**
	 *
	 * This function will save estimates in db
	 * @param estimate
	 * @return
	 */
	public long saveModelEstimates(Estimates estimate) {

		SQLiteDatabase db = getWritableDatabase();

		ContentValues cv = new ContentValues();

		cv.put(CARBON_D_MODEL_NAME,estimate.getModel_name());
		cv.put(CARBON_D_MODEL_DISTANCE,estimate.getDistance());
		cv.put(CARBON_D_MODEL_DISTANCE_UNIT,estimate.getDistance_unit());
		cv.put(CARBON_D_EST_G,estimate.getCarbon_g());
		cv.put(CARBON_D_EST_KG,estimate.getCarbon_kg());
		cv.put(CARBON_D_EST_LB,estimate.getCarbon_lb());
		cv.put(CARBON_D_EST_MT,estimate.getCarbon_mt());

		long i = db.insert(TABLE_CARBON_DIOXIDE, null, cv);
		db.close();
		return i;
	}

	/**
	 * get model details by id
	 *
	 * @param id id of model to get
	 * @return Estimate object
	 *
	 */
	public Estimates getModelById(int id)
	{
		SQLiteDatabase db = getReadableDatabase();

		String query = "SELECT *"
				+ " FROM " + TABLE_CARBON_DIOXIDE + ""
				+ " WHERE " + CARBON_D_ID + " = '" + id + "' ;";

		Cursor cursor = db.rawQuery(query, null);

		Estimates estimate = null;

		if (cursor == null || cursor.getCount() == 0) {
			cursor.close();
			db.close();
			return null;
		}
		if (cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					String model_name = cursor.getString(cursor.getColumnIndex(CARBON_D_MODEL_NAME));
					Double carbon_g =  cursor.getDouble(cursor.getColumnIndex(CARBON_D_EST_G));
					Double carbon_lb = cursor.getDouble(cursor.getColumnIndex(CARBON_D_EST_LB));
					Double carbon_kg = cursor.getDouble(cursor.getColumnIndex(CARBON_D_EST_KG));
					Double carbon_mt = cursor.getDouble(cursor.getColumnIndex(CARBON_D_EST_MT));
					int distance = cursor.getInt(cursor.getColumnIndex(CARBON_D_MODEL_DISTANCE));
					String distance_unit = cursor.getString(cursor.getColumnIndex(CARBON_D_MODEL_DISTANCE_UNIT));

					estimate = new Estimates(model_name,distance,distance_unit,carbon_g,carbon_lb,carbon_mt,carbon_kg);
				} while (cursor.moveToNext());
			}
		}
		db.close();
		cursor.close();
		return estimate;
	}

	/**
	 * delete estimate by id
	 *
	 * @param id id of model stored in db to delete
	 * @return if successfully deleted
	 */
	public boolean deleteEstimateByID(int id) {
		SQLiteDatabase db = getWritableDatabase();
		boolean res=db.delete(TABLE_CARBON_DIOXIDE, CARBON_D_ID+"="+id, null)>0;
		db.close();
		return res;
	}

}