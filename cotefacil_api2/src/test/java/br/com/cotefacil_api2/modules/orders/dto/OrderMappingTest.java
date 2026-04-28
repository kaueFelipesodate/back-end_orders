package br.com.cotefacil_api2.modules.orders.dto;

import br.com.cotefacil_api2.modules.orders.enums.OrderStatus;
import br.com.cotefacil_api2.modules.orders.model.Order;
import br.com.cotefacil_api2.modules.orders.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderMappingTest {

    @Test
    void toEntity_deveCalcularSubtotal_quandoOrderItemDTONaoInformarValor() {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductName("Produto");
        dto.setQuantity(2);
        dto.setUnitPrice(new BigDecimal("10.50"));
        dto.setSubtotal(BigDecimal.ZERO);

        OrderItem entity = dto.toEntity();

        assertEquals(new BigDecimal("21.00"), entity.getSubtotal());
    }

    @Test
    void toDTO_deveCopiarCampos_quandoEntidadeForConvertida() {
        OrderItem entity = new OrderItem();
        entity.setId(2L);
        entity.setProductName("Produto");
        entity.setQuantity(3);
        entity.setUnitPrice(new BigDecimal("7.50"));
        entity.setSubtotal(new BigDecimal("22.50"));

        OrderItemDTO dto = OrderItemDTO.toDTO(entity);

        assertEquals(2L, dto.getId());
        assertEquals("Produto", dto.getProductName());
        assertEquals(3, dto.getQuantity());
        assertEquals(new BigDecimal("7.50"), dto.getUnitPrice());
        assertEquals(new BigDecimal("22.50"), dto.getSubtotal());
    }

    @Test
    void toEntity_deveMapearItensETotal_quandoOrderDTOForPreenchido() {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductName("Produto");
        itemDTO.setQuantity(2);
        itemDTO.setUnitPrice(new BigDecimal("10.00"));
        itemDTO.setSubtotal(new BigDecimal("20.00"));

        OrderDTO dto = new OrderDTO();
        dto.setId(99L);
        dto.setCustomerName("Cliente");
        dto.setCustomerEmail("cliente@email.com");
        dto.setOrderDate(LocalDateTime.of(2024, 1, 10, 12, 0));
        dto.setStatus(OrderStatus.CONFIRMED);
        dto.setItems(List.of(itemDTO));

        Order entity = dto.toEntity();

        assertNull(entity.getId());
        assertEquals("Cliente", entity.getCustomerName());
        assertEquals(1, entity.getItems().size());
        assertEquals(new BigDecimal("20.00"), entity.getTotalAmount());
        assertEquals(entity, entity.getItems().get(0).getOrder());
    }

    @Test
    void toDTO_deveMapearItens_quandoOrderPossuirItens() {
        OrderItem item = new OrderItem();
        item.setId(3L);
        item.setProductName("Produto");
        item.setQuantity(4);
        item.setUnitPrice(new BigDecimal("5.00"));
        item.setSubtotal(new BigDecimal("20.00"));

        Order order = new Order();
        order.setId(11L);
        order.setCustomerName("Cliente");
        order.setCustomerEmail("cliente@email.com");
        order.setOrderDate(LocalDateTime.of(2024, 1, 10, 12, 0));
        order.setStatus(OrderStatus.PENDING);
        order.setItems(List.of(item));
        order.setTotalAmount(new BigDecimal("20.00"));

        OrderDTO dto = OrderDTO.toDTO(order);

        assertEquals(11L, dto.getId());
        assertEquals("Cliente", dto.getCustomerName());
        assertEquals(1, dto.getItems().size());
        assertEquals("Produto", dto.getItems().get(0).getProductName());
        assertEquals(new BigDecimal("20.00"), dto.getTotalAmount());
    }

    @Test
    void loadFromEntity_deveAtualizarEntidade_quandoDTOForAplicado() {
        OrderDTO dto = new OrderDTO();
        dto.setCustomerName("Cliente Atualizado");
        dto.setCustomerEmail("novo@email.com");
        dto.setStatus(OrderStatus.DELIVERED);

        Order order = new Order();
        order.setId(44L);
        order.setCustomerName("Cliente Antigo");
        order.setCustomerEmail("antigo@email.com");
        order.setStatus(OrderStatus.PENDING);

        dto.loadFromEntity(order);

        assertEquals("Cliente Atualizado", order.getCustomerName());
        assertEquals("novo@email.com", order.getCustomerEmail());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
}
