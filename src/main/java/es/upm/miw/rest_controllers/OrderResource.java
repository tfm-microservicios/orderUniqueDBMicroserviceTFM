package es.upm.miw.rest_controllers;

import es.upm.miw.business_controllers.OrderController;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(OrderResource.ORDERS)

public class OrderResource {

    public static final String ORDERS = "/orders";
    public static final String ID = "/{id}";

    @Autowired
    private OrderController orderController;

    @GetMapping
    public List<OrderSearchDto> readAll() {
        return this.orderController.readAll();
    }

    @PostMapping
    public OrderDto create (@Valid @RequestBody OrderDto orderDto, @RequestHeader("Authorization") String token){
        return orderController.create(orderDto, token);
    }

    @DeleteMapping(value = ID)
    public void  delete (@PathVariable String id){
        this.orderController.delete(id);
    }

}