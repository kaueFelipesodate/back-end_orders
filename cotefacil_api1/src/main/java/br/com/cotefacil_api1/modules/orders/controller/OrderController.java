package br.com.cotefacil_api1.modules.orders.controller;

import br.com.cotefacil_api1.modules.orders.client.OrderApiClient;
import br.com.cotefacil_api1.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api1.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api1.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api1.shared.web.responses.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Orders", description = "Order proxy exposed by API 1.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApiClient orderApiClient;

    @GetMapping("/orders")
    @Operation(summary = "Order list", description = "Forwards the paginated list of orders to API 2.")
    public ResponseEntity<Response> findAllOrders(
            @ModelAttribute PageRequestParams params,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.findAllOrders(params, authorization);
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Search order by ID.", description = "Forwards a specific order query to API 2.")
    public ResponseEntity<Response> findOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.findOrderById(id, authorization);
    }

    @PostMapping("/orders")
    @Operation(summary = "Create order", description = "Forwards the creation of requests to API 2.")
    public ResponseEntity<Response> createOrder(
            @RequestBody @Valid OrderDTO orderDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.createOrder(orderDTO, authorization);
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Update order", description = "Forwards order updates to API 2.")
    public ResponseEntity<Response> updateOrder(
            @PathVariable Long id,
            @RequestBody @Valid OrderDTO orderDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.updateOrder(id, orderDTO, authorization);
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Delete order", description = "Forward the order deletion to API 2.")
    public ResponseEntity<Response> deleteOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.deleteOrder(id, authorization);
    }

    @GetMapping("/orders/{id}/items")
    @Operation(summary = "List of items in the order", description = "Forwards the item query for an order to API 2.")
    public ResponseEntity<Response> findAllOrdersItemByIdOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.findAllOrdersItemByIdOrder(id, authorization);
    }

    @PostMapping("/orders/{id}/items")
    @Operation(summary = "Add item to order", description = "Forward the item addition to API 2.")
    public ResponseEntity<Response> addItemsOrder(
            @PathVariable Long id,
            @RequestBody @Valid OrderItemDTO orderItemDTO,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return orderApiClient.addItemsOrder(id, orderItemDTO, authorization);
    }
}
