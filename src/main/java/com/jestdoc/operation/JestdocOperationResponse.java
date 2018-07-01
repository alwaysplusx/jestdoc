package com.jestdoc.operation;

import org.springframework.http.HttpStatus;
import org.springframework.restdocs.operation.OperationResponse;

public class JestdocOperationResponse extends AbstractOperationMessage<JestdocOperationResponse> implements OperationResponse {

    private HttpStatus httpStatus;

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    public JestdocOperationResponse status(HttpStatus httpStatus) {
        return this;
    }

}
