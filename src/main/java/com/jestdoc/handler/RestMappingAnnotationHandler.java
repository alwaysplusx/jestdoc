package com.jestdoc.handler;

import com.jestdoc.context.RestMappingDoc;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author wuxii@foxmail.com
 */
public class RestMappingAnnotationHandler implements AnnotationHandler<RestMappingDoc> {

    private static final Set<String> mappingNames;

    static {
        Set<String> names = new HashSet<>();
        names.add(RequestMapping.class.getSimpleName());
        names.add(GetMapping.class.getSimpleName());
        names.add(PostMapping.class.getSimpleName());
        names.add(PutMapping.class.getSimpleName());
        names.add(DeleteMapping.class.getSimpleName());
        names.add(PatchMapping.class.getSimpleName());
        mappingNames = Collections.unmodifiableSet(names);
    }

    @Override
    public boolean support(AnnotationDesc annDesc) {
        AnnotationTypeDoc annType = annDesc.annotationType();
        return mappingNames.contains(annType.typeName());
    }

    @Override
    public RestMappingDoc handle(AnnotationDesc annDesc) {
        RestMappingDoc doc = new RestMappingDoc();
        AnnotationDesc.ElementValuePair[] pairs = annDesc.elementValues();
        for (AnnotationDesc.ElementValuePair pair : pairs) {
            String name = pair.element().name();
            if ("path".equals(name) || "value".equals(name)) {
                doc.path = asStringList((Object[]) pair.value().value());
                continue;
            }
            if ("method".equals(name)) {
                doc.method = asHttpMethodList((Object[]) pair.value().value());
                continue;
            }
            if ("consumes".equals(name)) {
                doc.consumes = asStringList((Object[]) pair.value().value());
                continue;
            }
            if ("produces".equals(name)) {
                doc.produces = asStringList((Object[]) pair.value().value());
                continue;
            }
        }
        return doc;
    }

    protected List<String> asStringList(Object[] value) {
        List<String> result = new ArrayList<>();
        for (Object v : value) {
            result.add(v.toString());
        }
        return result;
    }

    protected List<HttpMethod> asHttpMethodList(Object[] value) {
        List<HttpMethod> result = new ArrayList<>();
        for (Object o : value) {
            result.add(HttpMethod.valueOf(o.toString()));
        }
        return result;
    }

}
