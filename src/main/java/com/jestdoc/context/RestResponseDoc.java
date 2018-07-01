package com.jestdoc.context;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.Type;

/**
 * @author wuxii@foxmail.com
 */
public class RestResponseDoc implements TypeDoc {

    public AnnotationDesc annDesc;
    public Type returnType;
    public String restType;

    @Override
    public String qualifiedTypeName() {
        return returnType.qualifiedTypeName();
    }

    @Override
    public String simpleTypeName() {
        return returnType.simpleTypeName();
    }

    public String getType() {
        return simpleTypeName();
    }

}
