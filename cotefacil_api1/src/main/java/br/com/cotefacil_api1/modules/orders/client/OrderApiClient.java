package br.com.cotefacil_api1.modules.orders.client;

import br.com.cotefacil_api1.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api1.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.shared.web.responses.Response;
import org.springframework.http.ResponseEntity;

public interface OrderApiClient {

    ResponseEntity<Response> findAllOrders(PageRequestParams params, String authorization);

    ResponseEntity<Response> findOrderById(Long id, String authorization);

    ResponseEntity<Response> createOrder(OrderDTO orderDTO, String authorization);

    ResponseEntity<Response> updateOrder(Long id, OrderDTO orderDTO, String authorization);

    ResponseEntity<Response> deleteOrder(Long id, String authorization);

    ResponseEntity<Response> findAllOrdersItemByIdOrder(Long id, String authorization);

    ResponseEntity<Response> addItemsOrder(Long id, OrderItemDTO orderItemDTO, String authorization);
}
