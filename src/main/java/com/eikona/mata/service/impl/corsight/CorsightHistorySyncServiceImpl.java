package com.eikona.mata.service.impl.corsight;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.eikona.mata.entity.Transaction;
import com.eikona.mata.repository.TransactionRepository;
import com.eikona.mata.util.CorsightAuth;
import com.eikona.mata.util.SavingCropImageUtil;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Service
@EnableScheduling
public class CorsightHistorySyncServiceImpl {

	@Autowired
	private CorsightAuth logIn;

	@Autowired
	private TransactionRepository transactionRepository;

	@Value("${corsight.host.url}")
	private String host;

	@Value("${corsight.history.port}")
	private String portHistory;

	@Value("${corsight.user.port}")
	private String portUser;

	@Autowired
	private SavingCropImageUtil savingCropImageUtil;

	//@Scheduled(fixedDelay = 30000)
	public void getHistoryOfCorsightThroughScheduler() {
		String afterId = "";
		syncCorsightHistory(afterId);
	}

	@SuppressWarnings("unchecked")
	public void syncCorsightHistory(String afterId) {
		try {
			String authHeader = "Bearer " + logIn.getToken();
			JSONObject requestObj = new JSONObject();
			JSONArray cameraArray = new JSONArray();
			JSONArray watchListArray = new JSONArray();

			JSONObject matchedObj = new JSONObject();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = format.format(new Date());
			Calendar fromCal = Calendar.getInstance();
			fromCal.setTime(format.parse(dateStr));
			fromCal.add(Calendar.DATE, -1);
			fromCal.set(Calendar.HOUR, 0);
			fromCal.set(Calendar.MINUTE, 0);
			fromCal.set(Calendar.SECOND, 0);

			System.out.println(fromCal.getTime());

			Calendar toCal = Calendar.getInstance();
			toCal.setTime(format.parse(dateStr));
			toCal.set(Calendar.HOUR, 0);
			toCal.set(Calendar.MINUTE, 0);
			toCal.set(Calendar.SECOND, 0);

			System.out.println(toCal.getTime());

			long from = fromCal.getTimeInMillis() / 1000;
			long to = toCal.getTimeInMillis() / 1000;

			cameraArray.add("c445f45c-d06d-11ec-8903-a4bf01079b7f");

			matchedObj.put("match_outcome", "matched");
			matchedObj.put("watchlist_id", "d1ab3c92-d06d-11ec-a6b0-a4bf01079b7f");

			watchListArray.add(matchedObj);

			requestObj.put("cameras", cameraArray);
			requestObj.put("from", from);
			requestObj.put("till", to);
			requestObj.put("watchlists", watchListArray);

			String historyUrl = "https://" + host + ":" + portHistory;
			String addHistoryUrl = null;
			if (afterId.isEmpty())
				addHistoryUrl = "/history/?limit=4";
			else
				addHistoryUrl = "/history/?limit=4&after_id=" + afterId;

			SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();

			HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

			WebClient webClient = WebClient.builder().baseUrl(historyUrl + addHistoryUrl)
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
					.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
					.clientConnector(new ReactorClientHttpConnector(httpClient)).build();

			ResponseEntity<JSONObject> response = webClient.post().bodyValue(requestObj).retrieve()
					.toEntity(JSONObject.class).block();

			String responeData = response.getBody().toString();

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponse = (JSONObject) jsonParser.parse(responeData);
			JSONObject dataObject = (JSONObject) jsonResponse.get("data");
			JSONArray jsonArray = (JSONArray) dataObject.get("matches");
			List<Transaction> eventList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject currobj = (JSONObject) jsonArray.get(i);
				JSONObject appearanceDataObject = (JSONObject) currobj.get("appearance_data");
				Transaction event = new Transaction();
				Transaction eventReport = transactionRepository.findByAppearanceId((String) appearanceDataObject.get("appearance_id"));
				if (eventReport == null) {

					event.setAppearanceId((String) appearanceDataObject.get("appearance_id"));

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
					Timestamp ts = new Timestamp((long) ((double) appearanceDataObject.get("utc_time_started")));
					Date punchDate = new Date(ts.getTime() * 1000);

					String dateString = dateFormat.format(punchDate);
					event.setPunchDateStr(dateString);
					event.setPunchDate(punchDate);
					event.setPunchTimeStr(timeFormat.format(punchDate));

					JSONObject cameraDataObject = (JSONObject) currobj.get("camera_data");
					event.setDeviceId((String) cameraDataObject.get("camera_id"));
					event.setDeviceName((String) cameraDataObject.get("camera_description"));
					
					JSONObject matchDataObject = (JSONObject) currobj.get("match_data");

					JSONArray watchArray = (JSONArray) matchDataObject.get("watchlists");
					for (int j = 0; j < watchArray.size(); j++) {
						JSONObject watchobj = (JSONObject) watchArray.get(j);
						event.setWatchlistId((String) watchobj.get("watchlist_id"));
						event.setWatchlistName((String) watchobj.get("display_name"));
					}
					if (null != matchDataObject.get("poi_id")) {
						event.setPoiId((String) matchDataObject.get("poi_id"));
					}
					if (null != matchDataObject.get("poi_display_name")) {
						event.setName((String) matchDataObject.get("poi_display_name"));
						event.setEmpId((String) matchDataObject.get("poi_display_name"));
					}

					event.setPoiConfidence((double) matchDataObject.get("poi_confidence"));

					JSONObject cropDataObject = (JSONObject) currobj.get("crop_data");

					if (null != cropDataObject) {
						String imagePath = savingCropImageUtil.saveCropImages((String) cropDataObject.get("face_crop_img"), event);
						event.setCropimagePath(imagePath);
					}
					event.setEventId((String) currobj.get("event_id"));

					JSONObject faceDataObject = (JSONObject) currobj.get("face_features_data");

					event.setMaskStatus((String) faceDataObject.get("mask_outcome"));

					if ("Not_masked".equalsIgnoreCase(event.getMaskStatus()))
						event.setWearingMask(true);
					else
						event.setWearingMask(false);
					
					eventList.add(event);
					
			}

				}
			
			transactionRepository.saveAll(eventList);
			if (!eventList.isEmpty()) {
				afterId = eventList.get(eventList.size() - 1).getAppearanceId();

				syncCorsightHistory(afterId);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
