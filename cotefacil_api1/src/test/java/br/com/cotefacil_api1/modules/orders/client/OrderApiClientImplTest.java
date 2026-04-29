package br.com.cotefacil_api1.modules.orders.client;

import br.com.cotefacil_api1.modules.orders.client.impl.OrderApiClientImpl;
import br.com.cotefacil_api1.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api1.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.shared.web.responses.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApiClientImplTest {

    @Mock
    private RestTemplate api2RestTemplate;

    @InjectMocks
    private OrderApiClientImpl orderApiClient;

    @Test
    void findAllOrders_deveMontarUriComParametros_quandoFiltrosForemInformados() {
        ReflectionTestUtils.setField(orderApiClient, "api2BaseUrl", "http://localhost:8081");
        when(api2RestTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(Response.class)))
                .thenReturn(ResponseEntity.ok(Response.success("ok")));

        PageRequestParams params = PageRequestParams.builder()
                .page(2)
                .size(15)
                .orderBy("createdDate")
                .orderDir("DESC")
                .filters(Map.of("status", "PENDING"))
                .build();

        ResponseEntity<Response> response = orderApiClient.findAllOrders(params, "Bearer token");

        assertEquals(200, response.getStatusCode());
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(api2RestTemplate).exchange(uriCaptor.capture(), eq(HttpMethod.GET), entityCaptor.capture(), eq(Response.class));
        assertEquals("http://localhost:8081/api/orders?page=2&size=15&orderBy=createdDate&orderDir=DESC&filters[status]=PENDING",
                uriCaptor.getValue().toString());
        assertEquals("Bearer token", entityCaptor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void createOrder_deveEnviarBodyEHeader_quandoChamado() {
        ReflectionTestUtils.setField(orderApiClient, "api2BaseUrl", "http://localhost:8081");
        when(api2RestTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(Response.class)))
                .thenReturn(ResponseEntity.ok(Response.success("ok")));

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerName("Cliente");
        orderDTO.setCustomerEmail("cliente@email.com");

        orderApiClient.createOrder(orderDTO, "Bearer token");

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(api2RestTemplate).exchange(any(URI.class), eq(HttpMethod.POST), entityCaptor.capture(), eq(Response.class));
        assertEquals(orderDTO, entityCaptor.getValue().getBody());
        assertEquals("Bearer token", entityCaptor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void addItemsOrder_deveEnviarItem_quandoChamado() {
        ReflectionTestUtils.setField(orderApiClient, "api2BaseUrl", "http://localhost:8081");
        when(api2RestTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(Response.class)))
                .thenReturn(ResponseEntity.ok(Response.success("ok")));

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductName("Produto");

        orderApiClient.addItemsOrder(10L, itemDTO, null);

        ArgumentCaptor<HttpEntity<?>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(api2RestTemplate).exchange(any(URI.class), eq(HttpMethod.POST), entityCaptor.capture(), eq(Response.class));
        assertEquals(itemDTO, entityCaptor.getValue().getBody());
    }
}
