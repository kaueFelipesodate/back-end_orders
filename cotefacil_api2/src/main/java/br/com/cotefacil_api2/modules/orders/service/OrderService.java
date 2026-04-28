package br.com.cotefacil_api2.modules.orders.service;

import br.com.cotefacil_api2.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api2.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api2.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api2.modules.orders.dto.PageResponse;

import java.util.List;

public interface OrderService {

    PageResponse<OrderDTO> findAllOrders(PageRequestParams params);

    OrderDTO findOrderById(Long id);

    void createOrder(OrderDTO orderDTO);

    void updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    List<OrderItemDTO> findAllOrdersItemByIdOrder(Long id);

    void addItemsOrder(Long id, OrderItemDTO orderItemDTO);
}
