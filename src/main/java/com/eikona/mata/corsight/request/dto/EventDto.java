package com.eikona.mata.corsight.request.dto;

public class EventDto {
	
	private String event;
	
    private String id;
    
    private Data data;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
    
    
}
