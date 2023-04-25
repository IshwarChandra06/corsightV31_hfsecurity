package com.eikona.mata.constants;

public class CorsightDeviceConstants {
	
	public static final String SERVICE = "corsightDeviceService";
	public static final String MODEL_TYPE="Corsight";
	
	//api
	public static final String USER_API_LOGIN= "/auth/login/";
	
	public static final String CAMERA_API="/cameras/";
	public static final String CAMERAS_API_ANALYSIS = "/cameras/analysis/";
	
	public static final String POI_API_DELETE_PERSON="/poi_db/poi/remove/";
	public static final String POI_API_ADD_PERSON= "/poi_db/poi/";
	public static final String POI_API_GET = "/poi_db/poi/get/";
	public static final String POI_API_SYNC= "/poi_db/poi/";
	public static final String POI_API_SYNC_AFTER_ID= "/poi_db/poi/?limit=300&after_id=";
	public static final String POI_API_SEARCH_PERSON= "/poi_db/poi/name_search/";
	
	public static final String HISTORY_API_SYNC="/history/?limit=4";
	public static final String HISTORY_API_SYNC_AFTER_ID="/history/?limit=4&after_id=";
	public static final String HISTORY_API_SEARCH_IMG = "/history/search/img";
	public static final String SERVER_STATUS_API = "/status/";
	public static final String SERVER_START_API = "/start/";
	
	public static final String POI_API_GET_WATCHLIST = "/poi_db/watchlist/";
	
	public static final String POI_API_WATCHLIST_ADD = "/poi_db/poi/watchlist_add/";
	public static final String POI_API_WATCHLIST_REMOVE = "/poi_db/poi/watchlist_remove/";
	public static final String POI_API_WATCHLIST_SYNC ="/poi_db/watchlist/?count_pois=false";
	
	//key
	public static final String EVENT_TYPE = "event_type";
	public static final String APPEARANCE = "appearance";
	public static final String APPEARANCE_DATA ="appearance_data";
	public static final String APPEARANCE_ID ="appearance_id";
	public static final String CAMERA_DATA ="camera_data";
	public static final String CAMERA_ID ="camera_id";
	public static final String CAMERA_DESCRIPTION ="camera_description";
	public static final String MATCH_DATA ="match_data";
	public static final String POI_CONFIDENCE ="poi_confidence";
	public static final String CROP_DATA ="crop_data";
	public static final String FACE_CROP_IMG ="face_crop_img";
	public static final String EVENT_ID ="event_id";
	public static final String FACE_FEATURE_DATA ="face_features_data";
	public static final String MASK_OUTCOME ="mask_outcome";
	public static final String MATCHED="matched";
	public static final String NOT_MASKED = "Not_masked";
	public static final String METADATA ="metadata";
	public static final String MSG ="msg";
	public static final String POI_ID ="poi_id";
	public static final String POIS ="pois";
	public static final String SUCCESS_LIST="success_list";
	public static final String DISPLAY_IMG="display_img";
	public static final String POI_DISPLAY_NAME="poi_display_name";
	public static final String DISPLAY_NAME="display_name";
	public static final String USE_AS_DISPLAY="use_as_display";
	public static final String USE_AS_FACE="use_as_face";
	public static final String DATA="data";
	public static final String MAX_MATCHES="max_matches";
	public static final String MATCHES="matches";
	public static final String MATCH_OUTCOME="match_outcome";
	public static final String WATCHLIST_ID="watchlist_id";
	public static final String CAMERAS="cameras";
	public static final String FROM="from";
	public static final String TILL="till";
	public static final String WATCHLISTS="watchlists";
	public static final String POI_WATCHLISTS="poi_watchlists";
	public static final String UTC_TIME_STARTED="utc_time_started";
	public static final String EVENTS_URL = "events_url";
	public static final String EVENTS_OUTPUTS = "events_outputs";
	public static final String DISPLAY_COLOR = "display_color";
	public static final String DISPLAY_COLOR_BLUE = "#0000FF";
	public static final String IMG = "img";
	public static final String ANALYZE = "analyze";
	public static final String MIN_CONFIDENCE = "min_confidence";
	public static final String WATCHLIST_TYPE = "watchlist_type";
	public static final String PERSON_ID = "person_id";
	public static final String FACE_RECOGNITION_THRESHOLD = "face_recognition_threshold";
	public static final String THRESHOLD_DELTA = "threshold_delta";
	public static final String CAPTURE_ADDRESS = "capture_address";
	public static final String MODE = "mode";
	public static final String VIDEO =  "video";
	public static final String CONFIG = "config";
	public static final String CAPTURE_CONFIG = "capture_config";
	public static final String DESCRIPTION = "description";
	public static final String MIN_DETECTION_WIDTH = "min_detection_width";
	public static final String DISPLAY_RTSP_ADDRESS = "display_rtsp_address";
	
	public static final String EMP_NOT_ADDED_TO_WATCHLIST = "Error:Employee not added to watchlist.";
	public static final String EMP_NOT_REMOVE_FROM_WATCHLIST = "Error:Employee is not removed from watchlist.";
	public static final String FORCE = "force";
	public static final String SAVE_CROP ="save_crop";
	public static final String IMAGE_PAYLOAD ="image_payload";
	public static final String FACE = "face";
	public static final String GENDER_OUTCOME = "gender_outcome";
	public static final String AGE_OUTCOME = "age_group_outcome";

	
	
	
}