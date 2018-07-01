package com.jestdoc.context;

import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;

public class RestParamDoc implements TypeDoc {

    Parameter parameter;
    ParamTag paramTag;
    RestFieldDoc annDoc;

    public RestParamDoc(Parameter parameter, ParamTag paramTag, RestFieldDoc annDoc) {
        this.parameter = parameter;
        this.paramTag = paramTag;
        this.annDoc = annDoc;
    }

    public String getName() {
        return annDoc != null && annDoc.parameterName != null ? annDoc.parameterName : parameter.name();
    }

    public String getDescription() {
        return paramTag != null && paramTag.parameterComment() != null ? paramTag.parameterComment() : getName();
    }

    public Object getDefaultValue() {
        return annDoc != null ? annDoc.defaultValue : null;
    }

    public boolean isRequired() {
        return annDoc != null ? annDoc.required : true;
    }

    public String getRestType() {
        return annDoc != null && annDoc.type != null ? annDoc.type : "query";
    }

    public String getParamType() {
        return parameter.typeName();
    }

    @Override
    public String qualifiedTypeName() {
        return parameter.type().qualifiedTypeName();
    }

    @Override
    public String simpleTypeName() {
        return parameter.type().simpleTypeName();
    }

}
