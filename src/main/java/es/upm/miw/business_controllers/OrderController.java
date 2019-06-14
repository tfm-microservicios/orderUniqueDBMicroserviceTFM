package es.upm.miw.business_controllers;

import es.upm.miw.business_services.RestBuilder;
import es.upm.miw.business_services.RestService;
import es.upm.miw.documents.Order;
import es.upm.miw.documents.OrderLine;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import es.upm.miw.dtos.out.OrderMinimumValidationInputDto;
import es.upm.miw.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    public static final String PROVIDERS_ARTICLES_VALIDATION = "/providers/validate-presence";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestService restService;

    @Autowired
    private Environment environment;

    @Value("${article.provider.microservice}")
    private String articleProviderURI;


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

    public OrderDto create(OrderDto orderDto, String token) {
        this.restService.setToken(token).restBuilder(new RestBuilder<>())
                .heroku().serverUri(articleProviderURI)
                .path(PROVIDERS_ARTICLES_VALIDATION).body(Arrays.asList(new OrderMinimumValidationInputDto(orderDto)))
                .post().log().build();
        Order order = orderRepository.save(orderDto.prepareOrder());
        return new OrderDto(order);
    }

    public void delete(String id) {
        Optional<Order> order = this.orderRepository.findById(id);
        if (order.isPresent()) {
            this.orderRepository.delete(order.get());
        }
    }

    private void createAddOrderSearchDto(OrderDto dto, OrderLine orderLine) {
        OrderSearchDto orderSearchDto;
        orderSearchDto = new OrderSearchDto(dto.getId(), dto.getDescription(), orderLine.getArticleId(),
                orderLine.getRequiredAmount(), orderLine.getFinalAmount(), dto.getOpeningDate(), dto.getClosingDate());
        orderSearchDtos.add(orderSearchDto);
    }

}
