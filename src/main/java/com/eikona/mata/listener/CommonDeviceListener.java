package com.eikona.mata.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.DeviceListenerConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.service.impl.beward.BewardListenerServiceImpl;
import com.eikona.mata.service.impl.corsight.CorsightListenerServiceImpl;
import com.eikona.mata.service.impl.hfsecurity.HFSecurityListenerServiceImpl;
import com.eikona.mata.service.impl.model.SseServiceImpl;
import com.eikona.mata.service.impl.uniview.UnvListenerServiceImpl;

@RestController
public class CommonDeviceListener {
	
	@Autowired
	private CorsightListenerServiceImpl corsightListenerServiceImpl;
	
	@Autowired
	private HFSecurityListenerServiceImpl hfSecurityService;

	@Autowired
	private UnvListenerServiceImpl heartBeatService;

	@Autowired
	private BewardListenerServiceImpl bewardService;

	@Autowired
	private SseServiceImpl sseService;
	
	//HFSecurity Heartbeat
	@PostMapping(path ="/hfsecurity/heartbeat")
	public  ResponseEntity<String> hfSecurityHeartReportInfo(@RequestParam("ip") String ipAddress,
			@RequestParam("deviceKey") String deviceKey, @RequestParam("time") String time,
			@RequestParam("version") String version, 
			@RequestParam("faceCount") String faceCount, @RequestParam("personCount") String personCount) throws ParseException {
        
		hfSecurityService.saveDeviceInfoFromResponse(ipAddress,deviceKey,time,version,faceCount,personCount);
		
		System.out.println("Heartbeat received!!");
		JSONObject response = new JSONObject();

		response.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
    }
	
	//HFSecurity Eventlog
	@PostMapping(path ="/hfsecurity/eventlog")
	public String hfSecurityEventLogInfo(@RequestParam("ip") String ipAddress,
			@RequestParam("deviceKey") String deviceKey, @RequestParam("time") String time, @RequestParam("type") String type, 
			@RequestParam("searchScore") String searchScore,@RequestParam("livenessScore") String livenessScore,@RequestParam("personId") String personId,
			@RequestParam("mask") String mask,@RequestParam("imgBase64") String imgBase64) throws ParseException {
		
		hfSecurityService.saveTransactionInfoFromResponse(ipAddress,deviceKey,time,type,searchScore,livenessScore,mask,imgBase64,personId);
		
		System.out.println("Event log received!!");
		JSONObject response = new JSONObject();
		response.put("result", 1);
		response.put("success", true);

		return response.toString();
		
}
	
	@PostMapping(path = DeviceListenerConstants.CORSIGHT_LISTENER_API)
	public ResponseEntity<String> corsightHeartReportInfo(@RequestBody JSONObject request) throws ParseException {

		corsightListenerServiceImpl.corsightHeartReportInfo(request);

		JSONObject response = new JSONObject();

		response.put(ApplicationConstants.STATUS, ApplicationConstants.SUCCESS);

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}
	//Beward Listener
	@PostMapping(path = DeviceListenerConstants.BEWARD_HEARTBEAT_API)
	public ResponseEntity<String> bewardHeartReportInfo(@RequestBody String request, HttpServletRequest httpRequest)
			throws ParseException {
		bewardService.getDeviceInfoFromResponse(request);
		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject response = new JSONObject();
		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.BEWARD_HEARTBEAT_API);
		response.put(DeviceListenerConstants.CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime));

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}

	@PostMapping(path = DeviceListenerConstants.BEWARD_TRANSACTION_API)
	public ResponseEntity<String> bewardVerifyReportInfo(@RequestBody String request) throws ParseException {

		System.out.println(request.toString());

		bewardService.saveTransactionInfo(request);

		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject response = new JSONObject();
		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.BEWARD_TRANSACTION_API);
		response.put(DeviceListenerConstants.CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime));

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}

	//Uniview Listener
	@PostMapping(path = DeviceListenerConstants.UNV_HEARTBEAT_API)
	public ResponseEntity<String> unvHeartReportInfo(@RequestBody String request, HttpServletRequest httpRequest)
			throws ParseException {

		 heartBeatService.saveDeviceInfo(request, httpRequest.getRemoteHost());

		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject response = new JSONObject();
		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.UNV_HEARTBEAT_API);
		response.put(DeviceListenerConstants.CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime));

		System.out.println(response.toString());

		return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
	}

	@PostMapping(path = DeviceListenerConstants.UNV_TRANSACTION_API, produces = ApplicationConstants.APPLICATION_JSON, consumes = ApplicationConstants.MIME_TYPE_TEXT)
	public ResponseEntity<String> unvPersonVerification(@RequestBody String request) throws ParseException {


		heartBeatService.saveTransactionInfo(request);

		String currentTime = new SimpleDateFormat(ApplicationConstants.DATE_TIME_FORMAT_OF_US).format(new Date());

		JSONObject requestJson = new JSONObject(request);
		JSONObject masterResponse = new JSONObject();
		JSONObject response = new JSONObject();

		response.put(DeviceListenerConstants.RESPONSE_URL, DeviceListenerConstants.UNV_TRANSACTION_API);
		response.put(DeviceListenerConstants.STATUS_CODE, NumberConstants.ZERO);
		response.put(DeviceListenerConstants.STATUS_STRING, DeviceListenerConstants.SUCCEEED);
		response.put(DeviceListenerConstants.DATA, new JSONObject().put(DeviceListenerConstants.TIME, currentTime).put(DeviceListenerConstants.RECORD_ID, requestJson.get(DeviceListenerConstants.SEQ)));

		masterResponse.put(DeviceListenerConstants.RESPONSE, response);

		System.out.println(masterResponse.toString());

		return new ResponseEntity<String>(masterResponse.toString(), HttpStatus.OK);
	}

	@GetMapping(DeviceListenerConstants.WEBSOCKET_EVENT_RECEIVER_API)
	public @ResponseBody SseEmitter getEmitter() {
		return sseService.registerClient();
	}
}
