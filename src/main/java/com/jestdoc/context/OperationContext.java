package com.jestdoc.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;

import com.jestdoc.handler.AnnotationHandler;
import com.jestdoc.handler.RestFieldAnnotationHandler;
import com.jestdoc.handler.RestMappingAnnotationHandler;
import com.jestdoc.handler.RestResponseAnnotationHandler;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;

public class OperationContext {

    ClassDoc owner;
    MethodDoc method;

    private List<AnnotationHandler<RestFieldDoc>> parameterHandler = Arrays.asList(new RestFieldAnnotationHandler());

    private List<AnnotationHandler<RestMappingDoc>> mappingHandler = Arrays.asList(new RestMappingAnnotationHandler());

    private AnnotationHandler<RestResponseDoc> responseHandler = new RestResponseAnnotationHandler();

    public OperationContext(MethodDoc method, ClassDoc owner) {
        this.method = method;
        this.owner = owner;
    }

    public RestMappingDoc getRestMappingDoc() {
        AnnotationDesc[] annotations = method.annotations();
        for (AnnotationDesc ann : annotations) {
            for (AnnotationHandler handler : mappingHandler) {
                if (handler.support(ann)) {
                    return (RestMappingDoc) handler.handle(ann);
                }
            }
        }
        return null;
    }

    public List<RestParamDoc> getRestParamDocs() {
        List<RestParamDoc> docs = new ArrayList<>();
        Parameter[] parameters = method.parameters();
        for (Parameter p : parameters) {
            String name = p.name();
            ParamTag paramTag = getParamTag(name);
            RestFieldDoc restDoc = getRestAnnotationDoc(p);
            docs.add(new RestParamDoc(p, paramTag, restDoc));
        }
        return docs;
    }

    public RestParamDoc getRestParamDoc(String name) {
        Parameter parameter = getParameter(name);
        if (parameter == null) {
            return null;
        }
        ParamTag paramTag = getParamTag(name);
        RestFieldDoc restDoc = getRestAnnotationDoc(parameter);
        return new RestParamDoc(parameter, paramTag, restDoc);
    }

    public HttpMethod getHttpMethod() {
        RestMappingDoc doc = getRestMappingDoc();
        return doc.method != null && !doc.method.isEmpty() ? doc.method.get(0) : HttpMethod.GET;
    }

    public RestResponseDoc getRestResponseDoc() {
        RestResponseDoc responseDoc = null;
        AnnotationDesc[] anns = method.annotations();
        for (AnnotationDesc ann : anns) {
            if (responseHandler.support(ann)) {
                responseDoc = responseHandler.handle(ann);
                break;
            }
        }
        if (responseDoc == null) {
            for (AnnotationDesc ann : owner.annotations()) {
                if (responseHandler.support(ann)) {
                    responseDoc = responseHandler.handle(ann);
                    break;
                }
            }
        }
        if (responseDoc == null) {
            responseDoc = new RestResponseDoc();
        }
        responseDoc.returnType = method.returnType();
        return responseDoc;
    }

    public String getPath() {
        RestMappingDoc mapping = getRestMappingDoc();
        return mapping.path != null && mapping.path.isEmpty() ? mapping.path.get(0) : "/";
    }

    public List<RestParamDoc> getPathRestParamDocs() {
        List<RestParamDoc> pathDocs = new ArrayList<>();
        List<RestParamDoc> docs = getRestParamDocs();
        for (RestParamDoc doc : docs) {
            if ("path".equals(doc.getRestType())) {
                pathDocs.add(doc);
            }
        }
        return pathDocs;
    }

    // help methods

    protected RestFieldDoc getRestAnnotationDoc(Parameter p) {
        AnnotationDesc[] annotations = p.annotations();
        for (AnnotationDesc ann : annotations) {
            for (AnnotationHandler handler : parameterHandler) {
                if (handler.support(ann)) {
                    return (RestFieldDoc) handler.handle(ann);
                }
            }
        }
        return new RestFieldDoc(p.name());
    }

    protected Parameter getParameter(String name) {
        Parameter[] parameters = method.parameters();
        for (Parameter p : parameters) {
            if (p.name().equals(name)) {
                return p;
            }
        }
        return null;
    }

    protected ParamTag getParamTag(String name) {
        ParamTag[] tags = method.paramTags();
        for (ParamTag tag : tags) {
            if (tag.parameterName().equals(name))
                return tag;
        }
        return null;
    }

}
