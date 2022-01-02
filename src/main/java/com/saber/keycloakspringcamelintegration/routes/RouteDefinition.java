package com.saber.keycloakspringcamelintegration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RouteDefinition extends RouteBuilder {
	
	@Value(value = "${service.api.base-path}")
	private String apiBasePath;
	
	@Value(value = "${service.api.swagger.path}")
	private String swaggerPath;
	
	@Value(value = "${service.api.swagger.version}")
	private String swaggerVersion;
	
	@Value(value = "${service.api.swagger.title}")
	private String swaggerTitle;
	
	@Value(value = "${service.log.pretty-print}")
	private String prettyPrint;
	

	
	
	@Override
	public void configure() throws Exception {
		
		restConfiguration()
				.contextPath(apiBasePath)
				.apiContextPath(swaggerPath)
				.enableCORS(true)
				.bindingMode(RestBindingMode.json)
				.apiProperty("api.title",swaggerTitle)
				.apiProperty("api.version",swaggerVersion)
				.apiProperty("cors","true")
				.apiContextRouteId("doc-api")
				.component("servlet")
				.dataFormatProperty("prettyPrint",prettyPrint);
	}
}
