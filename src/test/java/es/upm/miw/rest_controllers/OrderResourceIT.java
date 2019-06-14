package es.upm.miw.rest_controllers;

import es.upm.miw.business_services.RestBuilder;
import es.upm.miw.business_services.RestService;
import es.upm.miw.documents.Order;
import es.upm.miw.documents.OrderLine;
import es.upm.miw.dtos.OrderDto;
import es.upm.miw.dtos.OrderSearchDto;
import es.upm.miw.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ApiTestConfig
public class OrderResourceIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestService restService;

    @Autowired
    private Environment environment;

    @Value("${article.provider.microservice}")
    private String articleProviderURI;

    @BeforeEach
    void createOrder() {
        if (this.orderRepository.findAll().size() == 0) {
            String[] articlesId = {"1", "8400000000048", "8400000000024", "8400000000031"};
            String providerId = "5cfffb93525ceb1784133a24";
            String description = "ORDER-" + String.valueOf((int) (Math.random() * 10000));
            for (int i = 0; i < 3; i++) {
                OrderLine[] orderLines = org.assertj.core.util.Arrays.array(new OrderLine(articlesId[i], 4), new OrderLine(articlesId[i], 5));
                if (i == 2) {
                    description = "ORDER-02019";
                }
                Order order = new Order(description, providerId, orderLines);
                this.orderRepository.save(order);
            }
        }
    }

    @Test
    void testReadAll() {
        List<OrderSearchDto> orders = Arrays.asList(this.restService.loginAdmin()
                .restBuilder(new RestBuilder<OrderSearchDto[]>())
                .clazz(OrderSearchDto[].class)
                .path(OrderResource.ORDERS)
                .get()
                .build());
        assertNotNull(orders);
    }

    @Test
    void testCreatePass() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProviderId("5cf42a0c47d52400048bb572");
        orderDto.setOrderLines(org.assertj.core.util.Arrays.array(new OrderLine("1", 10)));
        OrderDto orderDtoResponse = this.restService.loginAdmin().restBuilder(new RestBuilder<OrderDto>()).clazz(OrderDto.class)
                .path(OrderResource.ORDERS).body(orderDto)
                .post().log().build();
        assertNotNull(this.orderRepository.findById(orderDtoResponse.getId()));
    }

    @Test
    void testCreateNotPass() {
        OrderDto orderDto = new OrderDto();
        orderDto.setProviderId("2");
        orderDto.setOrderLines(org.assertj.core.util.Arrays.array(new OrderLine("1", 10)));
        assertThrows(HttpClientErrorException.NotFound.class, () -> this.restService.loginAdmin().
                restBuilder(new RestBuilder<OrderDto>()).clazz(OrderDto.class)
                .path(OrderResource.ORDERS).body(orderDto)
                .post().log().build());
    }

    @Test
    void testDeleteOrder (){
        OrderDto orderDto = new OrderDto();
        orderDto.setProviderId("5cf42a0c47d52400048bb572");
        orderDto.setOrderLines(org.assertj.core.util.Arrays.array(new OrderLine("1", 10)));
        OrderDto orderDtoResponse = this.restService.loginAdmin().restBuilder(new RestBuilder<OrderDto>()).clazz(OrderDto.class)
                .path(OrderResource.ORDERS).body(orderDto)
                .post().log().build();
        assertNotNull(this.orderRepository.findById(orderDtoResponse.getId()));
        this.restService.loginAdmin().restBuilder(new RestBuilder<OrderDto>()).clazz(OrderDto.class).path(OrderResource.ORDERS)
                .path(OrderResource.ID).expand(orderDtoResponse.getId()).delete().log().build();
        assertNull(this.orderRepository.findById(orderDtoResponse.getId()).orElse(null));
    }
}
