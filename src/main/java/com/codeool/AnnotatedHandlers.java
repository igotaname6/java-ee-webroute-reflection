package com.codeool;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


public class AnnotatedHandlers {

    private TemplatesProcessor templatesProcessor;
    private String name;

    public AnnotatedHandlers(){
        this.templatesProcessor = new TemplatesProcessor();
        this.name = "You";
    }

    @WebRoute(requestMethod = "GET")
    public void handleHome(HttpExchange httpExchange) throws IOException {

        String templateName = "home";
        String  responseBody = templatesProcessor.ProcessTemplateToPage(templateName);
        httpExchange.sendResponseHeaders(200, responseBody.getBytes().length);

        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }

    @WebRoute(requestMethod = "POST")
    public void handleFormFromHome(HttpExchange httpExchange) throws IOException {

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> parsedForm = parseFormData(formData);
        name = parsedForm.get("name");

        httpExchange.getResponseHeaders().add("Location", "/hello");
        httpExchange.sendResponseHeaders(302, -1);
    }

    @WebRoute(requestMethod = "GET", path = "/hello")
    public void handleHello(HttpExchange httpExchange)  throws IOException {
        String templateName = "hello";

        Map<String, Object> varMap = new HashMap<>();
        varMap.put("name", name);
        templatesProcessor.setVariables(varMap);
        String responseBody = templatesProcessor.ProcessTemplateToPage(templateName);

        httpExchange.sendResponseHeaders(200, responseBody.getBytes().length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }

    private Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> parsedForm = new HashMap<>();
        String[] pairs = formData.split("&");

        for(String pair: pairs){
            String decodedPair = new URLDecoder().decode(pair, "UTF-8");
            String [] singleSplitedPair = decodedPair.split("=");

            parsedForm.put(singleSplitedPair[0], singleSplitedPair[1]);
        }
        return parsedForm;
    }
}
