package com.challenge.healthcheck.config;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@Configuration
public class JerseyConfiguration extends ResourceConfig {
  public JerseyConfiguration(){
//	  register(AttachmentResource.class);
//      register(ContentResource.class);
      register(MultiPartFeature.class);
  }
  
}