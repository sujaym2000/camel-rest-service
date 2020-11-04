package com.camel.solution.rest.services;

import com.camel.solution.rest.dto.OrderDto;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    Map<String, OrderDto> orderStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        populateOrderStore();
    }


    public OrderDto getOrderByCustomerId(String customerId){
        return orderStore.get(customerId);
    }

    public void createOrder(OrderDto purchaseOrder){
        String customerId = purchaseOrder.getCustId();
        orderStore.put(customerId, purchaseOrder);
    }

    private void populateOrderStore(){
        String customerId = "c1b2a3";

        OrderDto order = new OrderDto();
        order.setPrdctId("mobile-apple");
        order.setQty(1);
        order.setOrdStatus("In-progress");
        order.setCustId(customerId);

        orderStore.put(customerId, order);
    }
}
