package com.jestdoc.context;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

public class OperationContextTest {

    public static boolean start(RootDoc root) {
        for (ClassDoc classDoc : root.classes()) {
            MethodDoc[] methods = classDoc.methods();
            for (MethodDoc method : methods) {
                OperationContext oc = new OperationContext(method, classDoc);
                List<RestParamDoc> docs = oc.getRestParamDocs();
                for (RestParamDoc doc : docs) {
                    System.out.println(JSON.toJSONString(doc));
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws URISyntaxException {
        File file = new File("src/test/java/com/jestdoc/controller/FooController.java");
        String[] docArgs = new String[] { "-private", "-doclet", OperationContextTest.class.getName(), file.getAbsolutePath() };
        com.sun.tools.javadoc.Main.execute(docArgs);
    }

}
