package com.jestdoc.context;

import com.sun.javadoc.AnnotationDesc;

public class RestFieldDoc implements TypeDoc {

    public AnnotationDesc annDesc;
    public String parameterName;
    public String defaultValue;
    public boolean required = true;
    public String type;

    public RestFieldDoc() {
    }

    public RestFieldDoc(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public String qualifiedTypeName() {
        return annDesc.annotationType().qualifiedTypeName();
    }

    @Override
    public String simpleTypeName() {
        return annDesc.annotationType().simpleTypeName();
    }
}
