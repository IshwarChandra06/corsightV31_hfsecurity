package com.eikona.mata.corsight.request.dto;

public class CropData {

	private long frame_id;

	private double frame_timestamp;

	private double detector_score;

	private double face_score;

	private double pitch;

	private double yaw;

	private String face_crop_img;

	private double masked_score;

	public long getFrame_id() {
		return frame_id;
	}

	public void setFrame_id(long frame_id) {
		this.frame_id = frame_id;
	}

	public double getFrame_timestamp() {
		return frame_timestamp;
	}

	public void setFrame_timestamp(double frame_timestamp) {
		this.frame_timestamp = frame_timestamp;
	}

	public double getDetector_score() {
		return detector_score;
	}

	public void setDetector_score(double detector_score) {
		this.detector_score = detector_score;
	}

	public double getFace_score() {
		return face_score;
	}

	public void setFace_score(double face_score) {
		this.face_score = face_score;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public String getFace_crop_img() {
		return face_crop_img;
	}

	public void setFace_crop_img(String face_crop_img) {
		this.face_crop_img = face_crop_img;
	}

	public double getMasked_score() {
		return masked_score;
	}

	public void setMasked_score(double masked_score) {
		this.masked_score = masked_score;
	}
	
	
}
