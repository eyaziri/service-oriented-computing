package com.smart_tourism.smart_tourism.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {

        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "culturalArchive")
    public DefaultWsdl11Definition wsdlDefinition(XsdSchema culturalArchiveSchema) {

        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("CulturalArchivePort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://smarttourism.com/soap");
        wsdl.setSchema(culturalArchiveSchema);

        return wsdl;
    }

    @Bean
    public XsdSchema culturalArchiveSchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/cultural-archive.xsd"));
    }
}
