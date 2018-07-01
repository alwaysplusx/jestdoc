package com.jestdoc.operation;

import org.springframework.http.HttpHeaders;

public class AbstractOperationMessage<T extends AbstractOperationMessage<T>> {

    private String content;
    private HttpHeaders headers;

    public byte[] getContent() {
        return content.getBytes();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getContentAsString() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

}
