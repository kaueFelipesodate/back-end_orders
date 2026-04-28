package br.com.cotefacil_api2.modules.orders.controller;

import br.com.cotefacil_api2.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api2.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api2.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api2.modules.orders.service.OrderService;
import br.com.cotefacil_api2.shared.web.responses.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Orders", description = "CRUD for orders and items in API 2.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    @Operation(summary = "Order list", description = "Returns paginated orders.")
    public Response findAllOrders(@ModelAttribute PageRequestParams params) {
        return Response.success(orderService.findAllOrders(params));
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Search order by ID.", description = "Returns a specific order.")
    public Response findOrderById(@PathVariable Long id) {
        return Response.success(orderService.findOrderById(id));
    }

    @PostMapping("/orders")
    @Operation(summary = "Create order", description = "Create new order.")
    public Response createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        orderService.createOrder(orderDTO);
        return Response.success("Order create with success.");
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Update order", description = "Update new order.")
    public Response updateOrder(@PathVariable Long id, @RequestBody @Valid OrderDTO orderDTO) {
        orderService.updateOrder(id, orderDTO);
        return Response.success("Order updated with success.");
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Delete order", description = "Delete new order.")
    public Response deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return Response.success("Order deleted with success.");
    }

    @GetMapping("/orders/{id}/items")
    @Operation(summary = "List of items in the order", description = "Returns the items from an order.")
    public Response findAllOrdersItemByIdOrder(@PathVariable Long id) {
        return Response.success(orderService.findAllOrdersItemByIdOrder(id));
    }

    @PostMapping("/orders/{id}/items")
    @Operation(summary = "Add item to order", description = "Add an item to an order.")
    public Response addItemsOrder(@PathVariable Long id, @RequestBody @Valid OrderItemDTO orderItemDTO) {
        orderService.addItemsOrder(id, orderItemDTO);
        return Response.success("Item add with success.");
    }
}
