package com.challenge.healthcheck.rest.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;

public class ResponseApi<T> {

    private T data;
    private Response.Status status;

    public ResponseApi() {
    }

    public ResponseApi(Response.Status status, T data) {
        this.status = status;
        this.data = data;
    }

    @JsonProperty
    public Response.Status getCode() {
        return status;
    }

    @JsonProperty
    public T getData() {
        return data;
    }
}
