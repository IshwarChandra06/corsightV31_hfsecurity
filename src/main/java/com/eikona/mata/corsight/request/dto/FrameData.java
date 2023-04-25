package com.eikona.mata.corsight.request.dto;

public class FrameData {
	 	private double utc_time_recorded;
	 	
	    private int utc_time_zone;
	    
	    private int frame_id;
	    
	    private BoundingBox bounding_box;

		public double getUtc_time_recorded() {
			return utc_time_recorded;
		}

		public void setUtc_time_recorded(double utc_time_recorded) {
			this.utc_time_recorded = utc_time_recorded;
		}

		public int getUtc_time_zone() {
			return utc_time_zone;
		}

		public void setUtc_time_zone(int utc_time_zone) {
			this.utc_time_zone = utc_time_zone;
		}

		public int getFrame_id() {
			return frame_id;
		}

		public void setFrame_id(int frame_id) {
			this.frame_id = frame_id;
		}

		public BoundingBox getBounding_box() {
			return bounding_box;
		}

		public void setBounding_box(BoundingBox bounding_box) {
			this.bounding_box = bounding_box;
		}
	    
	    
	    
}
