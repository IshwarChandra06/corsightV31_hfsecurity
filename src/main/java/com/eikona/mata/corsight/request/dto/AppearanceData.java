package com.eikona.mata.corsight.request.dto;

public class AppearanceData {
	
		private String appearance_id;
	 
	    private double utc_time_started;
	    
	    private int first_frame_id;
	    
	    private boolean fs_store;
	    
	    private boolean fs_update;

		public String getAppearance_id() {
			return appearance_id;
		}

		public void setAppearance_id(String appearance_id) {
			this.appearance_id = appearance_id;
		}

		public double getUtc_time_started() {
			return utc_time_started;
		}

		public void setUtc_time_started(double utc_time_started) {
			this.utc_time_started = utc_time_started;
		}

		public int getFirst_frame_id() {
			return first_frame_id;
		}

		public void setFirst_frame_id(int first_frame_id) {
			this.first_frame_id = first_frame_id;
		}

		public boolean isFs_store() {
			return fs_store;
		}

		public void setFs_store(boolean fs_store) {
			this.fs_store = fs_store;
		}

		public boolean isFs_update() {
			return fs_update;
		}

		public void setFs_update(boolean fs_update) {
			this.fs_update = fs_update;
		}

		
	    
	    
	    
}
