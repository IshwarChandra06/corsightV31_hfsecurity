package com.eikona.mata.corsight.request.dto;

import java.util.List;

public class MatchData {
	
	    private String poi_display_img;
	    
	    private String poi_display_name;
	    
	    private List<Watchlist> watchlists;
	    
	    private Object poi_notes;
	    
	    private double utc_time_matched;
	    
	    private String poi_id;
	    
	    private double poi_confidence;

		public String getPoi_display_img() {
			return poi_display_img;
		}

		public void setPoi_display_img(String poi_display_img) {
			this.poi_display_img = poi_display_img;
		}

		public String getPoi_display_name() {
			return poi_display_name;
		}

		public void setPoi_display_name(String poi_display_name) {
			this.poi_display_name = poi_display_name;
		}

		public List<Watchlist> getWatchlists() {
			return watchlists;
		}

		public void setWatchlists(List<Watchlist> watchlists) {
			this.watchlists = watchlists;
		}

		public Object getPoi_notes() {
			return poi_notes;
		}

		public void setPoi_notes(Object poi_notes) {
			this.poi_notes = poi_notes;
		}

		public double getUtc_time_matched() {
			return utc_time_matched;
		}

		public void setUtc_time_matched(double utc_time_matched) {
			this.utc_time_matched = utc_time_matched;
		}

		public String getPoi_id() {
			return poi_id;
		}

		public void setPoi_id(String poi_id) {
			this.poi_id = poi_id;
		}

		public double getPoi_confidence() {
			return poi_confidence;
		}

		public void setPoi_confidence(double poi_confidence) {
			this.poi_confidence = poi_confidence;
		}
	    
	    
}
