package com.jestdoc.operation;

import com.alibaba.fastjson.JSON;
import com.jestdoc.context.*;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.operation.*;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class JestdocOperationBuilder {

    public static RootDoc root;
    public static String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final Logger log = LoggerFactory.getLogger(JestdocOperationBuilder.class);

    public static boolean start(RootDoc root) throws IOException {
        JestdocOperationBuilder.root = root;
        for (ClassDoc classDoc : root.classes()) {
            MethodDoc[] methods = classDoc.methods();
            for (MethodDoc method : methods) {
                JestOperation operation = new JestOperation(method, classDoc);
                log.info("jest mock request: {}", operation.getRequest());
                log.info("jest mock response: {}", operation.getResponse());
                buildSnippet(operation);
            }
        }
        return true;
    }

    private static void buildSnippet(JestOperation operation) throws IOException {
        List<RestParamDoc> docs = operation.operationContext.getRestParamDocs();
        List<ParameterDescriptor> requestParams = new ArrayList<>();
        List<ParameterDescriptor> pathParams = new ArrayList<>();
        for (RestParamDoc doc : docs) {
            ParameterDescriptor param = RequestDocumentation.parameterWithName(doc.getName()).description(doc.getDescription());
            if (!doc.isRequired()) {
                param.optional();
            }
            param.attributes(new Attributes.Attribute("sample", buildSampleValue(doc)));
            if ("query".equals(doc.getRestType())) {
                requestParams.add(param);
            } else if ("path".equals(doc.getRestType())) {
                pathParams.add(param);
            }
        }
        if (!requestParams.isEmpty()) {
            RequestParametersSnippet requestParametersSnippet = RequestDocumentation
                    .requestParameters(requestParams.toArray(new ParameterDescriptor[requestParams.size()]));
            requestParametersSnippet.document(operation);
        }
        if (!pathParams.isEmpty()) {
            PathParametersSnippet pathParametersSnippet = RequestDocumentation.pathParameters(pathParams.toArray(new ParameterDescriptor[pathParams.size()]));
            pathParametersSnippet.document(operation);
        }
    }

    public static void main(String[] args) {
        File file = new File("src/test/java/com/jestdoc/controller/FooController.java");
        String[] docArgs = new String[] { "-private", "-doclet", JestdocOperationBuilder.class.getName(), file.getAbsolutePath() };
        com.sun.tools.javadoc.Main.execute(docArgs);
    }

    public static RequestContext buildRequestContext(List<RestParamDoc> docs) {
        RequestContext context = new RequestContext();
        for (RestParamDoc doc : docs) {
            String restType = doc.getRestType();
            if ("query".equals(restType)) {
                context.parameters.add(doc.getName(), buildSampleValue(doc));
            } else if ("body".equals(restType)) {
                context.bodyContent = buildSampleValue(doc);
            }
        }
        return context;
    }

    public static HttpHeaders buildHttpHeaders(RestMappingDoc mappingDoc) {
        return buildHeaders(mappingDoc.headers);
    }

    public static List<RequestCookie> buildRequestCookies(RestMappingDoc mappingDoc) {
        return Collections.emptyList();
    }

    public static List<OperationRequestPart> buildRequestPart(RestMappingDoc mappingDoc) {
        return Collections.emptyList();
    }

    public static String buildSampleValue(TypeDoc type) {
        String simpleTypeName = type.simpleTypeName();
        String value = buildSimpleTypeValue(simpleTypeName);
        if (value != null) {
            return value;
        }
        try {
            Class<?> clazz = ClassUtils.forName(type.qualifiedTypeName(), null);
            return JSON.toJSONString(BeanUtils.instantiateClass(clazz));
        } catch (ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    public static URI buildRequestURI(OperationContext operationContext) {
        try {
            return new URI(buildRequestPath(operationContext));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String buildRequestPath(OperationContext operationContext) {
        String path = operationContext.getPath();
        if (path.indexOf("{") != -1 && path.indexOf("}") != -1) {
            Map<String, String> pathVars = new HashMap<>();
            List<RestParamDoc> docs = operationContext.getPathRestParamDocs();
            for (RestParamDoc doc : docs) {
                pathVars.put(doc.getName(), buildSampleValue(doc));
            }
            for (Entry<String, String> var : pathVars.entrySet()) {
                path = path.replace("{" + var.getKey() + "}", var.getValue());
            }
        }
        return path;
    }

    protected static String buildSimpleTypeValue(String type) {
        if (type == null) {
            return null;
        }
        switch (type.toLowerCase()) {
        case "char":
            return "c";
        case "string":
            return "string";
        case "integer":
        case "int":
        case "short":
            return "1";
        case "double":
        case "number":
        case "float":
            return "1.0";
        case "date":
        case "timestamp":
            return new SimpleDateFormat(DATA_FORMAT).format(new Date());
        }
        return null;
    }

    protected static HttpHeaders buildHeaders(List<String> headers) {
        HttpHeaders result = new HttpHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (String header : headers) {
                String[] h = splitFirst(header, "=");
                if (h.length == 2) {
                    result.add(h[0], h[1]);
                }
            }
        }
        return result;
    }

    protected static String[] splitFirst(String s, String c) {
        int index = s.indexOf(c);
        return index == -1 ? new String[] { s } : new String[] { s.substring(0, index), s.substring(index + c.length()) };
    }

    static class RequestContext {

        private Parameters parameters = new Parameters();
        private String bodyContent;

    }

    private static class JestOperation implements Operation {

        public String name;
        private OperationContext operationContext;
        private Map<String, Object> attributes = new HashMap<>();

        public JestOperation(MethodDoc methodDoc, ClassDoc classDoc) {
            this.operationContext = new OperationContext(methodDoc, classDoc);
            this.name = buildRequestPath(operationContext);
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public OperationRequest getRequest() {
            JestdocOperationRequest request = new JestdocOperationRequest();
            List<RestParamDoc> parameters = operationContext.getRestParamDocs();
            RestMappingDoc mapping = operationContext.getRestMappingDoc();
            RequestContext requestContext = JestdocOperationBuilder.buildRequestContext(parameters);
            request.setParameters(requestContext.parameters);
            request.setContent(requestContext.bodyContent);
            request.setMethod(operationContext.getHttpMethod());
            request.setCookies(buildRequestCookies(mapping));
            request.setParts(buildRequestPart(mapping));
            request.setCookies(buildRequestCookies(mapping));
            request.setUri(buildRequestURI(operationContext));
            return request;
        }

        @Override
        public OperationResponse getResponse() {
            JestdocOperationResponse response = new JestdocOperationResponse();
            RestResponseDoc responseDoc = operationContext.getRestResponseDoc();
            response.setContent(buildSampleValue(responseDoc));
            if ("body".equals(responseDoc.restType)) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                response.setHeaders(headers);
            }
            return response;
        }

    }

}
