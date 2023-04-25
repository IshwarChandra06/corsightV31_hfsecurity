package com.eikona.mata.constants;

public class UnvDeviceConstants {
	
	public static final String MODEL_TYPE = "FRWT";
	public static final String SERVICE = "unvDeviceService";
	
	public static final String RESPONSE_CODE = "ResponseCode";
	public static final String RESPONSE = "Response";
	public static final String DEVICE_CODE = "DeviceCode";
	public static final String REF_ID = "RefId";
	public static final String DATA = "Data";
	public static final String TOTAL = "Total";
	public static final String PERSON_LIST = "PersonList";
	public static final String NUM= "Num";
	public static final String PERSON_INFO_LIST = "PersonInfoList";
	public static final String PERSON_CODE = "PersonCode";
	public static final String IMAGE_LIST = "ImageList";
	public static final String PERSON_NAME = "PersonName";
	public static final String PERSON_ID = "PersonID";
	public static final String LAST_CHANGE = "LastChange";
	public static final String TIME = "Time";
	public static final String DEVICE_TYPE = "DeviceType";
	public static final String LIB_MAT_INFO_LIST = "LibMatInfoList";
	public static final String MATCH_PERSON_INFO = "MatchPersonInfo";
	public static final String FACE_LIST = "FaceList";
	public static final String FACE_INFO_LIST = "FaceInfoList";
	public static final String MASK_FLAG = "MaskFlag";
	public static final String TIME_STAMP = "Timestamp";
	public static final String TEMPERATURE = "Temperature";
	public static final String RESULT_CODE = "ResultCode";
	public static final String FACE_IMAGE ="FaceImage";
	
	public final static String DEVICE_API_BASIC_INFO = "%s/LAPI/V1.0/System/DeviceBasicInfo";
	
	public final static String PEOPLE_API_DELETE = "%s/LAPI/V1.0/PeopleLibraries/3/People/%s?LastChange=%s";
	
	public final static String PEOPLE_API_SEARCH_INFO = "%s/LAPI/V1.0/PeopleLibraries/3/People/Info";
	
	public final static String PEOPLE_API_ADD_AND_UPDATE= "%s/LAPI/V1.0/PeopleLibraries/3/People";
	
	
	public final static String PEOPLE_INFO_JSON = "{\n" 
			+ "\"Num\": 0,\n" 
			+ "\"QueryInfos\": [\n" 
			+ "],\n" 
			+ "\"Limit\": 6,\n"
			+ "\"Offset\": %d\n" 
			+ "}";
	
	
	public final static String PEOPLE_SEARCH_JSON= "{\n" 
				+ "\"Num\": 1,\n" 
				+ "\"QueryInfos\": [\n" 
				+ "{\n" 
				+ "\"QryType\": 27,\n"
				+ "\"QryCondition\": 0,\n" 
				+ "\"QryData\": \"%s\"\n" 
				+ "}\n" 
				+ "],\n" 
				+ "\"Limit\": 6,\n" 
				+ "\"Offset\": 0\n" 
				+ "}";
	
	public final static String PEOPLE_ADD_AND_UPDATE_JSON = "{\n" 
			+ "\"Num\": 1,\n" 
			+ "\"PersonInfoList\": [\n" 
			+ "{\n" 
			+ "\"PersonID\": %d,\n" 
			+ "\"LastChange\": %d,\n" 
			+ "\"PersonCode\": \"%s\",\n" 
			+ "\"PersonName\": \"%s \",\n"
			+ "\"Remarks\": \" \",\n" 
			+ "\"TimeTemplateNum\": 0,\n" 
			+ "\"TimeTemplateList\": [{\n"
			+ " \"BeginTime\":0,\n" 
			+ " \"EndTime\":0,\n" 
			+ " \"Index\":0}],\n"
			+ "\"IdentificationNum\": 0,\n" 
			+ "\"IdentificationList\":[\n" 
			+ "{\n" 
			+ "\"Type\": 1,\n"
			+ "\"Number\": \"\"\n" 
			+ "},\n" 
			+ "{\n" 
			+ "\"Type\": 99,\n" 
			+ "\"Number\": \"0\"\n" 
			+ "}\n"
			+ "],\n" 
			+ "\"ImageNum\":1,\n" 
			+ "\"ImageList\": [\n" 
			+ "{\n" 
			+ "\"FaceID\": 1,\n"
			+ "\"Name\": \"%s.jpg\",\n" 
			+ "\"Size\": %d,\n" 
			+ "\"Data\": \"%s\"\n" 
			+ "}" 
			+ "]\n" 
			+ "}\n" 
			+ "]\n" 
			+ "}";
}
