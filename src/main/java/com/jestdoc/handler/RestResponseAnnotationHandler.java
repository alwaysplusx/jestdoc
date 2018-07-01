package com.jestdoc.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ResponseBody;

import com.jestdoc.context.RestResponseDoc;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;

/**
 * @author wuxii@foxmail.com
 */
public class RestResponseAnnotationHandler implements AnnotationHandler<RestResponseDoc> {

    private static final Map<String, String> responseNames;

    static {
        Map<String, String> names = new HashMap<>();
        names.put(ResponseBody.class.getSimpleName(), "body");
        responseNames = Collections.unmodifiableMap(names);
    }

    @Override
    public boolean support(AnnotationDesc annDesc) {
        AnnotationTypeDoc annType = annDesc.annotationType();
        return responseNames.containsKey(annType.typeName());
    }

    @Override
    public RestResponseDoc handle(AnnotationDesc annDesc) {
        AnnotationTypeDoc ann = annDesc.annotationType();
        RestResponseDoc doc = new RestResponseDoc();
        doc.annDesc = annDesc;
        doc.restType = responseNames.get(ann.typeName());
        return doc;
    }

}
