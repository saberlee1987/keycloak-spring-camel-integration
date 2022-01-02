package com.saber.keycloakspringcamelintegration.routes;

import com.saber.keycloakspringcamelintegration.dto.HelloDto;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class HelloRoute extends AbstractRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        rest("/hello")
                .get("/sayHello")
                .id(Routes.SAY_HELLO_ROUTE)
                .description("Say Hello Route")
                .responseMessage().code(200).responseModel(HelloDto.class).endResponseMessage()
                .produces("application/xml,application/json")
                .param().name("firstName").type(RestParamType.query).dataType("string").required(true).example("saber").endParam()
                .param().name("lastName").type(RestParamType.query).dataType("string").required(true).example("Azizi").endParam()
                .enableCORS(true)
                .bindingMode(RestBindingMode.auto)
                .route()
                .routeId(Routes.SAY_HELLO_ROUTE)
                .routeGroup(Routes.SAY_HELLO_ROUTE_GROUP)
                .threads().threadName(Routes.SAY_HELLO_ROUTE)
                .to(String.format("direct:%s", Routes.SAY_HELLO_ROUTE_GATEWAY));

        from(String.format("direct:%s", Routes.SAY_HELLO_ROUTE_GATEWAY))
                .routeId(Routes.SAY_HELLO_ROUTE_GATEWAY)
                .routeGroup(Routes.SAY_HELLO_ROUTE_GROUP)
                .log("Request for sayHello firstName ==> ${in.header.firstName} , lastName ==> ${in.header.lastName}")
                .process(exchange -> {
                    String firstName = exchange.getIn().getHeader("firstName", String.class);
                    String lastName = exchange.getIn().getHeader("lastName", String.class);

                    String message = String.format("Hello %s %s", firstName, lastName);

                    HelloDto helloDto = new HelloDto();
                    helloDto.setMessage(message);
                    exchange.getIn().setBody(helloDto);
                })
                .log("content-type : ${in.header.accept}")
                .choice()
                     .when(header("accept").isEqualTo("application/xml"))
                        .to(String.format("direct:%s", Routes.PRODUCE_XML_ROUTE))
                     .otherwise()
                        .to(String.format("direct:%s", Routes.PRODUCE_JSON_ROUTE))
                .end();


        from(String.format("direct:%s", Routes.PRODUCE_XML_ROUTE))
                .marshal().jacksonxml(HelloDto.class)
                .log("Response for sayHello ===> ${in.body}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        from(String.format("direct:%s", Routes.PRODUCE_JSON_ROUTE))
                .marshal().json(JsonLibrary.Jackson)
                .log("Response for sayHello ===> ${in.body}")
                .unmarshal().json(JsonLibrary.Jackson)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));


    }
}
