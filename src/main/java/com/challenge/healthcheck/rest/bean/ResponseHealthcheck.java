package com.challenge.healthcheck.rest.bean;

public class ResponseHealthcheck {
	 
    private int total_websits;
    private int success;
    private int failure;
    private long total_time;
    
	public int getTotal_websits() {
		return total_websits;
	}
	public void setTotal_websits(int total_websits) {
		this.total_websits = total_websits;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public int getFailure() {
		return failure;
	}
	public void setFailure(int failure) {
		this.failure = failure;
	}
	public long getTotal_time() {
		return total_time;
	}
	public void setTotal_time(long total_time) {
		this.total_time = total_time;
	}
    
    
 
}