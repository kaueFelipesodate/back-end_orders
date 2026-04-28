package br.com.cotefacil_api2.modules.orders.service.impl;

import br.com.cotefacil_api2.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api2.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api2.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api2.modules.orders.dto.PageResponse;
import br.com.cotefacil_api2.modules.orders.model.Order;
import br.com.cotefacil_api2.modules.orders.repository.OrderRepository;
import br.com.cotefacil_api2.modules.orders.service.OrderService;
import br.com.cotefacil_api2.shared.exceptions.ServiceException;
import br.com.cotefacil_api2.shared.utils.FilterSpecBuilder;
import br.com.cotefacil_api2.shared.utils.PageRequestUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    public static final String ORDER_NOT_FOUND = "Order not found.";

    private final OrderRepository orderRepository;

    @Override
    public PageResponse<OrderDTO> findAllOrders(PageRequestParams params) {
        if (params == null) {
            params = new PageRequestParams();
        }
        Pageable pageable = PageRequestUtil.toPageable(params, modelOrderMapping());
        Specification<Order> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (!CollectionUtils.isEmpty(params.getFilters())) {
            spec = spec.and(FilterSpecBuilder.build(params.getFilters(), null));
        }
        Page<OrderDTO> page = orderRepository.findAll(spec, pageable).map(OrderDTO::toDTO);

        return PageResponse.fromPage(page, params);
    }

    @Override
    public OrderDTO findOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDTO::toDTO)
                .orElseThrow(() -> new ServiceException(ORDER_NOT_FOUND));
    }

    @Override
    public void createOrder(OrderDTO orderDTO) {
        orderRepository.save(orderDTO.toEntity());
    }

    @Override
    public void updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(ORDER_NOT_FOUND));
        orderDTO.loadFromEntity(order);
        orderRepository.save(order);
        log.info("Order updated with success.");
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(ORDER_NOT_FOUND));
        orderRepository.delete(order);
        log.info("Order deleted with success.");
    }

    @Override
    public List<OrderItemDTO> findAllOrdersItemByIdOrder(Long id) {
        return findOrderById(id).getItems();
    }

    @Override
    public void addItemsOrder(Long id, OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ServiceException(ORDER_NOT_FOUND));
        var item = orderItemDTO.toEntity();
        item.setOrder(order);
        if (item.getSubtotal() == null || item.getSubtotal().compareTo(BigDecimal.ZERO) == 0) {
            item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.getItems().add(item);
        order.setTotalAmount(order.getItems().stream()
                .map(i -> i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        log.info("Item add with success.");
    }

    private static Map<String, String> modelOrderMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("createdDate", "createdDate");
        mapping.put("lastModifiedDate", "lastModifiedDate");
        return mapping;
    }
}
