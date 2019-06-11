package es.upm.miw.business_controllers;

import es.upm.miw.documents.OrderLine;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import es.upm.miw.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    private List<OrderSearchDto> orderSearchDtos;


    public List<OrderSearchDto> readAll() {
        orderSearchDtos = new ArrayList<>();
        for (OrderDto dto : orderRepository.findAllOrdersByOpeningDateDesc()) {
            for (OrderLine orderLine : dto.getOrderLines()) {
                createAddOrderSearchDto(dto, orderLine);
            }
        }
        return orderSearchDtos;
    }

    public void createAddOrderSearchDto(OrderDto dto, OrderLine orderLine) {
        OrderSearchDto orderSearchDto;
        orderSearchDto = new OrderSearchDto(dto.getId(), dto.getDescription(), orderLine.getArticleId(),
                orderLine.getRequiredAmount(), orderLine.getFinalAmount(), dto.getOpeningDate(), dto.getClosingDate());
        orderSearchDtos.add(orderSearchDto);
    }
}
