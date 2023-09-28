package com.docuitservice.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response extends SimpleResponse {
    private Object response;

	public Response() {
	}

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
	public void setStatus(Boolean true1) {
}

	public Response(Object response) {
		this.response = response;
	}
}
