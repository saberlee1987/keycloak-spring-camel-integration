package com.saber.keycloakspringcamelintegration.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data

@JacksonXmlRootElement(localName = "response")
public class HelloDto {
	@JacksonXmlProperty(localName = "message")
	private String message;
}
