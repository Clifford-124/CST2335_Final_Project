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

    public static final String API_KEY = "Y0ZMTMhF0Wdxnawy2m5Q";
    public static final String VEHICLE_MODELS_URL = "https://www.carboninterface.com/api/v1/vehicle_makes";
    public static final String VEHICLE_ESTIMATES_URL = "https://www.carboninterface.com/api/v1/estimates";

	public static final String ARG_MODEL_ID   ="model_id";
	public static final String ARG_MODEL_NAME ="model_name";
	public static final String ARG_VEHICLE_MAKE ="vehicle_make";
	public static final String ARG_DISPLAY    ="display";
	public static final String ARG_DISTANCE   ="distance";
	public static final String ARG_DISTANCE_UNIT   ="distance_unit";

	public static final String OWL_URL_GET_WORDS=  "https://owlbot.info/api/v4/dictionary/";
	public static final String OWL_HEADER=  "Authorization";
	public static final String OWL_API_KEY= "Token 3d4b3b4eeb834bb796853904f30d2e1baaeb9cf3";

}
