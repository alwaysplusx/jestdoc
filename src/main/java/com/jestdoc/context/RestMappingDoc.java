package com.jestdoc.context;

import java.util.List;

import org.springframework.http.HttpMethod;

public class RestMappingDoc {

    public List<HttpMethod> method;

    public List<String> path;

    public List<String> consumes;

    public List<String> produces;

    public List<String> headers;

}
