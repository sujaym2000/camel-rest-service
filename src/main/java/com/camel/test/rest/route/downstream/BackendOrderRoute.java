package com.camel.test.rest.route.downstream;

import com.camel.test.rest.dto.OrderDto;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BackendOrderRoute extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.off);

        rest()
            .get("/backend/customers/{customerId}/orders")
                .produces(MediaType.APPLICATION_XML_VALUE)
                .route()
                    .log("routing request to >> getOrders")
                    .to("direct:getOrders")
                .endRest();

        rest()
            .post("/backend/customers/orders")
                .consumes(MediaType.APPLICATION_XML_VALUE)
                .route()
                    .log("Create order requested ")
                    .to("direct:createOrder")
                    .log("Create order completed ")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.CREATED.value()))
                .setBody(simple(null))
                .endRest();

        from("direct:getOrders")
                .to("bean:orderService?method=getOrderByCustomerId(${header.customerId})")
                .marshal()
                .jacksonxml();

        from("direct:createOrder")
                .unmarshal()
                .jacksonxml(OrderDto.class)
                .to("bean:orderService?method=createOrder");
    }
}
