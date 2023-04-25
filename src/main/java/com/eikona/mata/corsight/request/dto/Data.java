package com.eikona.mata.corsight.request.dto;

public class Data {
	
	private String event_type;
	
	private String event_id;
	
	private double msg_send_timestamp;
	
	private CameraData camera_data;
	
	private FrameData frame_data;
	
	private AppearanceData appearance_data;
	
	private CropData crop_data;
	
	private MatchData match_data;
	
	private FaceFeaturesData face_features_data;
	
	private Updates updates;
	
	private int trigger;

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public double getMsg_send_timestamp() {
		return msg_send_timestamp;
	}

	public void setMsg_send_timestamp(double msg_send_timestamp) {
		this.msg_send_timestamp = msg_send_timestamp;
	}

	public CameraData getCamera_data() {
		return camera_data;
	}

	public void setCamera_data(CameraData camera_data) {
		this.camera_data = camera_data;
	}

	public FrameData getFrame_data() {
		return frame_data;
	}

	public void setFrame_data(FrameData frame_data) {
		this.frame_data = frame_data;
	}

	public AppearanceData getAppearance_data() {
		return appearance_data;
	}

	public void setAppearance_data(AppearanceData appearance_data) {
		this.appearance_data = appearance_data;
	}

	public CropData getCrop_data() {
		return crop_data;
	}

	public void setCrop_data(CropData crop_data) {
		this.crop_data = crop_data;
	}

	public MatchData getMatch_data() {
		return match_data;
	}

	public void setMatch_data(MatchData match_data) {
		this.match_data = match_data;
	}

	public FaceFeaturesData getFace_features_data() {
		return face_features_data;
	}

	public void setFace_features_data(FaceFeaturesData face_features_data) {
		this.face_features_data = face_features_data;
	}

	public Updates getUpdates() {
		return updates;
	}

	public void setUpdates(Updates updates) {
		this.updates = updates;
	}

	public int getTrigger() {
		return trigger;
	}

	public void setTrigger(int trigger) {
		this.trigger = trigger;
	}

}
