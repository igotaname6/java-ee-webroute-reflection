package com.codeool;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class webHandler implements HttpHandler {

    private AnnotatedHandlers handler;

    public webHandler(){
        try {
            this.handler = createHandlersByReflection();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private AnnotatedHandlers createHandlersByReflection() throws ReflectiveOperationException{
        String className = "com.codeool.AnnotatedHandlers";
        Class<?> handlersClass = null;

        handlersClass = Class.forName(className);
        Constructor constructor = handlersClass.getConstructor();
        AnnotatedHandlers object = (AnnotatedHandlers) constructor.newInstance();
        return object;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String uriPath = httpExchange.getRequestURI().getPath();

        resolveRequest(method, uriPath, httpExchange);
    }

    private void resolveRequest(String requestMethod, String  uriPath, HttpExchange httpExchange){
        String className = "com.codeool.AnnotatedHandlers";

        try {
            Class<?> handlersClass = Class.forName(className);

            Method[] methods = handlersClass.getMethods();
            Method properMethod = chooseMethod(methods, requestMethod, uriPath);
            properMethod.invoke(handler, httpExchange);

        } catch (ClassNotFoundException e) {
            System.out.println("Didin't find class");
            e.printStackTrace();
        } catch (ReflectiveOperationException e) {
            System.out.println("No such method");
            e.printStackTrace();
        }
    }

    private Method chooseMethod(Method[] methods, String requestedMethod, String uri) {
        Method chosenHandler = null;
        Method method404 = null;

        for (Method handleMethod : methods) {
            WebRoute annotation = handleMethod.getAnnotation(WebRoute.class);
            String path = annotation.path();
            String supportedMethod = annotation.requestMethod();

            if (requestedMethod.equals(supportedMethod) && uri.equals(path)) {
                chosenHandler = handleMethod;
                return chosenHandler;
            }
            if (requestedMethod.equals(supportedMethod) && path.equals("/404")) {
                method404 = handleMethod;
            }
        }
        if(chosenHandler == null){
            chosenHandler = method404;
        }
        return chosenHandler;
    }
}
