package com.eikona.mata.corsight.request.dto;

public class CameraData {
	    private String camera_id;
	    private String stream_id;
	    private String camera_description;
	    private String node_id;
	    private String analysis_mode;
	    private Object camera_notes;
		public String getCamera_id() {
			return camera_id;
		}
		public void setCamera_id(String camera_id) {
			this.camera_id = camera_id;
		}
		public String getStream_id() {
			return stream_id;
		}
		public void setStream_id(String stream_id) {
			this.stream_id = stream_id;
		}
		public String getCamera_description() {
			return camera_description;
		}
		public void setCamera_description(String camera_description) {
			this.camera_description = camera_description;
		}
		public String getNode_id() {
			return node_id;
		}
		public void setNode_id(String node_id) {
			this.node_id = node_id;
		}
		public String getAnalysis_mode() {
			return analysis_mode;
		}
		public void setAnalysis_mode(String analysis_mode) {
			this.analysis_mode = analysis_mode;
		}
		public Object getCamera_notes() {
			return camera_notes;
		}
		public void setCamera_notes(Object camera_notes) {
			this.camera_notes = camera_notes;
		}
	    
	    
}
