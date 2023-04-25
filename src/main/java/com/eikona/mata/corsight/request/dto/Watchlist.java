package com.eikona.mata.corsight.request.dto;

public class Watchlist {
	 private String watchlist_type;
	    private String display_name;
	    private String display_color;
	    private Object watchlist_notes;
	    private String watchlist_id;
	    private String match_outcome;
		public String getWatchlist_type() {
			return watchlist_type;
		}
		public void setWatchlist_type(String watchlist_type) {
			this.watchlist_type = watchlist_type;
		}
		public String getDisplay_name() {
			return display_name;
		}
		public void setDisplay_name(String display_name) {
			this.display_name = display_name;
		}
		public String getDisplay_color() {
			return display_color;
		}
		public void setDisplay_color(String display_color) {
			this.display_color = display_color;
		}
		public Object getWatchlist_notes() {
			return watchlist_notes;
		}
		public void setWatchlist_notes(Object watchlist_notes) {
			this.watchlist_notes = watchlist_notes;
		}
		public String getWatchlist_id() {
			return watchlist_id;
		}
		public void setWatchlist_id(String watchlist_id) {
			this.watchlist_id = watchlist_id;
		}
		public String getMatch_outcome() {
			return match_outcome;
		}
		public void setMatch_outcome(String match_outcome) {
			this.match_outcome = match_outcome;
		}
	    
}
