package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.OrderController;
import es.upm.miw.dtos.OrderSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(OrderResource.ORDERS)

public class OrderResource {

    public static final String ORDERS = "/orders";

    @Autowired
    private OrderController orderController;

    @GetMapping
    public List<OrderSearchDto> readAll() {
        return this.orderController.readAll();
    }

}