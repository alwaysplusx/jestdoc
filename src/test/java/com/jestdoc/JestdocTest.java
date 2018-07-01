package com.jestdoc;

import com.jestdoc.operation.JestdocOperationRequest;
import com.jestdoc.operation.JestdocOperationResponse;
import com.sun.javadoc.*;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.restdocs.operation.StandardOperation;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Attributes;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class JestdocTest {

    private static final Logger log = LoggerFactory.getLogger(JestdocTest.class);

    public static boolean start(RootDoc root) {
        ClassDoc[] classes = root.classes();
        for (ClassDoc classDoc : classes) {
            // System.out.println(classDoc);
            // AnnotationDesc[] annotations = classDoc.annotations();
            // for (AnnotationDesc ann : annotations) {
            // System.out.println(ann);
            // }
            // FieldDoc[] fields = classDoc.fields();
            // for (FieldDoc field : fields) {
            // System.out.println(field);
            // }
            MethodDoc[] methods = classDoc.methods();
            for (MethodDoc m : methods) {
                showMethodDoc(m);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        File file = new File("src/test/java/com/jestdoc/controller/FooController.java");
        String[] docArgs = new String[]{"-private", "-doclet", JestdocTest.class.getName(), file.getAbsolutePath()};
        com.sun.tools.javadoc.Main.execute(docArgs);
    }

    public static void restdocTest() throws IOException {
        ParameterDescriptor parameterDescriptor = RequestDocumentation
                .parameterWithName("name")
                .description("名称")
                .optional()
                .attributes(new Attributes.Attribute("foo", "bar"));

        RequestParametersSnippet snippet = RequestDocumentation.requestParameters(parameterDescriptor);

        JestdocOperationRequest request = new JestdocOperationRequest();
        JestdocOperationResponse response = new JestdocOperationResponse();

        StandardOperation operation = new StandardOperation("test", request, response, Collections.emptyMap());
        snippet.document(operation);
    }

    public static void showMethodDoc(MethodDoc method) {
        System.out.println();
        log.info("method comment text: {}", method.commentText());
        ParamTag[] paramTags = method.paramTags();
        Parameter[] parameters = method.parameters();
        for (int i = 0, max = paramTags.length; i < max; i++) {
            ParamTag tag = paramTags[i];
            Parameter param = parameters[i];
            log.info("param({}) tag: name={}, comment={}, type={}", i, tag.parameterName(), tag.parameterComment(), param.typeName());
            AnnotationDesc[] anns = param.annotations();
            for (AnnotationDesc ann : anns) {
                log.info("ann: type={}", ann.annotationType().typeName());
                ElementValuePair[] pairs = ann.elementValues();
                for (ElementValuePair pair : pairs) {
                    log.info("\tpair: element={}, value={}", pair.element(), pair.value());
                }
            }
        }
        Tag[] sampleTags = method.tags("sample");
        for (Tag tag : sampleTags) {
            log.info("sample tag: {} {} {}", tag.kind(), tag.name(), tag.text());
        }
        AnnotationDesc[] annotations = method.annotations();
        for (AnnotationDesc ann : annotations) {
            log.info("method ann: {}", ann.elementValues()[0].value().value());
        }
    }

}
