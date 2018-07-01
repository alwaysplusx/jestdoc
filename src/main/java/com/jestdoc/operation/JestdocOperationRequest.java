package com.jestdoc.operation;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestPart;
import org.springframework.restdocs.operation.Parameters;
import org.springframework.restdocs.operation.RequestCookie;

/**
 * @author wuxii
 */
public class JestdocOperationRequest extends AbstractOperationMessage<JestdocOperationRequest> implements OperationRequest {

    private HttpMethod method;

    private Parameters parameters;

    private List<OperationRequestPart> parts;

    private URI uri;

    private List<RequestCookie> cookies;

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public Parameters getParameters() {
        return this.parameters;
    }

    @Override
    public Collection<OperationRequestPart> getParts() {
        return this.parts;
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public Collection<RequestCookie> getCookies() {
        return this.cookies;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setParts(List<OperationRequestPart> parts) {
        this.parts = parts;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setCookies(List<RequestCookie> cookies) {
        this.cookies = cookies;
    }

}
