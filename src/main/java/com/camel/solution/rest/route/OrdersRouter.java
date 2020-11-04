package com.camel.solution.rest.route;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class OrdersRouter extends RouteBuilder {

    @Value("${server.port}")
    private String port;

    private static String DOWN_STREAM_GET_URL = "http://localhost:%s/backend/customers/${header.customerId}/orders?bridgeEndpoint=true";
    private static String DOWN_STREAM_POST_URL = "http://localhost:%s/backend/customers/orders?bridgeEndpoint=true";

    @Override
    public void configure() {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off);

        XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
        xmlJsonFormat.setEncoding("UTF-8");
        xmlJsonFormat.setForceTopLevelObject(false);
        xmlJsonFormat.setTrimSpaces(true);
        xmlJsonFormat.setSkipNamespaces(true);
        xmlJsonFormat.setRemoveNamespacePrefixes(true);
        xmlJsonFormat.setRootName("orderDto");

        onException(Throwable.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setBody(simple("{\"message\": \"Server Error \"}"));

        onException(JsonValidationException.class)
                .log("${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .handled(true)
                .setBody(simple("{\"message\": \"Invalid order request\"}"));

        onException(InvalidFormatException.class)
                .log("${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .handled(true)
                .setBody(simple("{\"message\": \"Invalid order format\"}"));

        rest()
                .get("/customers/{customerId}/orders")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .log("routing request to >> getOrdersFromDownStream")
                .to("direct:getOrdersFromDownStream")
                .endRest();

        rest()
                .post("/customers/orders")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .log("routing request to >> createOrderToDownStream")
                .to("direct:createOrderToDownStream")
                .log("Create order request completed ")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.CREATED.value()))
                .setBody(simple("{\"message\": \"Order created successfully.\"}"))
                .endRest();

        from("direct:getOrdersFromDownStream")
                .log("Calling backend service to get orders for customer")
                .toD(String.format(DOWN_STREAM_GET_URL, port))
                .log("Received getOrders response from backend service")
                .marshal(xmlJsonFormat);

        from("direct:createOrderToDownStream")
                .to("json-validator:create-order-schema.json")
                .log("Create order request validated")
                .unmarshal(xmlJsonFormat)
                .log("Calling backend service for create order")
                .to(String.format(DOWN_STREAM_POST_URL, port));
    }

}
