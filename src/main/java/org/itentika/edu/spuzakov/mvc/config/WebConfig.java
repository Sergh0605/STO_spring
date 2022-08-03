package org.itentika.edu.spuzakov.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

/*	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {	
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper());
		converter.registerObjectMappersForType(ClientDTO.class, m->m.put(MediaType.APPLICATION_JSON, objectMapper()));
		converter.registerObjectMappersForType(ClientExDTO.class, m->m.put(MediaType.APPLICATION_JSON, objectExMapper()));
		converters.add(converter);
	}

	@Override
	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new ClientClientDTO());
		registry.addConverter(new ClientDTOClient());
		super.addFormatters(registry);
	}	*/
}
