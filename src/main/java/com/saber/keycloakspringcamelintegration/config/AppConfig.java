package com.saber.keycloakspringcamelintegration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.messagehistory.MetricsMessageHistoryFactory;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	@Value(value = "${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String tokenUrl;
	
	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		
		return mapper;
	}
	
	@Bean
	public CamelContextConfiguration camelContextConfiguration() {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
				camelContext.addRoutePolicyFactory(new MicrometerRoutePolicyFactory());
				camelContext.setMessageHistoryFactory(new MetricsMessageHistoryFactory());
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				SpringCamelContext springCamelContext = (SpringCamelContext) camelContext;
				for (RestDefinition restDefinition : springCamelContext.getRestDefinitions()) {
					restDefinition.security("auth.tiddev.modern")
							.securityDefinitions()
							.oauth2("auth.tiddev.modern")
							.password(tokenUrl)
							.end();
				}
			}
	
		};
	}
}

