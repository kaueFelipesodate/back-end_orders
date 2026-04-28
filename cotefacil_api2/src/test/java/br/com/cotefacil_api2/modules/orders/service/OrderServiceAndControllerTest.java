package br.com.cotefacil_api2.modules.orders.service;

import br.com.cotefacil_api2.modules.orders.controller.OrderController;
import br.com.cotefacil_api2.modules.orders.dto.OrderDTO;
import br.com.cotefacil_api2.modules.orders.dto.OrderItemDTO;
import br.com.cotefacil_api2.modules.orders.dto.PageRequestParams;
import br.com.cotefacil_api2.modules.orders.dto.PageResponse;
import br.com.cotefacil_api2.modules.orders.enums.OrderStatus;
import br.com.cotefacil_api2.modules.orders.model.Order;
import br.com.cotefacil_api2.modules.orders.model.OrderItem;
import br.com.cotefacil_api2.modules.orders.repository.OrderRepository;
import br.com.cotefacil_api2.modules.orders.service.impl.OrderServiceImpl;
import br.com.cotefacil_api2.shared.exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceAndControllerTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    private OrderController orderController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository);
        orderController = new OrderController(orderService);
    }

    @Test
    void findAllOrders_deveRetornarPaginaConvertida_quandoNaoHouverFiltros() {
        Order order = novoOrder(1L, "Cliente", "cliente@email.com", OrderStatus.PENDING);
        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
        when(orderRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), org.mockito.ArgumentMatchers.<Pageable>any()))
                .thenReturn(page);

        PageResponse<OrderDTO> response = orderService.findAllOrders(null);

        assertEquals(1, response.getList().size());
        assertEquals("Cliente", response.getList().get(0).getCustomerName());
        verify(orderRepository).findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), org.mockito.ArgumentMatchers.<Pageable>any());
    }

    @Test
    void findOrderById_deveLancarExcecao_quandoPedidoNaoExistir() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> orderService.findOrderById(99L));
    }

    @Test
    void createOrder_deveSalvarEntidade_quandoDTOForValido() {
        OrderDTO dto = novoOrderDto();

        orderService.createOrder(dto);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals("Cliente", captor.getValue().getCustomerName());
        assertEquals(new BigDecimal("20.00"), captor.getValue().getTotalAmount());
    }

    @Test
    void updateOrder_deveAtualizarESalvar_quandoPedidoExistir() {
        Order existing = novoOrder(10L, "Cliente Antigo", "antigo@email.com", OrderStatus.PENDING);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

        OrderDTO dto = novoOrderDto();
        dto.setCustomerName("Cliente Atualizado");

        orderService.updateOrder(10L, dto);

        verify(orderRepository).save(existing);
        assertEquals("Cliente Atualizado", existing.getCustomerName());
    }

    @Test
    void deleteOrder_deveRemover_quandoPedidoExistir() {
        Order existing = novoOrder(10L, "Cliente", "cliente@email.com", OrderStatus.PENDING);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

        orderService.deleteOrder(10L);

        verify(orderRepository).delete(existing);
    }

    @Test
    void addItemsOrder_deveCalcularSubtotalEAtualizarTotal_quandoItemNovoForAdicionado() {
        Order existing = novoOrder(10L, "Cliente", "cliente@email.com", OrderStatus.PENDING);
        existing.getItems().clear();
        when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductName("Produto");
        itemDTO.setQuantity(2);
        itemDTO.setUnitPrice(new BigDecimal("10.00"));
        itemDTO.setSubtotal(BigDecimal.ZERO);

        orderService.addItemsOrder(10L, itemDTO);

        assertEquals(1, existing.getItems().size());
        assertEquals(new BigDecimal("20.00"), existing.getTotalAmount());
        assertEquals(new BigDecimal("20.00"), existing.getItems().get(0).getSubtotal());
    }

    @Test
    void controller_deveDelegarOperacoes_quandoEndpointsForemChamados() {
        OrderDTO orderDTO = novoOrderDto();
        OrderItemDTO itemDTO = novoItemDto();

        when(orderRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Order>>any(), org.mockito.ArgumentMatchers.<Pageable>any()))
                .thenReturn(new PageImpl<>(List.of(novoOrder(1L, "Cliente", "cliente@email.com", OrderStatus.PENDING))));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(novoOrder(1L, "Cliente", "cliente@email.com", OrderStatus.PENDING)));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(novoOrder(2L, "Cliente", "cliente@email.com", OrderStatus.PENDING)));

        assertTrue(orderController.findAllOrders(new PageRequestParams()).getSuccess());
        assertTrue(orderController.findOrderById(1L).getSuccess());
        assertTrue(orderController.createOrder(orderDTO).getSuccess());
        assertTrue(orderController.updateOrder(1L, orderDTO).getSuccess());
        assertTrue(orderController.deleteOrder(1L).getSuccess());
        assertTrue(orderController.findAllOrdersItemByIdOrder(2L).getSuccess());
        assertTrue(orderController.addItemsOrder(2L, itemDTO).getSuccess());
    }

    private OrderDTO novoOrderDto() {
        OrderItemDTO itemDTO = novoItemDto();

        OrderDTO dto = new OrderDTO();
        dto.setCustomerName("Cliente");
        dto.setCustomerEmail("cliente@email.com");
        dto.setOrderDate(LocalDateTime.of(2024, 1, 10, 12, 0));
        dto.setStatus(OrderStatus.PENDING);
        dto.setItems(List.of(itemDTO));
        return dto;
    }

    private OrderItemDTO novoItemDto() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductName("Produto");
        dto.setQuantity(2);
        dto.setUnitPrice(new BigDecimal("10.00"));
        dto.setSubtotal(new BigDecimal("20.00"));
        return dto;
    }

    private Order novoOrder(Long id, String nome, String email, OrderStatus status) {
        OrderItem item = new OrderItem();
        item.setProductName("Produto");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        item.setSubtotal(new BigDecimal("20.00"));

        Order order = new Order();
        order.setId(id);
        order.setCustomerName(nome);
        order.setCustomerEmail(email);
        order.setOrderDate(LocalDateTime.of(2024, 1, 10, 12, 0));
        order.setStatus(status);
        order.setItems(new java.util.ArrayList<>(List.of(item)));
        order.setTotalAmount(new BigDecimal("20.00"));
        item.setOrder(order);
        return order;
    }
}
