package com.jestdoc.handler;

import com.jestdoc.context.RestFieldDoc;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestFieldAnnotationHandler implements AnnotationHandler<RestFieldDoc> {

    private static final Map<String, String> parameterNames;

    static {
        Map<String, String> names = new HashMap();
        names.put(RequestParam.class.getSimpleName(), "query");
        names.put(PathVariable.class.getSimpleName(), "path");
        names.put(RequestBody.class.getSimpleName(), "body");
        parameterNames = Collections.unmodifiableMap(names);
    }

    @Override
    public boolean support(AnnotationDesc annDesc) {
        String typeName = annDesc.annotationType().typeName();
        return parameterNames.containsKey(typeName);
    }

    @Override
    public RestFieldDoc handle(AnnotationDesc annDesc) {

        RestFieldDoc restAnnDoc = new RestFieldDoc();
        AnnotationTypeDoc annType = annDesc.annotationType();

        restAnnDoc.annDesc = annDesc;
        restAnnDoc.type = parameterNames.get(annType.typeName());

        AnnotationDesc.ElementValuePair[] pairs = annDesc.elementValues();
        for (AnnotationDesc.ElementValuePair pair : pairs) {
            String name = pair.element().name();
            Object value = pair.value().value();
            if ("value".equals(name) || "name".equals(name)) {
                restAnnDoc.parameterName = value.toString();
                continue;
            }
            if ("defaultValue".equals(name)) {
                restAnnDoc.defaultValue = value.toString();
                continue;
            }
            if ("required".equals(name)) {
                restAnnDoc.required = Boolean.valueOf(value.toString());
                continue;
            }
        }
        return restAnnDoc;
    }

}
