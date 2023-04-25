package com.eikona.mata.constants;

public final class BewardDeviceConstants {
	
	public static final String  MODEL_TYPE ="BFRC"; 
	
	public static final String  SERVICE = "bewardDeviceService";
	
	public static final String  INFO = "info";
	public static final String  PERSON_NUM = "PersonNum";
	public static final String  LIST ="List";
	public static final String  CUSTOMIZE_ID ="CustomizeID";
	public static final String  ID_CARD ="IdCard";
	public static final String  GENDER ="Gender";
	public static final String  NAME ="Name";
	public static final String  PIC_INFO ="Picinfo";
	public static final String  DEVICE_ID ="DeviceID";
	public static final String  TIME ="Time";
	public static final String  IP ="Ip";
	public static final String  CREATE_TIME ="CreateTime";
	public static final String  TEMPERATURE ="Temperature";
	public static final String  IS_NO_MASK ="isNoMask";
	public static final String  SNAP_PIC ="SanpPic";
	public static final String  SEARCH_INFO = "SearchInfo";
	public static final String  CODE = "code";
	public static final String  RESULT = "Result";
	public static final String  DETAIL = "Detail";

	
	public static final String  CAMERA_API_BASIC_INFO = "/action/GetSysParam";
	public static final String  PERSON_API_DELETE = "/action/DeletePerson";
	public static final String  PERSON_API_SEARCH = "/action/SearchPerson";
	public static final String  PERSON_API_DELETE_ALL = "/action/DeleteAllPerson";
	public static final String  PERSON_API_ADD = "/action/AddPerson";
	public static final String  PERSON_API_EDIT = "/action/EditPerson";
	public static final String  TRANSACTION_API_SEARCH= "/action/SearchControl";
	public static final String  PERSON_API_SEARCH_ALL ="/action/SearchPersonNum";
	public static final String  PERSON_API_GET_ALL_PERSON ="/action/SearchPersonList";
	public static final String  OPEN_DOOR_API ="/action/OpenDoor";
	
	public static final String  DELETE_PERSON_JSON ="{\r\n"
			+ "\"operator\": \"DeletePerson\",\r\n"
			+ "\"info\": {\r\n"
			+ "\"DeviceID\":%d,\r\n"
			+ "\"TotalNum\": 1,\r\n"
			+ "\"IdType\": 2,\r\n"
			+ "\"PersonUUID\":[\"%s\"]\r\n"
			+ "}\r\n"
			+ "}";
	public static final String  SEARCH_PERSON_JSON ="{"
			+ "\"operator\": \"SearchPerson\","
			+ "\"info\": {"
			+ "\"DeviceID\":\"%s\","
			+ "\"SearchType\":1,"
			+ "\"SearchID\": \"%s\",\r\n"
			+ "\"Picture\":1"
			+ "}"
			+ "}";
	public static final String  DELETE_ALL_PERSON_JSON ="{\r\n"
			+ "\"operator\": \"DeleteAllPerson\",\r\n"
			+ "\"info\":\r\n"
			+ "{\r\n"
			+ "\"DeleteAllPersonCheck\": 1\r\n"
			+ "}\r\n"
			+ "}";
	public static final String  ADD_PERSON_JSON ="{\r\n"
			+ "\"operator\": \"AddPerson\",\r\n"
			+ "\"info\": {\r\n"
			+ "\"DeviceID\":%d,\r\n"
			+ "\"PersonType\": 0,\r\n"
			+ "\"Name\":  \"%s\",\r\n"
			+ "\"Gender\": \"%s\",\r\n"
			+ "\"Nation\":1,\r\n"
			+ "\"CardType\":0,\r\n"
			+ "\"IdCard\": \"%s\",\r\n"
			+ "\"MjCardFrom\": 2,\r\n"
			+ "\"WiegandType\":1,\r\n"
			+ "\"CardMode\":0,\r\n"
			+ "\"MjCardNo\": %d,\r\n"
			+ "\"Tempvalid\": 0,\r\n"
			+ "\"CustomizeID\": %d,\r\n"
			+ "\"PersonUUID\": \"%s\",\r\n"
			+ "\"isCheckSimilarity\": 0\r\n"
			+ "},\r\n"
			+ "\"picinfo\":\"data:image/jpeg;base64,%s\"\r\n"
			+ "}";
	
	public static final String  EDIT_PERSON_JSON ="{\r\n"
			+ "\"operator\": \"EditPerson\",\r\n"
			+ "\"info\": {\r\n"
			+ "\"DeviceID\":%d,\r\n"
			+ "\"PersonType\": 0,\r\n"
			+ "\"Name\":  \"%s\",\r\n"
			+ "\"Gender\": \"%s\",\r\n"
			+ "\"Nation\":1,\r\n"
			+ "\"CardType\":0,\r\n"
			+ "\"IdCard\": \"%s\",\r\n"
			+ "\"MjCardFrom\": 2,\r\n"
			+ "\"WiegandType\":1,\r\n"
			+ "\"CardMode\":0,\r\n"
			+ "\"MjCardNo\": %d,\r\n"
			+ "\"Tempvalid\": 0,\r\n"
			+ "\"CustomizeID\": %d,\r\n"
			+ "\"PersonUUID\": \"%s\",\r\n"
			+ "\"isCheckSimilarity\": 0\r\n"
			+ "},\r\n"
			+ "\"picinfo\":\"data:image/jpeg;base64,%s\"\r\n"
			+ "}";
	
	public static final String  SEARCH_TRANSACTION_JSON ="{"
			+ "\"operator\": \"SearchControl\","
			+ "\"info\": {"
			+ "\"DeviceID\": %d,"
			+ "\"BeginTime\": \"%s\","
			+ "\"EndTime\": \"%s\"" 
//				+ "\"Picture\": 2,"
			+ "}"
			+ "}";
	
	public static final String  SEARCH_ALL_PERSON_JSON="{"
			+ "\"operator\": \"SearchPersonNum\","
			+ "\"info\": {"
			+ "\"DeviceID\":%d"
			+ "}"
			+ "}";
	
	public static final String  GET_PERSON_LIST_JSON ="{"
			+ "\"operator\": \"SearchPersonList\","
			+ "\"info\": {"
			+ "\"DeviceID\":%d,"
			+ "\"BeginNO\":%d,"
			+ "\"RequestCount\":%d,"
			+ "\"Picture\":1"
			+ "}"
			+ "}";
	public static final String  OPEN_DOOR_JSON ="{"
			+ "\"operator\": \"OpenDoor\","
			+ "\"info\": {"
			+ "\"DeviceID\":%s,"
			+ "\"Chn\":0,"
			+ "\"status\":0,"
			+ "\"msg\":\"Welcome To Eikona\""
			+ "}"
			+ "}";
	
	
}
