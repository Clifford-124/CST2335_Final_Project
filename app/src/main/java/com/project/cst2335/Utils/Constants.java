package com.project.cst2335.Utils;

/**
 *
 * Constants to use on the app
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class Constants {

	public static final String URL_GET_PHOTOS=  "https://api.pexels.com/v1/search?query=";
	public static final String PEXELS_HEADER=  "Authorization";
	public static final String PEXELS_API_KEY= "563492ad6f91700001000001562a1bd69c0b40df8414fb041a332d30";
	public static final String PEXELS_IMAGE_FOLDER= "images";
	public static final String PEXELS_LARGE_IMAGE= "_large";
	public static final String PEXELS_SMALL_IMAGE= "_small";

	public static final int PEXELS_LOAD_SMALL_IMAGE= 1;
	public static final int PEXELS_LOAD_LARGE_IMAGE= 2;

	public static final int TASK_SAVE_IMAGE= 3;
	public static final int TASK_DELETE_IMAGE= 4;
	public static final int TASK_LOAD_OFFLINE= 2;
	public static final int TASK_LOAD= 1;


	public static final String KEY_SRC=  "src";
	public static final String KEY_LARGE=  "large";
	public static final String KEY_LARGE2X=  "large2x";
	public static final String KEY_MEDIUM=  "medium";
	public static final String KEY_ORIGINAL=  "original";
	public static final String KEY_ID=  "id";
	public static final String KEY_PHOTOS=  "photos";
	public static final String KEY_TINY=  "tiny";

	public static final String KEY_PREFS=  "search_term";
	public static final String DEFAULT_SEARCH_TERM=  "cars";
	public static final String PREFS_NAME=  "pexelsPrefs";




}
