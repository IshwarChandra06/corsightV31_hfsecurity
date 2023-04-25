package com.eikona.mata.constants;

public class BioSecurityConstants {
	public static final String  API_ADD_PERSON ="/api/person/add";
	
	public static final String  MESSAGE ="message";
	public static final String  DATA ="data";
	
	public static final String  ACC_LEVEL_SYNC_API ="/api/accLevel/list?pageNo=1&pageSize=100&access_token=";
	
	public static final String  PERSON_ADD_JSON ="{\n"
						+ "  \"name\": \"%s\",\n"
						+ "  \"pin\": \"%s\",\n"
						+ "  \"hireDate\": \"%s\"\n"
						+ "}";
	
	public static final String  PERSON_ADD_ACCESSELEVEL_IDS_JSON ="{\n"
						+ "  \"accLevelIds\": \"%s\",\n"
						+ "  \"pin\":  \"%s\"\n"
						+ "}";
}
