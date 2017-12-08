package com.codeool;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.Map;

public class TemplatesProcessor {

    private TemplateEngine engine;
    private ClassLoaderTemplateResolver resolver;
    private Context context;


    public TemplatesProcessor(){
        // Creates new templateEngine
        this.engine = new TemplateEngine();
        // Creates resolver
        this.context = new Context();
        this.resolver = new ClassLoaderTemplateResolver();
        //Sets up engine by resolver
        resolver.setPrefix("/web/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");

        engine.setTemplateResolver(resolver);
    }

    public void setVariables(Map<String, Object> variables){
        context.setVariables(variables);
    }

    public String ProcessTemplateToPage(String templateName){
        StringWriter writer = new StringWriter();

        engine.process(templateName, this.context, writer);
        return writer.toString();
    }
}
