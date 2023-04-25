package com.eikona.mata.dto;


import java.util.List;

public class PaginationDto<T> {

	private List<T> data;
	private int total_pages;
	private int page_number;
	private int page_size;
	private long total_record;
	private long filter_record;
	private String sortDir;
	private String msg;
	private String msg_type;
	
	public PaginationDto() {
		super();
	}
	public PaginationDto(List<T> data, int total_pages, int page_number, int page_size,long filter_record,long total_record,String sortDir,String msg,String msg_type) {
		super();
		this.data = data;
		this.total_pages = total_pages;
		this.page_number = page_number;
		this.page_size = page_size;
		this.filter_record = filter_record;
		this.total_record = total_record;
		this.sortDir = sortDir;
		this.msg = msg;
		this.msg_type = msg_type;
	}
	
	public List<T> getData() {
		return data;
	}
	public int getTotal_pages() {
		return total_pages;
	}
	public int getPage_number() {
		return page_number;
	}
	public int getPage_size() {
		return page_size;
	}
	public long getTotal_record() {
		return total_record;
	}
	public long getFilter_record() {
		return filter_record;
	}
	public String getMsg() {
		return msg;
	}
	public String getMsg_type() {
		return msg_type;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}
	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}
	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}
	public void setTotal_record(long total_record) {
		this.total_record = total_record;
	}
	public void setFilter_record(long filter_record) {
		this.filter_record = filter_record;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setMsg_type(String msg_type) {
		this.msg_type = msg_type;
	}
	public String getSortDir() {
		return sortDir;
	}
	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}
}
