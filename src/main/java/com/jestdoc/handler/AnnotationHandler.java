package com.jestdoc.handler;

import com.sun.javadoc.AnnotationDesc;

public interface AnnotationHandler<T> {

    boolean support(AnnotationDesc annDesc);

    T handle(AnnotationDesc annDesc);

}
