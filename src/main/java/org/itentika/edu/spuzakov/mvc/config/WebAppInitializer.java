package org.itentika.edu.spuzakov.mvc.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext servletContext) {
        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(JavaConfig.class);
        servletContext.addListener(new ContextLoaderListener(context));


        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(context);


        ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/*");

        servletContext.addFilter("customRequestLoggingFilter",
                        CustomRequestLoggingFilter.class)
                .addMappingForServletNames(null, false, "app");
    }
}
