package br.com.cotefacil_api1.modules.orders.client.impl;

import br.com.cotefacil_api1.modules.orders.client.OrderApiClient;
import br.com.cotefacil_api1.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api1.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.shared.web.responses.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderApiClientImpl implements OrderApiClient {

    private static final String ORDERS_PATH = "/api/orders";

    private final RestTemplate api2RestTemplate;
    @Value("${api2.base-url}")
    private String api2BaseUrl;

    @Override
    public ResponseEntity<Response> findAllOrders(PageRequestParams params, String authorization) {
        return exchange(HttpMethod.GET, buildOrdersUri(params), null, authorization);
    }

    @Override
    public ResponseEntity<Response> findOrderById(Long id, String authorization) {
        return exchange(HttpMethod.GET, buildUri(ORDERS_PATH + "/" + id), null, authorization);
    }

    @Override
    public ResponseEntity<Response> createOrder(OrderDTO orderDTO, String authorization) {
        return exchange(HttpMethod.POST, buildUri(ORDERS_PATH), orderDTO, authorization);
    }

    @Override
    public ResponseEntity<Response> updateOrder(Long id, OrderDTO orderDTO, String authorization) {
        return exchange(HttpMethod.PUT, buildUri(ORDERS_PATH + "/" + id), orderDTO, authorization);
    }

    @Override
    public ResponseEntity<Response> deleteOrder(Long id, String authorization) {
        return exchange(HttpMethod.DELETE, buildUri(ORDERS_PATH + "/" + id), null, authorization);
    }

    @Override
    public ResponseEntity<Response> findAllOrdersItemByIdOrder(Long id, String authorization) {
        return exchange(HttpMethod.GET, buildUri(ORDERS_PATH + "/" + id + "/items"), null, authorization);
    }

    @Override
    public ResponseEntity<Response> addItemsOrder(Long id, OrderItemDTO orderItemDTO, String authorization) {
        return exchange(HttpMethod.POST, buildUri(ORDERS_PATH + "/" + id + "/items"), orderItemDTO, authorization);
    }

    private URI buildOrdersUri(PageRequestParams params) {
        PageRequestParams safeParams = params != null ? params : new PageRequestParams();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(api2BaseUrl)
                .path(ORDERS_PATH)
                .queryParam("page", safeParams.getPage())
                .queryParam("size", safeParams.getSize());

        if (safeParams.getOrderBy() != null && !safeParams.getOrderBy().isBlank()) {
            builder.queryParam("orderBy", safeParams.getOrderBy());
        }
        if (safeParams.getOrderDir() != null && !safeParams.getOrderDir().isBlank()) {
            builder.queryParam("orderDir", safeParams.getOrderDir());
        }
        if (safeParams.getFilters() != null) {
            for (Map.Entry<String, String> entry : safeParams.getFilters().entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    builder.queryParam("filters[" + entry.getKey() + "]", entry.getValue());
                }
            }
        }

        return builder.build().encode().toUri();
    }

    private URI buildUri(String path) {
        return UriComponentsBuilder.fromHttpUrl(api2BaseUrl)
                .path(path)
                .build()
                .toUri();
    }

    private ResponseEntity<Response> exchange(HttpMethod method, URI uri, Object body, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null && !authorization.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }

        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Response> exchange = api2RestTemplate.exchange(uri, method, entity, Response.class);
        return ResponseEntity
                .status(exchange.getStatusCode())
                .body(exchange.getBody());
    }
}
